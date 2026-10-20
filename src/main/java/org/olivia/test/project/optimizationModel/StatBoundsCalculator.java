package org.olivia.test.project.optimizationModel;

import org.olivia.test.project.data.BaseScaledStats;
import org.olivia.test.project.data.StatIncreases;
import org.olivia.test.project.data.config.StatLimitConfiguration;
import org.olivia.test.project.data.enumerations.EquipmentSlots;
import org.olivia.test.project.data.enumerations.StatVariable;

import java.util.HashMap;
import java.util.Map;

public class StatBoundsCalculator {

    public static Map<StatVariable, StatBounds> calculateStatBounds(BaseScaledStats baseScaledStats,
            StatIncreases statIncreases,
            StatLimitConfiguration statLimitConfiguration) {
        Map<StatVariable, StatBounds> bounds = new HashMap<>();

        int slotCount = EquipmentSlots.values().length;

        // hardcoded maximums, and user-set limits
        Map<StatVariable, Integer> statCaps = statLimitConfiguration.statCaps();
        Map<StatVariable, Integer> statUpperBounds = statLimitConfiguration.statUpperBounds();
        Map<StatVariable, Integer> statLowerBounds = statLimitConfiguration.statLowerBounds();

        // for each stat in StatVariable, calculate the upper and lower bounds of the stat, within set bounds
        for (StatVariable stat : StatVariable.values()) {
            int lowerBound = baseScaledStats.baseStats().get(stat);
            int upperBound = lowerBound;

            // Adding stats for main stat and sub stat slots
            for (EquipmentSlots slot : EquipmentSlots.values()) {
                int[] main = statIncreases.mainStats().get(stat);
                if (main != null) {
                    int inc = main[slot.index];
                    lowerBound += Math.min(0, inc);
                    upperBound += Math.max(0, inc);
                }

                int[] sub = statIncreases.subStats().get(stat);
                if (sub != null) {
                    int inc = sub[slot.index];
                    lowerBound += Math.min(0, inc);
                    upperBound += Math.max(0, inc);
                }
            }

            // Adding stats for runes
            int runeInc = statIncreases.runes().getOrDefault(stat, 0);
            lowerBound += slotCount * Math.min(0, runeInc);
            upperBound += slotCount * Math.max(0, runeInc);

            // Apply configured bounds and caps.
            Integer cap = statCaps.get(stat);
            if (cap != null) {
                upperBound = Math.min(upperBound, cap);
            }

            Integer customLower = statLowerBounds.get(stat);
            if (customLower != null) {
                lowerBound = Math.max(lowerBound, customLower);
            }

            Integer customUpper = statUpperBounds.get(stat);
            if (customUpper != null) {
                upperBound = Math.min(upperBound, customUpper);
            }

            if (lowerBound > upperBound) {
                throw new IllegalArgumentException("No possible domain for " + stat + ": calculated lower bound " + lowerBound + " exceeds upper bound " + upperBound);
            }

            bounds.put(stat, new StatBounds(lowerBound, upperBound));
        }

        return bounds;
    }

    public record StatBounds(int lower, int upper) {
    }
}
