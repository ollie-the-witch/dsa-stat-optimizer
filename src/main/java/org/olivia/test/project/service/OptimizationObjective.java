package org.olivia.test.project.service;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import org.olivia.test.project.config.OptimizationConfigurations;
import org.olivia.test.project.config.enumerations.StatVariableName;

import java.util.Map;

public class OptimizationObjective {


    public static IntVar addObjective(CpModel model,
            Map<StatVariableName, IntVar> stats,
            Map<StatVariableName, StatBoundsCalculator.StatBounds> bounds,
            StatusUptimeParameters parameters) {


        long scale = parameters.scale();
        long physicalUptime = Math.round(parameters.physicalStatusUptime() * getUptimeScale());
        long elementalUptime = Math.round(parameters.elementalStatusUptime() * getUptimeScale());


        return null;
    }

    private static long getUptimeScale() {
        return OptimizationConfigurations.getScale();
    }

    /**
     * Record for passing information about damage uptime
     *
     * @param scale
     * @param physicalStatusUptime
     * @param elementalStatusUptime
     */
    public record StatusUptimeParameters(int scale, double physicalStatusUptime, double elementalStatusUptime) {
    }
}
