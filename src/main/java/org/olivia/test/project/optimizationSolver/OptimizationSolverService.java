package org.olivia.test.project.optimizationSolver;

import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import org.olivia.test.project.data.config.SolverConfiguration;
import org.olivia.test.project.data.enumerations.EquipmentSlots;
import org.olivia.test.project.data.enumerations.StatVariable;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OptimizationSolverService {
    private final OptimizationModelBundle bundle;
    private final SolverConfiguration solverConfiguration;

    public OptimizationSolverService(OptimizationModelBundle bundle,
            SolverConfiguration solverConfiguration) {
        this.bundle = bundle;
        this.solverConfiguration = solverConfiguration;
    }

    public OptimizationResult optimize() {

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(solverConfiguration.timeLimit());
        solver.getParameters().setNumSearchWorkers(solverConfiguration.workerCount());

        CpSolverStatus status = solver.solve(bundle.model());

        if (status != CpSolverStatus.OPTIMAL && status != CpSolverStatus.FEASIBLE) {
            throw new IllegalStateException("Optimization failed. Solver status: " + status);
        }

        Map<StatVariable, Integer> finalStats = new EnumMap<>(StatVariable.class);
        bundle.finalStats().forEach((stat, variable) -> finalStats.put(stat, Math.toIntExact(solver.value(variable))));

        Map<EquipmentSlots, StatVariable> mainStats = new EnumMap<>(EquipmentSlots.class);
        Map<EquipmentSlots, List<StatVariable>> subStats = new EnumMap<>(EquipmentSlots.class);
        for (EquipmentSlots slot : EquipmentSlots.values()) {
            var selectedMain =
                    bundle.mainStatChoicesBySlot().get(slot).entrySet().stream().filter(entry -> solver.booleanValue(entry.getValue())).map(Map.Entry::getKey).findFirst().orElseThrow(() -> new IllegalStateException("No main stat selected for " + slot));
            mainStats.put(slot, selectedMain);

            List<StatVariable> selectedSubStats =
                    bundle.substatChoicesBySlot().get(slot).entrySet().stream().filter(entry -> solver.booleanValue(entry.getValue())).map(Map.Entry::getKey).toList();
            subStats.put(slot, selectedSubStats);
        }

        Map<StatVariable, Integer> runeCounts = new EnumMap<>(StatVariable.class);
        bundle.runeCountsByStat().forEach((stat, variable) -> runeCounts.put(stat, Math.toIntExact(solver.value(variable))));

        return new OptimizationResult(status.name(), finalStats, mainStats, subStats, runeCounts, solver.value(bundle.objective()), solver.bestObjectiveBound(), solver.wallTime());
    }
}
