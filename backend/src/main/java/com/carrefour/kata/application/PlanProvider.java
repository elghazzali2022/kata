package com.carrefour.kata.application;

import com.carrefour.kata.domain.model.Plan;
import com.carrefour.kata.domain.model.PlanType;

public interface PlanProvider {

    Plan get(PlanType type);
}
