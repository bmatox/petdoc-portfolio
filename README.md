# 🐾 PetDoc - Plataforma SaaS de Gestão Veterinária

<div align="center">

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)](https://vuejs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![AWS App Runner](https://img.shields.io/badge/AWS%20App%20Runner-FF9900?style=for-the-badge&logo=amazon-aws&logoColor=white)](https://aws.amazon.com/apprunner/)

**Plataforma completa para gestão de carteiras de vacinação de pets com geração de documentos, lembretes automáticos e deploy contínuo na AWS.**

[🌐 Demo Online](https://6dfs8v3kpf.us-east-1.awsapprunner.com/login) • [📖 Documentação Técnica](#-stack-tecnológica) • [🎯 Roadmap 2026](#-roadmap-2026)

</div>

---

## 📋 Visão Geral

O **PetDoc** é um MVP de plataforma SaaS desenvolvido com **Java 21** e **Spring Boot 3.3**, demonstrando proficiência em:

- 🔄 **Arquitetura Full-Stack** com API REST + Frontend Vue.js SPA
- 📄 **Geração de Documentos PDF** dinâmicos com OpenHTMLToPDF
- 📧 **Integração SMTP** com templates HTML e agendamento automático
- ☁️ **Deploy Contínuo** na AWS com Docker e GitHub Actions
- 🔐 **Segurança** com Spring Security (Session-based + CSRF)

> **⚠️ Nota:** Este repositório é uma vitrine pública. O código-fonte completo é privado.

---

## 🖼️ GALERIA & EVIDÊNCIAS TÉCNICAS

Esta seção contém **evidências visuais** das funcionalidades implementadas no backend, provando que o sistema vai além de um simples CRUD.

### 📄 1. Geração de Documentos PDF

| Screenshot | Descrição Técnica |
|:----------:|:------------------|
| ![Carteira de Vacinação PDF](./screenshots/img.png) | **Carteira de Vacinação Digital em PDF** <br><br> ✅ *Implementação do **OpenHTMLToPDF** com Thymeleaf* <br> ✅ *Manipulação de streams binários (ByteArrayOutputStream)* <br> ✅ *Renderização de SVG com BatikSVGDrawer* <br> ✅ *Conversão de imagens para Base64 inline* <br><br> 📁 Código: [`code_snippets/CarteiraDigitalService.java`](./code_snippets/CarteiraDigitalService.java) |

<details>
<summary>💡 <strong>O que esse print prova?</strong></summary>

- Capacidade de gerar documentos PDF programaticamente
- Integração de template engine (Thymeleaf) com biblioteca de PDF
- Manipulação de recursos binários e streams em Java
- Conhecimento de conversão de formatos (HTML → PDF, Imagem → Base64)

</details>

---

### 📧 2. Integração SMTP com Scheduler Automático

| Screenshot | Descrição Técnica |
|:----------:|:------------------|
| ![E-mail de Lembrete](./screenshots/img_1.png) | **E-mail de Lembrete de Vacina** <br><br> ✅ *Integração **Spring Mail** com JavaMailSender* <br> ✅ *Templates HTML responsivos com Thymeleaf* <br> ✅ *Agendamento automático com `@Scheduled`* <br> ✅ *Lembretes em D-15, D-7 e D-1 (produção)* <br><br> 📁 Código: [`code_snippets/LembreteService.java`](./code_snippets/LembreteService.java) |

<details>
<summary>💡 <strong>O que esse print prova?</strong></summary>

- Implementação de sistema de notificações por e-mail
- Uso de Scheduler do Spring para tarefas automáticas
- Integração com serviços externos via SMTP
- Templates HTML profissionais com variáveis dinâmicas
- Tratamento diferenciado por ambiente (dev/prod com `@Profile`)

</details>

---

### 📊 3. Dashboard & Analytics

| Screenshot | Descrição Técnica |
|:----------:|:------------------|
| ![Dashboard com KPIs](./screenshots/screenshot1.png) | **Dashboard com KPIs e Grid de Pets** <br><br> ✅ *API REST servindo dados para Frontend Vue.js* <br> ✅ *Cálculo de métricas em tempo real* <br> ✅ *Filtros dinâmicos por espécie e busca* <br> ✅ *Contagem de vacinas vencidas e lembretes ativos* <br><br> 📁 Código: Ver `DashboardApiController.java` no repositório privado |

<details>
<summary>💡 <strong>O que esse print prova?</strong></summary>

- Desenvolvimento de API REST bem estruturada
- Integração Frontend-Backend com arquitetura moderna
- Cálculo de KPIs com consultas otimizadas ao banco
- Interface reativa com Vue.js consumindo API

</details>

---

### 📱 4. Responsividade Mobile

**Interface Responsiva (Mobile First)** <br>
✅ *SPA com Vue.js 3 responsiva* | ✅ *CSS Grid e Flexbox adaptativo* | ✅ *UX otimizada para touch devices*

| Tela 1 | Tela 2 | Tela 3 | Tela 4 |
|:---:|:---:|:---:|:---:|
| ![Mobile 1](./screenshots/img_2.png) | ![Mobile 2](./screenshots/img_3.png) | ![Mobile 3](./screenshots/img_4.png) | ![Mobile 4](./screenshots/img_5.png) |

<details>
<summary>💡 <strong>O que esse print prova?</strong></summary>

- Desenvolvimento frontend responsivo
- Aplicação de princípios Mobile-First
- Componentização de interface com Vue.js
- Preocupação com UX/UI em múltiplos dispositivos

</details>

---

## 🏛️ Arquitetura de Solução

### Diagrama de Arquitetura Completo

```mermaid
flowchart TB
    subgraph USUARIO["👤 Usuário Final"]
        Browser["🌐 Navegador Web"]
    end

    subgraph GITHUB["📦 GitHub"]
        Repo["📂 Repositório Privado"]
        Actions["⚙️ GitHub Actions CI/CD"]
    end

    subgraph AWS["☁️ Amazon Web Services"]
        subgraph ECR["📦 AWS ECR"]
            DockerImage["🐳 Docker Image"]
        end
        
        subgraph AppRunner["🚀 AWS App Runner"]
            Container["🐳 Container Java 21"]
            
            subgraph SpringBoot["🍃 Spring Boot 3.3"]
                direction TB
                Security["🔐 Spring Security"]
                WebControllers["🌐 Web Controllers"]
                RestAPI["📡 REST API"]
                Services["⚙️ Services"]
                Scheduler["⏰ Scheduler"]
                EmailService["📧 Email Service"]
                PDFService["📄 PDF Generator"]
                JPA["💾 Spring Data JPA"]
            end
        end
        
        subgraph RDS["🗄️ AWS RDS"]
            PostgreSQL["🐘 PostgreSQL 15"]
        end
    end

    subgraph EXTERNAL["🔗 Serviços Externos"]
        SMTP["📬 SMTP Server"]
    end

    Browser -->|"HTTPS"| AppRunner
    WebControllers -->|"HTML + Vue.js"| Browser
    Browser -->|"fetch() JSON"| RestAPI
    
    RestAPI --> Services
    WebControllers --> Services
    Services --> JPA
    JPA -->|"JDBC"| PostgreSQL
    
    Scheduler --> Services
    Services --> EmailService
    EmailService -->|"SMTP"| SMTP
    Services --> PDFService
    PDFService -->|"byte[]"| RestAPI

    Repo -->|"Push"| Actions
    Actions -->|"Docker Build"| DockerImage
    AppRunner -->|"Auto Deploy"| DockerImage

    style AWS fill:#FF9900,color:#232F3E
    style SpringBoot fill:#6DB33F,color:#fff
    style PostgreSQL fill:#336791,color:#fff
    style DockerImage fill:#2496ED,color:#fff
```

### Fluxo de Deploy Contínuo

```mermaid
sequenceDiagram
    participant Dev as 👨‍💻 Developer
    participant GH as 📦 GitHub
    participant CI as ⚙️ GitHub Actions
    participant ECR as 📦 AWS ECR
    participant AR as 🚀 App Runner

    Dev->>GH: git push (master)
    GH->>CI: Trigger Workflow
    CI->>CI: mvn clean install
    CI->>CI: docker build
    CI->>ECR: docker push
    ECR-->>AR: Nova imagem detectada
    AR->>AR: Deploy automático
    AR-->>Dev: ✅ Aplicação atualizada
```

### Fluxo do Scheduler de Lembretes

```mermaid
sequenceDiagram
    participant S as ⏰ Scheduler
    participant LS as 📋 LembreteService
    participant VR as 💾 VacinaRepository
    participant ES as 📧 EmailService
    participant SMTP as 📬 SMTP Server

    Note over S: @Scheduled(cron = "0 0 8 * * ?")
    S->>LS: verificarLembretesProducao()
    
    loop Para cada período (D-15, D-7, D-1)
        LS->>VR: findByDataReforcoComPetETutor(dataAlvo)
        VR-->>LS: List<Vacina>
        
        loop Para cada vacina
            LS->>LS: Preparar contexto Thymeleaf
            LS->>ES: enviarEmailHtml(para, assunto, template, context)
            ES->>ES: templateEngine.process()
            ES->>SMTP: javaMailSender.send()
            SMTP-->>ES: ✅ Enviado
        end
    end
```

---

## 💻 Stack Tecnológica

### Backend

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **Java** | 21 (LTS) | Linguagem principal com features modernas (Records, Pattern Matching) |
| **Spring Boot** | 3.3.0 | Framework principal |
| **Spring Security** | 6.x | Autenticação session-based com CSRF |
| **Spring Data JPA** | - | ORM com Hibernate |
| **Spring Mail** | - | Envio de e-mails SMTP |
| **Flyway** | - | Versionamento de banco de dados |
| **OpenHTMLToPDF** | 1.0.10 | Geração de PDFs |
| **Thymeleaf** | - | Templates (HTML + PDF + E-mail) |
| **SpringDoc OpenAPI** | 2.5.0 | Documentação Swagger |
| **PostgreSQL** | 15 | Banco de dados relacional |
| **Lombok** | - | Redução de boilerplate |
| **JaCoCo** | 0.8.11 | Cobertura de testes (mín. 50%) |

### Frontend

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **Vue.js** | 3.5.13 | Framework JavaScript (via CDN) |
| **Font Awesome** | 6.5.2 | Ícones |
| **CSS3** | - | Estilização responsiva |

### DevOps & Infra

| Tecnologia | Propósito |
|------------|-----------|
| **Docker** | Containerização (multi-stage build) |
| **GitHub Actions** | CI/CD Pipeline |
| **AWS App Runner** | PaaS para containers |
| **AWS ECR** | Registro de imagens Docker |
| **AWS RDS** | PostgreSQL gerenciado |

---

## 📂 Código Fonte - Snippets Selecionados

Esta pasta contém trechos de código **reais** do sistema, demonstrando qualidade e boas práticas.

| Arquivo | Descrição | Conceitos Demonstrados |
|---------|-----------|------------------------|
| [`CarteiraDigitalService.java`](./code_snippets/CarteiraDigitalService.java) | Geração de PDF | OpenHTMLToPDF, Streams, Base64, Thymeleaf |
| [`LembreteService.java`](./code_snippets/LembreteService.java) | Scheduler de Lembretes | @Scheduled, @Profile, Spring Mail, Thymeleaf Context |
| [`SecurityConfig.java`](./code_snippets/SecurityConfig.java) | Configuração de Segurança | Spring Security 6, CORS, CSRF, Roles |

---

## 🎯 Roadmap 2026

### Q1 2026 - Evolução do MVP

- [ ] **Multi-tenancy** - Suporte a clínicas veterinárias como organizações
- [ ] **Notificações Push** - Integração com Firebase Cloud Messaging
- [ ] **API Pública** - Documentação OpenAPI para integrações externas
- [ ] **Testes E2E** - Implementação com Playwright/Cypress

### Q2 2026 - Novas Funcionalidades

- [ ] **Agenda de Consultas** - Sistema de agendamento com veterinários
- [ ] **Prontuário Eletrônico** - Histórico médico completo do pet
- [ ] **Integração WhatsApp** - Notificações via WhatsApp Business API
- [ ] **Relatórios Avançados** - Analytics com gráficos interativos

### Q3 2026 - Escalabilidade

- [ ] **Microservices** - Separação do serviço de e-mails e PDFs
- [ ] **Cache Distribuído** - Redis para sessões e cache
- [ ] **CDN** - CloudFront para assets estáticos
- [ ] **Observabilidade** - APM com AWS X-Ray ou Datadog

### Q4 2026 - Monetização

- [ ] **Planos Premium** - Features avançadas por assinatura
- [ ] **Marketplace** - Integração com petshops e clínicas
- [ ] **App Mobile** - PWA ou aplicativo nativo
- [ ] **Internacionalização** - Suporte a múltiplos idiomas

---

## 👨‍💻 Autor

**Bruno Matos** - Desenvolvedor Full Stack

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/bmatox)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/bmatox)

---

<div align="center">

**⭐ Se este projeto demonstrou valor, considere deixar uma estrela!**

</div>
