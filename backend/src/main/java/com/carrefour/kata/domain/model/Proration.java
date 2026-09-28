package com.carrefour.kata.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class Proration {

    private static final int CALCULATION_SCALE = 8;
    private static final int MONEY_SCALE = 2;

    private Proration() {
    }

    public static BigDecimal calculate(
            Plan currentPlan,
            Plan newPlan,
            LocalDate startDate,
            LocalDate nextBillingDate,
            LocalDate changeDate
    ) {
        if (currentPlan.type() == newPlan.type()) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE);
        }

        long totalRemainingPeriodDays =
                ChronoUnit.DAYS.between(startDate, nextBillingDate);

        long remainingDays =
                ChronoUnit.DAYS.between(changeDate, nextBillingDate);

        if (totalRemainingPeriodDays <= 0 || remainingDays <= 0) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE);
        }

        BigDecimal currentRemainingValue = dailyPrice(currentPlan)
                .multiply(BigDecimal.valueOf(remainingDays));

        BigDecimal newPlanRemainingValue = dailyPrice(newPlan)
                .multiply(BigDecimal.valueOf(remainingDays));

        return newPlanRemainingValue
                .subtract(currentRemainingValue)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal dailyPrice(Plan plan) {
        return plan.price()
                .divide(
                        BigDecimal.valueOf(plan.durationInDays()),
                        CALCULATION_SCALE,
                        RoundingMode.HALF_UP
                );
    }
}
