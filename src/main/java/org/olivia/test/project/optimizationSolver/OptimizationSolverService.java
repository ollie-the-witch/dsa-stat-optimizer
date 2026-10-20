package org.olivia.test.project.optimizationSolver;

import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import org.olivia.test.project.config.SolverConfiguration;
import org.olivia.test.project.config.enumerations.EquipmentSlots;
import org.olivia.test.project.config.enumerations.StatVariableName;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OptimizationSolverService {
    public static OptimizationResult optimize(OptimizationModelBundle bundle,
            SolverConfiguration solverConfiguration) {

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(solverConfiguration.timeLimit());
        solver.getParameters().setNumSearchWorkers(solverConfiguration.workerCount());

        CpSolverStatus status = solver.solve(bundle.model());

        if (status != CpSolverStatus.OPTIMAL && status != CpSolverStatus.FEASIBLE) {
            throw new IllegalStateException("Optimization failed. Solver status: " + status);
        }

        Map<StatVariableName, Integer> finalStats = new EnumMap<>(StatVariableName.class);
        bundle.finalStats().forEach((stat, variable) -> finalStats.put(stat, Math.toIntExact(solver.value(variable))));

        Map<EquipmentSlots, StatVariableName> mainStats = new EnumMap<>(EquipmentSlots.class);
        Map<EquipmentSlots, List<StatVariableName>> subStats = new EnumMap<>(EquipmentSlots.class);
        for (EquipmentSlots slot : EquipmentSlots.values()) {
            var selectedMain = bundle.mainStatChoicesBySlot().get(slot).entrySet().stream()
                    .filter(entry -> solver.booleanValue(entry.getValue())).map(Map.Entry::getKey).findFirst().orElseThrow(() -> new IllegalStateException("No main stat selected for " + slot));
            mainStats.put(slot, selectedMain);

            List<StatVariableName> selectedSubStats =
                    bundle.substatChoicesBySlot().get(slot).entrySet().stream().filter(entry -> solver.booleanValue(entry.getValue())).map(Map.Entry::getKey).toList();
            subStats.put(slot, selectedSubStats);
        }

        Map<StatVariableName, Integer> runeCounts = new EnumMap<>(StatVariableName.class);
        bundle.runeCountsByStat().forEach((stat, variable) -> runeCounts.put(stat, Math.toIntExact(solver.value(variable))));

        return new OptimizationResult(status.name(), finalStats, mainStats, subStats, runeCounts, solver.value(bundle.objective()), solver.bestObjectiveBound(), solver.wallTime());
    }
}
