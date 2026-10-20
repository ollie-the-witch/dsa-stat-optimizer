package org.olivia.test.project.config;

import org.olivia.test.project.config.enumerations.StatVariableName;
import org.olivia.test.project.inputs.dto.UserInputsDto;

import java.util.Map;

public record StatLimitConfiguration(Map<StatVariableName, Integer> statCaps, Map<StatVariableName, Integer> statLowerBounds, Map<StatVariableName, Integer> statUpperBounds) {
    public static StatLimitConfiguration defaultStatBoundsConfiguration(Integer criticalRateCap,
            UserInputsDto userInputsDto) {
        Map<StatVariableName, Integer> statCaps = Map.of(StatVariableName.CRITICAL_RATE, criticalRateCap);


        Integer min = (int) Math.round(userInputsDto.criticalBoundsInputDto().minimum() * (OptimizationConfigurations.getScale() / 100.0));
        Integer max = (int) Math.round(userInputsDto.criticalBoundsInputDto().maximum() * (OptimizationConfigurations.getScale() / 100.0));

        Map<StatVariableName, Integer> statLowerBounds = Map.of(StatVariableName.CRITICAL_RATE, min);

        Map<StatVariableName, Integer> statUpperBounds = Map.of(StatVariableName.CRITICAL_RATE, max);

        return new StatLimitConfiguration(statCaps, statLowerBounds, statUpperBounds);
    }

    public static int getDefaultCriticalCap() {
        return 10000;
    }
}
