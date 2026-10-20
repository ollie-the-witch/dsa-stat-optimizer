package org.olivia.test.project.optimizationSolver;

import org.olivia.test.project.config.enumerations.EquipmentSlots;
import org.olivia.test.project.config.enumerations.StatVariableName;

import java.util.List;
import java.util.Map;

public record OptimizationResult(String solverStatus, Map<StatVariableName, Integer> finalStats, Map<EquipmentSlots, StatVariableName> mainStats,
                                 Map<EquipmentSlots, List<StatVariableName>> subStats, Map<StatVariableName, Integer> runeCounts, long scaledObjective, double bestObjectiveBound,
                                 double wallTimeSeconds) {
}