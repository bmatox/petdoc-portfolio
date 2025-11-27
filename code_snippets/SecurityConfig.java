/**
 * =============================================================================
 * 🔐 SecurityConfig.java - Configuração de Segurança Spring Security 6
 * =============================================================================
 * 
 * Este arquivo demonstra:
 * ✅ Configuração moderna do Spring Security 6 com Lambda DSL
 * ✅ Proteção CSRF com CookieCsrfTokenRepository
 * ✅ Configuração de CORS para integração com Frontend Vue.js
 * ✅ Controle de acesso baseado em Roles (ROLE_USER, ROLE_ADMIN)
 * ✅ Tratamento diferenciado para API REST vs páginas web
 * ✅ Autenticação session-based com formulário de login
 * 
 * Tecnologias: Java 21, Spring Boot 3.3, Spring Security 6
 * =============================================================================
 */

package com.petdoc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuração central de segurança da aplicação.
 * 
 * Estratégia:
 * - Session-based authentication (não JWT) para simplificar o MVP
 * - CSRF habilitado com cookie acessível pelo JavaScript do frontend
 * - CORS configurado para permitir requisições do Vue.js
 * - Roles: USER (usuários normais) e ADMIN (acesso ao Swagger)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    /**
     * Encoder de senhas usando BCrypt com custo padrão (10).
     * BCrypt já inclui salt automático em cada hash.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuração principal da cadeia de filtros de segurança.
     * 
     * Utiliza a nova API Lambda DSL do Spring Security 6.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // =========================================================
            // CSRF - Cross-Site Request Forgery Protection
            // =========================================================
            .csrf(csrf -> csrf
                // HttpOnly=false é INTENCIONAL: permite que o JavaScript do Vue.js
                // leia o token CSRF do cookie e o inclua nos headers das requisições.
                // Isso é seguro pois o token ainda precisa ser enviado de volta,
                // e políticas de SameSite protegem contra CSRF cross-site.
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // Desabilita CSRF para API REST (stateless por design)
                .ignoringRequestMatchers("/api/**")
                .ignoringRequestMatchers(new AntPathRequestMatcher("/logout"))
            )

            // =========================================================
            // CORS - Cross-Origin Resource Sharing
            // =========================================================
            // Habilita CORS para permitir requisições do frontend Vue.js
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // =========================================================
            // AUTORIZAÇÃO DE REQUISIÇÕES
            // =========================================================
            .authorizeHttpRequests(authorize -> authorize
                // == Recursos Públicos (Estáticos) ==
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico")
                    .permitAll()

                // == Páginas Públicas ==
                .requestMatchers("/login", "/cadastro")
                    .permitAll()

                // == API REST - Endpoints Públicos ==
                .requestMatchers("/api/auth/**")
                    .permitAll()

                // == API REST - Endpoints Protegidos (ROLE_USER) ==
                .requestMatchers("/api/dashboard/**").hasRole("USER")
                .requestMatchers("/api/racas/**").hasRole("USER")
                .requestMatchers("/api/pets/**").hasRole("USER")
                .requestMatchers("/api/perfil/**").hasRole("USER")

                // == Swagger UI - Apenas ADMIN ==
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                    .hasRole("ADMIN")

                // == Páginas Web (Thymeleaf) - Protegidas ==
                .requestMatchers("/dashboard", "/pets/**", "/vacinas/**", "/perfil/**", "/ajuda")
                    .hasRole("USER")

                // Qualquer outra requisição requer autenticação
                .anyRequest().authenticated()
            )

            // =========================================================
            // FORMULÁRIO DE LOGIN
            // =========================================================
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )

            // =========================================================
            // TRATAMENTO DE EXCEÇÕES DE AUTENTICAÇÃO
            // =========================================================
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    String requestURI = request.getRequestURI();
                    
                    // Se for uma requisição de API, retorna 401 JSON-friendly
                    if (requestURI.startsWith("/api/")) {
                        response.sendError(401, "Não autenticado");
                    } else {
                        // Se for uma página web, redireciona para login
                        response.sendRedirect("/login");
                    }
                })
            )

            // =========================================================
            // LOGOUT
            // =========================================================
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    /**
     * Configuração de CORS para permitir requisições do frontend Vue.js.
     * 
     * Permite:
     * - Origins configuráveis via application.properties
     * - Métodos HTTP padrão (GET, POST, PUT, DELETE, OPTIONS)
     * - Todos os headers
     * - Credenciais (cookies de sessão)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origins permitidas (configurável por ambiente)
        configuration.setAllowedOrigins(allowedOrigins);
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        
        // Headers permitidos (wildcard)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permite envio de cookies/credenciais
        configuration.setAllowCredentials(true);

        // Aplica configuração apenas para rotas de API
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}
