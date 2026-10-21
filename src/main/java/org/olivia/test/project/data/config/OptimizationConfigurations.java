package org.olivia.test.project.data.config;

public class OptimizationConfigurations {
    private static int scale = 10000;

    public OptimizationConfigurations() {
    }

    public OptimizationConfigurations(int scale) {
        OptimizationConfigurations.scale = scale;
    }

    public static int getScale() {
        return scale;
    }

    public static double getDoubleScale() {
        return scale;
    }
}
