package org.olivia.test.project.data.config;

import org.olivia.test.project.data.enumerations.StatVariable;
import org.olivia.test.project.inputs.dto.CriticalBoundsTranslationDto;

import java.util.Map;

public record StatLimitConfiguration(Map<StatVariable, Integer> statCaps, Map<StatVariable, Integer> statLowerBounds, Map<StatVariable, Integer> statUpperBounds) {
    public static StatLimitConfiguration statBoundsConfiguration(CriticalBoundsTranslationDto criticalBoundsTranslationDto) {


        Integer cap = (int) Math.round(criticalBoundsTranslationDto.cap() * (OptimizationConfigurations.getScale() / 100.0));
        Integer min = (int) Math.round(criticalBoundsTranslationDto.minimum() * (OptimizationConfigurations.getScale() / 100.0));
        Integer max = (int) Math.round(criticalBoundsTranslationDto.maximum() * (OptimizationConfigurations.getScale() / 100.0));

        Map<StatVariable, Integer> statCaps = Map.of(StatVariable.CRITICAL_RATE, cap);
        Map<StatVariable, Integer> statLowerBounds = Map.of(StatVariable.CRITICAL_RATE, min);

        Map<StatVariable, Integer> statUpperBounds = Map.of(StatVariable.CRITICAL_RATE, max);

        return new StatLimitConfiguration(statCaps, statLowerBounds, statUpperBounds);
    }
}
