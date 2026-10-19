package org.olivia.test.project.tests;

import org.olivia.test.project.config.BaseStats;
import org.olivia.test.project.config.SelectCount;
import org.olivia.test.project.config.StatIncreases;
import org.olivia.test.project.config.StatLimitConfiguration;
import org.olivia.test.project.defaultSettings.DefaultConfigs;
import org.olivia.test.project.service.OptimizationModel;

public class RandomTests {

    public void runSmokeTest() {

        StatIncreases defaultStatIncrease = DefaultConfigs.defaultStatIncreases();
        BaseStats defaultBaseStats = DefaultConfigs.defaultBaseStats();
        SelectCount defaultSelectCount = DefaultConfigs.defaultSelectCount();
        StatLimitConfiguration defaultStatBoundsConfiguration = DefaultConfigs.defaultStatBoundsConfiguration();

        OptimizationModel optimizationModel = new OptimizationModel();
        optimizationModel.createOptimizationModel(defaultBaseStats, defaultStatIncrease, defaultStatBoundsConfiguration, defaultSelectCount);
    }
}
