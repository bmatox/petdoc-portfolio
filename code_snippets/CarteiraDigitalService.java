/**
 * =============================================================================
 * 📄 CarteiraDigitalService.java - Geração de PDF com OpenHTMLToPDF
 * =============================================================================
 * 
 * Este serviço demonstra:
 * ✅ Integração do Spring com biblioteca OpenHTMLToPDF
 * ✅ Uso do Thymeleaf para renderização de templates HTML
 * ✅ Manipulação de streams binários (ByteArrayOutputStream)
 * ✅ Conversão de imagens para Base64 inline
 * ✅ Renderização de SVG com BatikSVGDrawer
 * 
 * Tecnologias: Java 21, Spring Boot 3.3, OpenHTMLToPDF 1.0.10, Thymeleaf
 * =============================================================================
 */

package com.petdoc.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import com.petdoc.dto.api.pet.CarteiraDigitalDTO;
import com.petdoc.model.Pet;
import com.petdoc.model.Vacina;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * Service responsável pela geração de PDF da Carteira Digital de Vacinação.
 * 
 * Fluxo de geração:
 * 1. Recebe dados do Pet e suas Vacinas
 * 2. Converte para DTO específico da carteira
 * 3. Carrega logo como Base64 para embedding inline
 * 4. Processa template Thymeleaf para HTML
 * 5. Converte HTML para PDF usando OpenHTMLToPDF
 * 6. Retorna array de bytes para download
 */
@Service
public class CarteiraDigitalService {

    private final TemplateEngine templateEngine;

    public CarteiraDigitalService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Gera a carteira digital de vacinação em formato PDF.
     * 
     * @param pet O pet para o qual a carteira será gerada
     * @param vacinas Lista de vacinas aplicadas no pet
     * @param tutorNome Nome do tutor (dono) do pet
     * @return byte[] contendo o PDF gerado
     */
    public byte[] gerarCarteiraPDF(Pet pet, List<Vacina> vacinas, String tutorNome) {

        // 1. Transforma entidades em DTO específico para a carteira
        CarteiraDigitalDTO dto = CarteiraDigitalDTO.fromPet(pet, vacinas, tutorNome);

        // 2. Carrega a logo como Base64 para embedding inline no HTML
        //    Isso evita problemas de caminho relativo na renderização do PDF
        String logoBase64 = carregarImagemBase64("static/images/petdoc-logo_compressed2.png");

        // 3. Prepara o contexto do Thymeleaf com as variáveis do template
        Context context = new Context();
        context.setVariable("carteira", dto);
        context.setVariable("logoPetDoc", logoBase64);

        // 4. Processa o template HTML usando Thymeleaf
        String html = templateEngine.process("carteira/carteira-digital", context);

        // 5. Converte o HTML em PDF
        return convertHtmlToPdf(html);
    }

    /**
     * Carrega uma imagem do Classpath (resources) e converte para string Base64 
     * pronta para uso em src de tag <img> no HTML.
     * 
     * Formato retornado: "data:image/png;base64,iVBORw0KGg..."
     * 
     * @param caminhoResource Caminho relativo a partir de src/main/resources
     * @return String Base64 com prefixo data URI ou string vazia se não encontrar
     */
    private String carregarImagemBase64(String caminhoResource) {
        try {
            ClassPathResource resource = new ClassPathResource(caminhoResource);
            if (resource.exists()) {
                byte[] imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                
                // Detecta o tipo MIME baseado na extensão
                String mimeType = caminhoResource.endsWith(".jpg") || 
                                  caminhoResource.endsWith(".jpeg") 
                                  ? "image/jpeg" 
                                  : "image/png";
                
                return "data:" + mimeType + ";base64," + base64;
            }
        } catch (IOException e) {
            log.error("Erro ao carregar imagem para Base64: " + e.getMessage(), e);
        }
        return "";
    }

    /**
     * Converte HTML para PDF usando OpenHTMLToPDF.
     * 
     * Características:
     * - Modo rápido habilitado para melhor performance
     * - Suporte a SVG via BatikSVGDrawer
     * - Renderização em memória (ByteArrayOutputStream)
     * 
     * @param html String contendo HTML válido
     * @return byte[] contendo o PDF renderizado
     * @throws RuntimeException se houver erro na conversão
     */
    private byte[] convertHtmlToPdf(String html) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();

            // Modo rápido: desabilita algumas features para ganho de performance
            builder.useFastMode();

            // Habilita suporte a SVG para ícones e gráficos
            builder.useSVGDrawer(new BatikSVGDrawer());
            
            // Define o conteúdo HTML e a base URI para recursos relativos
            builder.withHtmlContent(html, "/");
            
            // Define o stream de saída
            builder.toStream(os);
            
            // Executa a renderização
            builder.run();
            
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                "Erro ao gerar PDF da carteira digital: " + e.getMessage(), e
            );
        }
    }
}
