package org.olivia.test.project.optimizationSolver;

import org.olivia.test.project.data.enumerations.EquipmentSlots;
import org.olivia.test.project.data.enumerations.StatVariable;

import java.util.List;
import java.util.Map;

public record OptimizationResult(String solverStatus, Map<StatVariable, Integer> finalStats, Map<EquipmentSlots, StatVariable> mainStats,
                                 Map<EquipmentSlots, List<StatVariable>> subStats, Map<StatVariable, Integer> runeCounts, long scaledObjective, double bestObjectiveBound,
                                 double wallTimeSeconds) {
}