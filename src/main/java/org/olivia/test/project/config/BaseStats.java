package org.olivia.test.project.config;

import org.olivia.test.project.config.enumerations.StatVariableName;

import java.util.Map;

public record BaseStats(Map<StatVariableName, Integer> baseStats) {
}
