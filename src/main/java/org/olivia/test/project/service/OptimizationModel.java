package org.olivia.test.project.service;

import com.google.ortools.sat.*;
import org.olivia.test.project.config.BaseStats;
import org.olivia.test.project.config.SelectCount;
import org.olivia.test.project.config.StatIncreases;
import org.olivia.test.project.config.StatLimitConfiguration;
import org.olivia.test.project.config.enumerations.EquipmentSlots;
import org.olivia.test.project.config.enumerations.StatCategory;
import org.olivia.test.project.config.enumerations.StatVariableName;

import java.util.EnumMap;
import java.util.Map;

public class OptimizationModel {

    private static Map<StatVariableName, BoolVar> createSelectionVariables(CpModel model,
            Map<StatVariableName, int[]> increments,
            String family,
            EquipmentSlots slot) {
        Map<StatVariableName, BoolVar> variables = new EnumMap<>(StatVariableName.class);

        for (StatVariableName stat : increments.keySet()) {
            String variableName = family + "_" + slot.name() + "_" + stat.name();
            variables.put(stat, model.newBoolVar(variableName));
        }

        return variables;
    }

    /**
     * Creates the potential choices for each of the slots
     *
     * @param model          model to modify
     * @param variablePrefix string to differentiate between different kinds of stats for different (stat) categories
     * @param availableStats stats available to equipment of this (stat) category, derived from StatIncreases
     * @return the choices
     */
    private static Map<EquipmentSlots, Map<StatVariableName, BoolVar>> createSlotChoices(CpModel model,
            String variablePrefix,
            Iterable<StatVariableName> availableStats) {

        Map<EquipmentSlots, Map<StatVariableName, BoolVar>> choicesBySlot = new EnumMap<>(EquipmentSlots.class);

        /*
         * For each equipment slot,
         * for each stat,
         *
         * Add a BoolVar for the stat to create potential choice of stat
         */
        for (EquipmentSlots slot : EquipmentSlots.values()) {
            Map<StatVariableName, BoolVar> choicesByStat = new EnumMap<>(StatVariableName.class);

            for (StatVariableName stat : availableStats) {
                String variableName = String.format("%s_%s_%s", variablePrefix, slot.name(), stat.name());
                choicesByStat.put(stat, model.newBoolVar(variableName));
            }
            choicesBySlot.put(slot, choicesByStat);
        }

        return choicesBySlot;
    }

    /**
     * Adds a constraint to the model that enforces a number of selections for mainstat, substat
     *
     * @param model         model :)
     * @param choicesBySlot the choices model created prior for category (main/sub)
     * @param requiredCount how many choices of stats for this equipment slot (and category, implicitly via choiceBySlot)
     */
    private static void enforceSelectionCount(CpModel model,
            Map<EquipmentSlots, Map<StatVariableName, BoolVar>> choicesBySlot,
            int requiredCount) {

        for (EquipmentSlots slot : EquipmentSlots.values()) {
            LinearExprBuilder selectedCount = LinearExpr.newBuilder();
            for (BoolVar choice : choicesBySlot.get(slot).values()) {
                selectedCount.add(choice);
            }
            model.addEquality(selectedCount.build(), requiredCount);
        }
    }

    /**
     * Selection-equivalent for runes; slot agnostic, choose 5 among 3 choices
     *
     * @param model             model ._.
     * @param runeStatIncreases provides the list of valid stat increases for runes
     * @param slotCount         how many runes are allowed (5 by default)
     * @return the "selection choices" for runes
     */
    private static Map<StatVariableName, IntVar> createRuneCounts(CpModel model,
            Map<StatVariableName, Integer> runeStatIncreases,
            int slotCount) {

        Map<StatVariableName, IntVar> runeCounts = new EnumMap<>(StatVariableName.class);


        /*
        Allow 0-5 selections for each runeStatIncreases stat
        Restrict total count to 5
         */
        for (StatVariableName stat : runeStatIncreases.keySet()) {
            runeCounts.put(stat, model.newIntVar(0, slotCount, String.format("rune_count_%s", stat.name())));
        }

        LinearExprBuilder totalRunes = LinearExpr.newBuilder();
        for (IntVar runeCount : runeCounts.values()) {
            totalRunes.add(runeCount);
        }

        // One rune per equipment slot, but rune types can repeat.
        model.addEquality(totalRunes.build(), slotCount);

        return runeCounts;
    }

    /**
     * Set final constraints for each stat
     *
     * @param model                 model
     * @param baseStats             base stats calculated from input (percentages are scaled to 10000)
     * @param statIncreases         the increase increment data for main, sub, runes, for each stat for each slot
     * @param mainStatChoicesBySlot selection for main stats
     * @param substatChoicesBySlot  selection for sub stats
     * @param runeCountsByStat      selection for runes
     * @param statBounds            theoretical limits for each stat
     * @return the final constraints for stats
     */
    private static Map<StatVariableName, IntVar> createFinalStatVariables(CpModel model,
            BaseStats baseStats,
            StatIncreases statIncreases,
            Map<EquipmentSlots, Map<StatVariableName, BoolVar>> mainStatChoicesBySlot,
            Map<EquipmentSlots, Map<StatVariableName, BoolVar>> substatChoicesBySlot,
            Map<StatVariableName, IntVar> runeCountsByStat,
            Map<StatVariableName, StatBoundsCalculator.StatBounds> statBounds) {

        Map<StatVariableName, IntVar> finalStats = new EnumMap<>(StatVariableName.class);

        /*
        For each stat, add the calculation possibilities
         */
        for (StatVariableName stat : baseStats.baseStats().keySet()) {
            var bounds = statBounds.get(stat);
            IntVar finalStat = model.newIntVar(bounds.lower(), bounds.upper(), String.format("final_%s", stat.name()));

            LinearExprBuilder expression = LinearExpr.newBuilder();
            expression.add(baseStats.baseStats().get(stat));

            /*
            For each category/equipment & rune for this stat, add how much to increment for calculations in solver
             */
            for (EquipmentSlots slot : EquipmentSlots.values()) {
                int[] mainIncrements = statIncreases.mainStats().get(stat);
                BoolVar mainChoice = mainStatChoicesBySlot.get(slot).get(stat);
                if (mainIncrements != null && mainChoice != null) {
                    expression.addTerm(mainChoice, mainIncrements[slot.index]);
                }

                int[] subIncrements = statIncreases.subStats().get(stat);
                BoolVar subChoice = substatChoicesBySlot.get(slot).get(stat);
                if (subIncrements != null && subChoice != null) {
                    expression.addTerm(subChoice, subIncrements[slot.index]);
                }
            }

            Integer runeIncrement = statIncreases.runes().get(stat);
            IntVar runeCount = runeCountsByStat.get(stat);
            if (runeIncrement != null && runeCount != null) {
                expression.addTerm(runeCount, runeIncrement);
            }

            model.addEquality(finalStat, expression.build());
            finalStats.put(stat, finalStat);
        }

        return finalStats;
    }


    /**
     * TODO WIP; refactor later
     * Create model to shove into solver later
     * Needs:
     * - Selection
     * - Constraints
     * - Envelope
     * - Bounds
     *
     * @param baseStats
     * @param statIncreases
     * @param statLimitConfiguration
     * @param selectCount
     * @return
     */
    public OptimizationModelBundle createOptimizationModel(BaseStats baseStats,
            StatIncreases statIncreases,
            StatLimitConfiguration statLimitConfiguration,
            SelectCount selectCount) {

        /*
        Input validation
        Initial Bounds and restrictions
         */
        Map<StatVariableName, StatBoundsCalculator.StatBounds> bounds = StatBoundsCalculator.calculateStatBounds(baseStats, statIncreases, statLimitConfiguration);


        CpModel model = new CpModel();

        /*
        Create the slot choices for main and substat, then add selection count (default 1,4 respectively)
        Also create the selection choices of runes
         */
        Map<EquipmentSlots, Map<StatVariableName, BoolVar>> mainStatChoicesBySlot = createSlotChoices(model, "main", statIncreases.mainStats().keySet());
        Map<EquipmentSlots, Map<StatVariableName, BoolVar>> substatChoicesBySlot = createSlotChoices(model, "sub", statIncreases.subStats().keySet());

        int mainStatCount = selectCount.count().get(StatCategory.MAIN);
        int substatCount = selectCount.count().get(StatCategory.SUBSTAT);
        enforceSelectionCount(model, mainStatChoicesBySlot, mainStatCount);
        enforceSelectionCount(model, substatChoicesBySlot, substatCount);

        int slotCount = EquipmentSlots.values().length;
        Map<StatVariableName, IntVar> runeCounts = createRuneCounts(model, statIncreases.runes(), slotCount);

        /*
        Define each final stat as base + selected increments.
         */
        Map<StatVariableName, IntVar> finalStats = createFinalStatVariables(model, baseStats, statIncreases, mainStatChoicesBySlot, substatChoicesBySlot, runeCounts, bounds);

        IntVar objective = null;

        return null;
    }

    public record OptimizationModelBundle(CpModel model, Map<EquipmentSlots, Map<StatVariableName, BoolVar>> mainStatChoicesBySlot,
                                          Map<EquipmentSlots, Map<StatVariableName, BoolVar>> substatChoicesBySlot, Map<StatVariableName, IntVar> runeCountsByStat,
                                          Map<StatVariableName, IntVar> finalStats, IntVar objective, Map<StatVariableName, StatBoundsCalculator.StatBounds> statBounds) {
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
