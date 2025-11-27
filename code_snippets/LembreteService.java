/**
 * =============================================================================
 * ⏰ LembreteService.java - Scheduler Automático de Lembretes por E-mail
 * =============================================================================
 * 
 * Este serviço demonstra:
 * ✅ Uso do @Scheduled para tarefas agendadas
 * ✅ Separação de comportamento por ambiente com @Profile
 * ✅ Integração com Spring Mail via EmailService
 * ✅ Uso do Thymeleaf Context para templates de e-mail
 * ✅ Consultas otimizadas com JPA Repository
 * 
 * Tecnologias: Java 21, Spring Boot 3.3, Spring Mail, Thymeleaf
 * =============================================================================
 */

package com.petdoc.service;

import com.petdoc.model.Vacina;
import com.petdoc.repository.VacinaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Serviço responsável por enviar lembretes automáticos de reforço de vacinas.
 * 
 * Estratégia de notificação (Produção):
 * - D-15: Aviso antecipado de 15 dias
 * - D-7: Lembrete de 1 semana
 * - D-1: Último aviso (véspera do reforço)
 * 
 * Em desenvolvimento, roda a cada 30 minutos para facilitar testes.
 */
@Service
public class LembreteService {

    private static final Logger log = LoggerFactory.getLogger(LembreteService.class);
    
    private final VacinaRepository vacinaRepository;
    private final EmailService emailService;

    @Value("${app.dashboard.url}")
    private String dashboardUrl;

    public LembreteService(VacinaRepository vacinaRepository, EmailService emailService) {
        this.vacinaRepository = vacinaRepository;
        this.emailService = emailService;
    }

    // =========================================================================
    // SCHEDULERS POR AMBIENTE
    // =========================================================================

    /**
     * PRODUÇÃO: Executa diariamente às 8:00 AM.
     * 
     * Cron expression: "0 0 8 * * ?"
     * - Segundo 0, Minuto 0, Hora 8
     * - Todos os dias do mês, todos os meses, qualquer dia da semana
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Profile("prod")
    public void verificarLembretesProducao() {
        log.info("[PERFIL PROD] Iniciando verificação diária de lembretes...");
        
        // Executa verificação para cada período configurado
        executarVerificacao(15);  // D-15: Aviso antecipado
        executarVerificacao(7);   // D-7:  Lembrete de 1 semana
        executarVerificacao(1);   // D-1:  Véspera do reforço
        
        log.info("[PERFIL PROD] Verificação diária concluída.");
    }

    /**
     * DESENVOLVIMENTO: Executa a cada 30 minutos para facilitar testes.
     * 
     * fixedRate = 1800000ms = 30 minutos
     */
    @Scheduled(fixedRate = 1800000)
    @Profile("dev")
    public void verificarLembretesDesenvolvimento() {
        log.info("[PERFIL DEV] Verificando lembretes (a cada 30 minutos)...");
        executarVerificacao(0);  // Verifica reforços para HOJE
    }

    // =========================================================================
    // LÓGICA CENTRAL
    // =========================================================================

    /**
     * Lógica central que busca vacinas para uma data alvo e dispara e-mails.
     * 
     * @param diasDeAntecedencia Quantos dias à frente verificar (0 = hoje)
     */
    protected void executarVerificacao(int diasDeAntecedencia) {
        // Calcula a data alvo baseada nos dias de antecedência
        LocalDate dataAlvo = LocalDate.now().plusDays(diasDeAntecedencia);
        String tipoAviso = (diasDeAntecedencia == 0) 
            ? "HOJE" 
            : "em " + diasDeAntecedencia + " dias";

        log.info("Procurando por reforços agendados para {} (Aviso de {})", 
                 dataAlvo, tipoAviso);

        // Consulta otimizada que já traz Pet e Tutor via JOIN FETCH
        List<Vacina> vacinasParaLembrar = vacinaRepository
            .findByDataReforcoComPetETutor(dataAlvo);

        if (vacinasParaLembrar.isEmpty()) {
            log.info("Nenhum reforço de vacina encontrado para {}.", dataAlvo);
        } else {
            log.warn(">>> {} REFORÇO(S) ENCONTRADO(S) PARA {}:", 
                     vacinasParaLembrar.size(), dataAlvo);

            // Processa cada vacina encontrada
            for (Vacina vacina : vacinasParaLembrar) {
                enviarLembrete(vacina, tipoAviso);
            }
        }
    }

    /**
     * Processa e envia um único lembrete de reforço por e-mail.
     * 
     * @param vacina Entidade Vacina com relacionamentos Pet e Tutor
     * @param tipoAviso Texto descritivo do tipo de aviso ("HOJE", "em 7 dias", etc.)
     */
    private void enviarLembrete(Vacina vacina, String tipoAviso) {
        // Extrai dados necessários das entidades relacionadas
        String emailPara = vacina.getPet().getTutor().getEmail();
        String nomeTutor = vacina.getPet().getTutor().getNome();
        String nomePet = vacina.getPet().getNome();
        String nomeVacina = vacina.getNomeVacinaCompleto();
        String dataFormatada = vacina.getDataReforco()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        log.warn("   - LEMBRETE ({}): Vacina: {}, Pet: {}, Dono: {}",
                tipoAviso, nomeVacina, nomePet, emailPara);

        // Prepara o contexto Thymeleaf com variáveis para o template
        Context context = new Context();
        context.setVariable("nomeTutor", nomeTutor);
        context.setVariable("nomePet", nomePet);
        context.setVariable("nomeVacina", nomeVacina);
        context.setVariable("tipoAviso", tipoAviso.toLowerCase());
        context.setVariable("dataReforcoFormatada", dataFormatada);
        context.setVariable("dashboardUrl", this.dashboardUrl);
        context.setVariable("logoUrl", "cid:petdoc-logo.png");  // Content-ID para inline image

        // Monta o assunto do e-mail
        String assunto = String.format(
            "Lembrete de Reforço PetDoc: %s (%s)", nomePet, nomeVacina
        );

        // Delega o envio para o EmailService
        emailService.enviarEmailHtml(emailPara, assunto, "lembrete-vacina.html", context);
    }
}
