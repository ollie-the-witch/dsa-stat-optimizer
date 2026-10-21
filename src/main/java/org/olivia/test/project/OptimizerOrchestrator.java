package org.olivia.test.project;

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
import org.olivia.test.project.inputs.dto.UserInputsDto;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;
import org.olivia.test.project.optimizationModel.OptimizationModelService;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;
import org.olivia.test.project.optimizationSolver.OptimizationSolverService;
import org.olivia.test.project.outputs.ConsolePrint;

public class OptimizerOrchestrator {


    public static void solveOptimizationProblem() {
        Loader.loadNativeLibraries();

        /*
        Get Inputs
         */

        InputService inputService = new InputService();

        UserInputsDto userInput = inputService.readInputMock();
        BaseScaledStats baseScaledStats = BaseScaledStats.calculateBaseStats(userInput);
        StatusUptimeParameters damageUptimeParameter = StatusUptimeParameters.damageUptimeParameter(userInput);
        StatLimitConfiguration statBoundsConfiguration = StatLimitConfiguration.statBoundsConfiguration(StatLimitConfiguration.getDefaultCriticalCap(), userInput);
        StatusDamageBonus statusDamageBonus = StatusDamageBonus.damageBonusParameter(userInput);
        SolverConfiguration solverConfiguration = SolverConfiguration.configuration(userInput);

        StatIncreases defaultStatIncrease = StatIncreases.defaultStatIncreases();
        SelectCount defaultSelectCount = SelectCount.defaultSelectCount();
        OptimizationHint defaultOptimizationHint = OptimizationHint.defaultOptimizationHint();


        /*
        Call Optimization Model
         */

        OptimizationModelService optimizationModelService = new OptimizationModelService();
        OptimizationModelBundle model = optimizationModelService.createOptimizationModelBundle(baseScaledStats, defaultStatIncrease, statBoundsConfiguration, defaultSelectCount, damageUptimeParameter, defaultOptimizationHint);

        /*
        Call Solver
         */
        OptimizationSolverService solverService = new OptimizationSolverService(model, solverConfiguration);
        OptimizationRunResult result = solverService.optimize();

        /*
        Output Results
         */
        ConsolePrint consolePrinter = new ConsolePrint(userInput, baseScaledStats, damageUptimeParameter, statusDamageBonus, result);
        consolePrinter.printOutput();

    }
}
