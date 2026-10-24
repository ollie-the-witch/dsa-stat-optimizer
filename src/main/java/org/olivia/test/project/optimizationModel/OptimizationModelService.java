package org.olivia.test.project.optimizationModel;

import com.google.ortools.sat.*;
import org.olivia.test.project.data.BaseScaledStats;
import org.olivia.test.project.data.StatIncreases;
import org.olivia.test.project.data.StatusUptimeParameters;
import org.olivia.test.project.data.config.OptimizationHint;
import org.olivia.test.project.data.config.SelectCount;
import org.olivia.test.project.data.config.StatLimitConfiguration;
import org.olivia.test.project.data.enumerations.EquipmentSlots;
import org.olivia.test.project.data.enumerations.StatCategory;
import org.olivia.test.project.data.enumerations.StatVariable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to set, for the optimizer, variables, constraints, and sets the final objective to work towards
 */
public class OptimizationModelService {

    /**
     * Creates the potential choices for each of the slots
     *
     * @param model          model to modify
     * @param variablePrefix string to differentiate between different kinds of stats for different (stat) categories
     * @param availableStats stats available to equipment of this (stat) category, derived from StatIncreases
     * @return the choices
     */
    private static Map<EquipmentSlots, Map<StatVariable, BoolVar>> createSlotChoices(CpModel model,
            String variablePrefix,
            Iterable<StatVariable> availableStats) {

        Map<EquipmentSlots, Map<StatVariable, BoolVar>> choicesBySlot = new EnumMap<>(EquipmentSlots.class);

        /*
         * For each equipment slot,
         * for each stat,
         *
         * Add a BoolVar for the stat to create potential choice of stat
         */
        for (EquipmentSlots slot : EquipmentSlots.values()) {
            Map<StatVariable, BoolVar> choicesByStat = new EnumMap<>(StatVariable.class);

            for (StatVariable stat : availableStats) {
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
            Map<EquipmentSlots, Map<StatVariable, BoolVar>> choicesBySlot,
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
    private static Map<StatVariable, IntVar> createRuneCounts(CpModel model,
            Map<StatVariable, Integer> runeStatIncreases,
            int slotCount) {

        Map<StatVariable, IntVar> runeCounts = new EnumMap<>(StatVariable.class);


        /*
        Allow 0-5 selections for each runeStatIncreases stat
        Restrict total count to 5
         */
        for (StatVariable stat : runeStatIncreases.keySet()) {
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
     * @param baseScaledStats       base stats calculated from input (percentages are scaled to 10000)
     * @param statIncreases         the increase increment data for main, sub, runes, for each stat for each slot
     * @param mainStatChoicesBySlot selection for main stats
     * @param substatChoicesBySlot  selection for sub stats
     * @param runeCountsByStat      selection for runes
     * @param statBounds            theoretical limits for each stat
     * @return the final constraints for stats
     */
    private static Map<StatVariable, IntVar> createFinalStatVariables(CpModel model,
            BaseScaledStats baseScaledStats,
            StatIncreases statIncreases,
            Map<EquipmentSlots, Map<StatVariable, BoolVar>> mainStatChoicesBySlot,
            Map<EquipmentSlots, Map<StatVariable, BoolVar>> substatChoicesBySlot,
            Map<StatVariable, IntVar> runeCountsByStat,
            Map<StatVariable, StatBoundsCalculator.StatBounds> statBounds) {

        Map<StatVariable, IntVar> finalStats = new EnumMap<>(StatVariable.class);

        /*
        For each stat, add the calculation possibilities
         */
        for (StatVariable stat : baseScaledStats.baseStats().keySet()) {
            var bounds = statBounds.get(stat);
            IntVar finalStat = model.newIntVar(bounds.lower(), bounds.upper(), String.format("final_%s", stat.name()));

            LinearExprBuilder expression = LinearExpr.newBuilder();
            expression.add(baseScaledStats.baseStats().get(stat));

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

    private static void addSelectionHints(CpModel model,
            Map<StatVariable, BoolVar> choicesByStat,
            Set<StatVariable> desiredStats,
            int selectionCount) {
        Set<StatVariable> hintedSelected = EnumSet.noneOf(StatVariable.class);

        // Prefer desired stats that are available in this choice map.
        for (StatVariable stat : choicesByStat.keySet()) {
            if (desiredStats.contains(stat) && hintedSelected.size() < selectionCount) {
                hintedSelected.add(stat);
            }
        }

        // Fill any remaining selections with available stats.
        for (StatVariable stat : choicesByStat.keySet()) {
            if (hintedSelected.size() == selectionCount) {
                break;
            }
            hintedSelected.add(stat);
        }

        // A complete hint must select exactly the configured number of choices.
        if (hintedSelected.size() != selectionCount) {
            return;
        }

        for (var entry : choicesByStat.entrySet()) {
            long value = hintedSelected.contains(entry.getKey()) ? 1L : 0L;
            model.addHint(entry.getValue(), value);
        }
    }


    /**
     * Create model to shove into solver later
     * Needs:
     * - Selection
     * - Constraints
     * - Envelope
     * - Bounds
     *
     * @param baseScaledStats        base stats derived from character sheet inputs
     * @param statIncreases          configured stat increase intervals
     * @param statLimitConfiguration configured limits on stats
     * @param selectCount            number of selections for each category
     * @return the final bundle for optimizer use
     */
    public OptimizationModelBundle createOptimizationModelBundle(BaseScaledStats baseScaledStats,
            StatIncreases statIncreases,
            StatLimitConfiguration statLimitConfiguration,
            SelectCount selectCount,
            StatusUptimeParameters uptimeParameters,
            OptimizationHint hint) {

        /*
        Input validation
        Initial Bounds and restrictions
         */
        Map<StatVariable, StatBoundsCalculator.StatBounds> bounds = StatBoundsCalculator.calculateStatBounds(baseScaledStats, statIncreases, statLimitConfiguration);


        CpModel model = new CpModel();

        /*
        Create the slot choices for main and substat, then add selection count (default 1,4 respectively)
        Also create the selection choices of runes
         */
        Map<EquipmentSlots, Map<StatVariable, BoolVar>> mainStatChoicesBySlot = createSlotChoices(model, "main", statIncreases.mainStats().keySet());
        Map<EquipmentSlots, Map<StatVariable, BoolVar>> substatChoicesBySlot = createSlotChoices(model, "sub", statIncreases.subStats().keySet());

        int mainStatCount = selectCount.count().get(StatCategory.MAIN);
        int subStatCount = selectCount.count().get(StatCategory.SUBSTAT);
        enforceSelectionCount(model, mainStatChoicesBySlot, mainStatCount);
        enforceSelectionCount(model, substatChoicesBySlot, subStatCount);

        // Hard-lock every equipment slot to Attack Power Up.
        for (EquipmentSlots slot : EquipmentSlots.values()) {
            // model.addEquality(mainStatChoicesBySlot.get(slot).get(StatVariable.ATTACK_POWER_UP), 1);
        }

        /*
        Add Hints for Main, Sub, and Runes
         */
        for (EquipmentSlots slot : EquipmentSlots.values()) {
            addSelectionHints(model, mainStatChoicesBySlot.get(slot), hint.mainHint(), mainStatCount);
            addSelectionHints(model, substatChoicesBySlot.get(slot), hint.subStatHint(), subStatCount);
        }
        int slotCount = EquipmentSlots.values().length;
        Map<StatVariable, IntVar> runeCounts = createRuneCounts(model, statIncreases.runes(), slotCount);
        for (var entry : runeCounts.entrySet()) {
            int hintedCount = hint.runeHint().getOrDefault(entry.getKey(), 0);
            model.addHint(entry.getValue(), hintedCount);
        }

        /*
        Define each final stat as base + selected increments.
         */
        Map<StatVariable, IntVar> finalStats = createFinalStatVariables(model, baseScaledStats, statIncreases, mainStatChoicesBySlot, substatChoicesBySlot, runeCounts, bounds);

        IntVar objective = OptimizationObjective.addObjective(model, finalStats, bounds, uptimeParameters);


        return new OptimizationModelBundle(model, mainStatChoicesBySlot, substatChoicesBySlot, runeCounts, finalStats, bounds, objective);
    }


}
