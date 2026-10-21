package org.olivia.test.project.optimizationSolver;

import com.google.ortools.sat.*;
import org.olivia.test.project.data.config.SolverConfiguration;
import org.olivia.test.project.data.enumerations.EquipmentSlots;
import org.olivia.test.project.data.enumerations.StatVariable;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;

import java.util.ArrayList;
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

    private static void excludeFinalStats(CpModel model,
            Map<StatVariable, IntVar> finalStatVariables,
            Map<StatVariable, Integer> previousFinalStats,
            int previousSolve) {
        List<Literal> differentStatLiterals = new ArrayList<>();

        for (var entry : finalStatVariables.entrySet()) {
            StatVariable stat = entry.getKey();
            IntVar variable = entry.getValue();
            int previousValue = previousFinalStats.get(stat);
            BoolVar isDifferent = model.newBoolVar("solve_" + previousSolve + "_different_" + stat.name());

            model.addDifferent(variable, previousValue).onlyEnforceIf(isDifferent);
            model.addEquality(variable, previousValue).onlyEnforceIf(isDifferent.not());
            differentStatLiterals.add(isDifferent);
        }

        if (differentStatLiterals.isEmpty()) {
            throw new IllegalStateException("Cannot exclude a prior result without final-stat variables.");
        }
        model.addBoolOr(differentStatLiterals.toArray(Literal[]::new));
    }

    public OptimizationRunResult optimize() {
        if (solverConfiguration.resultCount() < 1) {
            throw new IllegalArgumentException("Result count must be at least 1.");
        }

        List<OptimizationResult> solutions = new ArrayList<>();
        String stopReason = "Requested number of distinct outcomes returned.";

        for (int solveIndex = 0; solveIndex < solverConfiguration.resultCount(); solveIndex++) {
            CpSolver solver = new CpSolver();
            solver.getParameters().setMaxTimeInSeconds(solverConfiguration.timeLimit());
            solver.getParameters().setNumSearchWorkers(solverConfiguration.workerCount());

            CpSolverStatus status = solver.solve(bundle.model());
            if (status != CpSolverStatus.OPTIMAL && status != CpSolverStatus.FEASIBLE) {
                if (status == CpSolverStatus.MODEL_INVALID) {
                    throw new IllegalStateException("Optimization model is invalid.");
                }
                if (solutions.isEmpty()) {
                    stopReason = status == CpSolverStatus.INFEASIBLE ? "No feasible solution exists for the current model." : "No feasible solution was found before the solver " +
                            "stopped: " + status + ".";
                } else {
                    stopReason = status == CpSolverStatus.INFEASIBLE ? "No further distinct final-stat outcome exists." :
                            "Stopped before finding another distinct outcome: " + status + ".";
                }
                break;
            }

            solutions.add(readSolution(solver, status));
            excludeFinalStats(bundle.model(), bundle.finalStats(), solutions.get(solutions.size() - 1).finalStats(), solveIndex + 1);
        }

        solutions.sort((first, second) -> Long.compare(second.scaledObjective(), first.scaledObjective()));
        List<OptimizationResult> rankedSolutions = new ArrayList<>(solutions.size());
        for (int index = 0; index < solutions.size(); index++) {
            rankedSolutions.add(solutions.get(index).withRank(index + 1));
        }

        return new OptimizationRunResult(solverConfiguration.resultCount(), rankedSolutions, stopReason);
    }

    private OptimizationResult readSolution(CpSolver solver,
            CpSolverStatus status) {
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

        return new OptimizationResult(0, status.name(), finalStats, mainStats, subStats, runeCounts, solver.value(bundle.objective()), solver.bestObjectiveBound(),
                solver.wallTime());
    }
}
