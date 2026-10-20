package org.olivia.test.project.data.config;

import org.olivia.test.project.data.enumerations.StatVariable;
import org.olivia.test.project.inputs.dto.UserInputsDto;

import java.util.Map;

public record StatLimitConfiguration(Map<StatVariable, Integer> statCaps, Map<StatVariable, Integer> statLowerBounds, Map<StatVariable, Integer> statUpperBounds) {
    public static StatLimitConfiguration statBoundsConfiguration(Integer criticalRateCap,
            UserInputsDto userInputsDto) {
        Map<StatVariable, Integer> statCaps = Map.of(StatVariable.CRITICAL_RATE, criticalRateCap);


        Integer min = (int) Math.round(userInputsDto.criticalBoundsInputDto().minimum() * (OptimizationConfigurations.getScale() / 100.0));
        Integer max = (int) Math.round(userInputsDto.criticalBoundsInputDto().maximum() * (OptimizationConfigurations.getScale() / 100.0));

        Map<StatVariable, Integer> statLowerBounds = Map.of(StatVariable.CRITICAL_RATE, min);

        Map<StatVariable, Integer> statUpperBounds = Map.of(StatVariable.CRITICAL_RATE, max);

        return new StatLimitConfiguration(statCaps, statLowerBounds, statUpperBounds);
    }

    public static int getDefaultCriticalCap() {
        return 10000;
    }
}
