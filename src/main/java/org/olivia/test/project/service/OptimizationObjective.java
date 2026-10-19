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

        /*
        Item to maximize: Damage

        Formula (pre-scaled):
        Damage = (Attack * (1 + Attack Power Up)) * (1 + (Critical Damage * Critical Rate))
        Damage = ((Attack * (1 + Attack Power Up)) + (Status Attack * (1 + Status Attack Power Up))) * (1 + (Critical Damage * Critical Rate))
        Damage = ((Attack * (1 + Attack Power Up)) + ((Physical + Element Status Attack) * (1 + Status Attack Power Up))) * (1 + (Critical Damage * Critical Rate))
        Damage = ((Attack * (1 + Attack Power Up)) + ((Physical + Element Status Attack) * (1 + Status Attack Power Up))) * (1 + (Critical Damage * Critical Rate))

        Then add uptime components to Physical and Elemental attack modifiers because how much they actually do depend on how often the status they need are actually on:
        Thus, uptime.

        Scale percentages by 10000 to do integer operations for stats that need optimization
        Percentages:
        - Attack Power Up
        - Status Attack Power Up
        - Critical Damage
        - Critical Rate

        Attack Component: (Attack * (10000 + Attack Power Up)) + ((PhysAttack * PhysUptime) + (EleAttack * EleUptime)) * (10000 + Status Attack Power Up) = P
        Critical Component: 100000000 * (Critical Damage * Critical Rate) = Q

        Objective = R = P * Q

        Operations will exceed Integer maxes so convert to longs first
         */

        LongBounds xBounds = asLongBounds(bounds.get(StatVariableName.ATTACK));
        LongBounds wBounds = asLongBounds(bounds.get(StatVariableName.ATTACK_POWER_UP));
        LongBounds yBounds = asLongBounds(bounds.get(StatVariableName.CRITICAL_RATE));
        LongBounds zBounds = asLongBounds(bounds.get(StatVariableName.CRITICAL_DAMAGE));
        LongBounds uBounds = asLongBounds(bounds.get(StatVariableName.PHYSICAL_ATTACK));
        LongBounds vBounds = asLongBounds(bounds.get(StatVariableName.ELEMENTAL_ATTACK));
        LongBounds tBounds = asLongBounds(bounds.get(StatVariableName.STATUS_ATTACK_POWER_UP));

        long scale = parameters.scale();
        long physicalUptime = Math.round(parameters.physicalStatusUptime() * getUptimeScale());
        long elementalUptime = Math.round(parameters.elementalStatusUptime() * getUptimeScale());


        return null;
    }

    private static long getUptimeScale() {
        return OptimizationConfigurations.getScale();
    }

    private static LongBounds asLongBounds(StatBoundsCalculator.StatBounds bounds) {
        return new LongBounds(bounds.lower(), bounds.upper());
    }

    record LongBounds(long lower, long upper) {
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
