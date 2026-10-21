package org.olivia.test.project.data.config;

import org.olivia.test.project.data.enumerations.StatVariable;

import java.util.EnumSet;
import java.util.Set;

public record OptimizationHint(Set<StatVariable> mainHint, Set<StatVariable> subStatHint) {
    public static OptimizationHint defaultOptimizationHint() {
        return new OptimizationHint(EnumSet.of(StatVariable.ATTACK_POWER_UP), EnumSet.of(StatVariable.ATTACK_POWER_UP, StatVariable.CRITICAL_RATE, StatVariable.CRITICAL_DAMAGE));
    }
}
