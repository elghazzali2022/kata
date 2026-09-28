package com.carrefour.kata.infrastructure.config;

import com.carrefour.kata.application.PlanProvider;
import com.carrefour.kata.domain.model.Plan;
import com.carrefour.kata.domain.model.PlanType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Map;

@Configuration
public class PlanConfiguration {

    @Bean
    PlanProvider planProvider() {
        Map<PlanType, Plan> plans = Map.of(
                PlanType.MONTHLY,
                new Plan(PlanType.MONTHLY, new BigDecimal("10.00"), 30),

                PlanType.QUARTERLY,
                new Plan(PlanType.QUARTERLY, new BigDecimal("27.00"), 90),

                PlanType.ANNUAL,
                new Plan(PlanType.ANNUAL, new BigDecimal("96.00"), 365)
        );

        return type -> plans.get(type);
    }
}
