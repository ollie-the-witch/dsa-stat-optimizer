package org.olivia.test.project.orchestrator;

import com.google.ortools.Loader;
import org.olivia.test.project.data.BaseScaledStats;
import org.olivia.test.project.data.StatIncreases;
import org.olivia.test.project.data.StatusDamageBonus;
import org.olivia.test.project.data.StatusUptimeParameters;
import org.olivia.test.project.data.config.OptimizationHint;
import org.olivia.test.project.data.config.SelectCount;
import org.olivia.test.project.data.config.SolverConfiguration;
import org.olivia.test.project.data.config.StatLimitConfiguration;
import org.olivia.test.project.inputs.InputService;
import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;
import org.olivia.test.project.optimizationModel.OptimizationModelService;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;
import org.olivia.test.project.optimizationSolver.OptimizationSolverService;
import org.olivia.test.project.outputs.ConsolePrint;

public class OptimizerOrchestrator {

    private final BaseScaledStats baseScaledStats;
    private final StatusUptimeParameters damageUptimeParameter;
    private final StatLimitConfiguration statBoundsConfiguration;
    private final StatusDamageBonus statusDamageBonus;
    private final SolverConfiguration solverConfiguration;

    private final StatIncreases defaultStatIncrease;
    private final SelectCount defaultSelectCount;
    private final OptimizationHint defaultOptimizationHint;

    public OptimizerOrchestrator(Inputs inputs) {
        baseScaledStats = BaseScaledStats.calculateBaseStats(inputs.getCharacterStatInputDto());
        damageUptimeParameter = StatusUptimeParameters.damageUptimeParameter(inputs.getUptimeAssumptionsInputDto());
        statBoundsConfiguration = StatLimitConfiguration.statBoundsConfiguration(inputs.getCriticalBoundsInputDto());
        statusDamageBonus = StatusDamageBonus.damageBonusParameter(inputs.getCharacterStatInputDto().statusAdditionalDamageBonus());
        solverConfiguration = SolverConfiguration.configuration(inputs.getSolverSettingsInputDto());

        defaultStatIncrease = StatIncreases.defaultStatIncreases();
        defaultSelectCount = SelectCount.defaultSelectCount();
        defaultOptimizationHint = OptimizationHint.defaultOptimizationHint();
    }

    public static Inputs loadInput() {

        InputService inputService = new InputService();
        Inputs input = inputService.defaultInputs();

        return input;
    }

    public OptimizationModelBundle createOptimizationModel() {
        Loader.loadNativeLibraries();
        OptimizationModelService optimizationModelService = new OptimizationModelService();
        return optimizationModelService.createOptimizationModelBundle(baseScaledStats,
                defaultStatIncrease,
                statBoundsConfiguration,
                defaultSelectCount,
                damageUptimeParameter,
                defaultOptimizationHint);

    }

    public OptimizationRunResult solverOptimzationProblem(OptimizationModelBundle model) {
        OptimizationSolverService solverService = new OptimizationSolverService(model, solverConfiguration);
        return solverService.optimize();
    }

    public void printResultToConsole(Inputs inputs,
            OptimizationRunResult result) {
        ConsolePrint consolePrinter = new ConsolePrint(inputs, baseScaledStats, damageUptimeParameter, statusDamageBonus, result);
        consolePrinter.printOutput();
    }
}
