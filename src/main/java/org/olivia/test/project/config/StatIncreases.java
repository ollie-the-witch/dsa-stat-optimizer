package org.olivia.test.project.config;

import org.olivia.test.project.config.enumerations.StatVariableName;

import java.util.Map;

public record StatIncreases(Map<StatVariableName, int[]> mainStats, Map<StatVariableName, int[]> subStats, Map<StatVariableName, Integer> runes) {
}
