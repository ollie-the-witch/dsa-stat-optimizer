package org.olivia.test.project.config;

import org.olivia.test.project.config.enumerations.StatVariableName;

import java.util.Map;

public record StatLimitConfiguration(Map<StatVariableName, Integer> statCaps, Map<StatVariableName, Integer> statLowerBounds, Map<StatVariableName, Integer> statUpperBounds) {
}
