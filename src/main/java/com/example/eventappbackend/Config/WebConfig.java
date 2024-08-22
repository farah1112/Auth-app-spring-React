package com.example.eventappbackend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Assuming the uploaded-photos directory is directly under the project root
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}

