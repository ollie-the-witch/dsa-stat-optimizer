package org.olivia.test.project;

import com.google.ortools.Loader;
import org.olivia.test.project.config.*;
import org.olivia.test.project.inputs.InputService;
import org.olivia.test.project.inputs.dto.UserInputsDto;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;
import org.olivia.test.project.optimizationModel.OptimizationModelService;

public class OptimizerOrchestrator {


    public static void solveOptimizationProblem() {
        Loader.loadNativeLibraries();

        /*
        Get Inputs
         */

        InputService inputService = new InputService();

        UserInputsDto userInput = inputService.readInputMock();
        BaseStats baseStats = BaseStats.calculateBaseStats(userInput);
        StatusUptimeParameters defaultDamageUptimeParameter = StatusUptimeParameters.damageUptimeParameter(userInput);
        StatLimitConfiguration defaultStatBoundsConfiguration = StatLimitConfiguration.defaultStatBoundsConfiguration(StatLimitConfiguration.getDefaultCriticalCap(), userInput);

        StatIncreases defaultStatIncrease = StatIncreases.defaultStatIncreases();
        SelectCount defaultSelectCount = SelectCount.defaultSelectCount();


        /*
        Call Optimization Model
         */

        OptimizationModelService optimizationModelService = new OptimizationModelService();
        OptimizationModelBundle model = optimizationModelService.createOptimizationModelBundle(baseStats, defaultStatIncrease, defaultStatBoundsConfiguration, defaultSelectCount
                , defaultDamageUptimeParameter);
        System.out.println(model);

        /*
        Call Solver
         */
    }

    private void buildModel() {

    }
}
