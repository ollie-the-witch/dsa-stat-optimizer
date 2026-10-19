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
     * @param model model to modify
     * @param variablePrefix string to differentiate between different kinds of stats for different (stat) categories
     * @param availableStats stats available to equipment of this (stat) category, derived from StatIncreases
     * @return the choices
     */
    private static Map<EquipmentSlots, Map<StatVariableName, BoolVar>> createSlotChoices(CpModel model,
            String variablePrefix,
            Iterable<StatVariableName> availableStats) {

        Map<EquipmentSlots, Map<StatVariableName, BoolVar>> choicesBySlot = new java.util.EnumMap<>(EquipmentSlots.class);

        /*
         * For each equipment slot,
         * for each stat,
         *
         * Add a BoolVar for the stat to create potential choice of stat
         */
        for (EquipmentSlots slot : EquipmentSlots.values()) {
            Map<StatVariableName, BoolVar> choicesByStat = new java.util.EnumMap<>(StatVariableName.class);

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
     * @param model model :)
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
     * TODO WIP; refactor later
     * Create model to shove into solver later
     * Needs:
     * - Selection
     * - Constraints
     * - Envelope
     * - Bounds
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
         */
        var mainStatChoicesBySlot = createSlotChoices(model, "main", statIncreases.mainStats().keySet());
        var substatChoicesBySlot = createSlotChoices(model, "sub", statIncreases.subStats().keySet());

        int mainStatCount = selectCount.count().get(StatCategory.MAIN);
        int substatCount = selectCount.count().get(StatCategory.SUBSTAT);
        enforceSelectionCount(model, mainStatChoicesBySlot, mainStatCount);
        enforceSelectionCount(model, substatChoicesBySlot, substatCount);


        Map<StatVariableName, IntVar> finalStats = null;
        IntVar objective = null;

        return null;
    }

    public record OptimizationModelBundle(CpModel model, Map<EquipmentSlots, Map<StatVariableName, BoolVar>> mainStatChoicesBySlot,
                                          Map<EquipmentSlots, Map<StatVariableName, BoolVar>> substatChoicesBySlot, Map<StatVariableName, IntVar> runeCountsByStat,
                                          Map<StatVariableName, IntVar> finalStats, IntVar objective, Map<StatVariableName, StatBoundsCalculator.StatBounds> statBounds) {
    }

    /**
     * Record for passing information about damage uptime
     * @param scale
     * @param physicalStatusUptime
     * @param elementalStatusUptime
     */
    public record StatusUptimeParameters(int scale, double physicalStatusUptime, double elementalStatusUptime) {
    }


}
