package com.tiendaya.catalogo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Habilita CORS para el API REST.
 *
 * Se usa allowedOriginPatterns("*") en lugar de una lista fija de orígenes
 * porque este proyecto está pensado para correr tanto en localhost:4200
 * (desarrollo local) como en las URLs dinámicas de GitHub Codespaces
 * (https://<nombre-codespace>-4200.app.github.dev), que cambian en cada
 * Codespace. Para un entorno de producción real, esto debería restringirse
 * a los orígenes específicos conocidos.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
