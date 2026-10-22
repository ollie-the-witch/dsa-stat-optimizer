package org.olivia.test.project.data.config;

import org.olivia.test.project.data.enumerations.StatVariable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public record OptimizationHint(Set<StatVariable> mainHint, Set<StatVariable> subStatHint, Map<StatVariable, Integer> runeHint) {
    public static OptimizationHint defaultOptimizationHint() {
        Map<StatVariable, Integer> runes = new EnumMap<>(StatVariable.class);
        runes.put(StatVariable.ATTACK, 0);
        runes.put(StatVariable.CRITICAL_RATE, 0);
        runes.put(StatVariable.CRITICAL_DAMAGE, 5);
        return new OptimizationHint(EnumSet.of(StatVariable.ATTACK_POWER_UP), EnumSet.of(StatVariable.ATTACK, StatVariable.ATTACK_POWER_UP, StatVariable.CRITICAL_RATE, StatVariable.CRITICAL_DAMAGE), Map.copyOf(runes));
    }
}
