package org.olivia.test.project;

import org.olivia.test.project.service.OptimizerService;
import org.olivia.test.project.tests.RandomTests;

public class MainApp {
    public static void main(String[] args) {

        OptimizerService.solveOptimizationProblem();
        RandomTests randomTests = new RandomTests();
        randomTests.runSmokeTest();
    }
}