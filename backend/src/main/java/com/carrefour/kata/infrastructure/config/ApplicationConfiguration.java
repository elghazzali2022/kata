package com.carrefour.kata.infrastructure.config;

import com.carrefour.kata.application.PlanProvider;
import com.carrefour.kata.application.SubscriptionService;
import com.carrefour.kata.domain.repository.SubscriptionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;

@Configuration
public class ApplicationConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    SubscriptionService subscriptionService(
            SubscriptionRepository repository,
            PlanProvider planProvider,
            Clock clock
    ) {
        return new SubscriptionService(repository, planProvider, clock);
    }

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "OPTIONS"
                        )
                        .allowedHeaders("*");
            }
        };
    }
}