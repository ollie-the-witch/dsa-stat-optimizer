package org.olivia.test.project.outputs;

import org.olivia.test.project.data.BaseScaledStats;
import org.olivia.test.project.data.StatusDamageBonus;
import org.olivia.test.project.data.StatusUptimeParameters;
import org.olivia.test.project.data.config.OptimizationConfigurations;
import org.olivia.test.project.data.enumerations.EquipmentSlots;
import org.olivia.test.project.data.enumerations.StatVariable;
import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.optimizationSolver.OptimizationResult;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConsolePrint {
    private final OptimizationRunResult runResult;
    private final Inputs userInput;
    private final StatusDamageBonus statusDamageBonus;
    private final StatusUptimeParameters statusUptimeParameters;

    public ConsolePrint(Inputs userInput,
            BaseScaledStats baseScaledStats,
            StatusUptimeParameters statusUptimeParameters,
            StatusDamageBonus statusDamageBonus,
            OptimizationRunResult runResult) {
        this.userInput = Objects.requireNonNull(userInput);
        this.statusUptimeParameters = Objects.requireNonNull(statusUptimeParameters);
        this.statusDamageBonus = Objects.requireNonNull(statusDamageBonus);
        this.runResult = Objects.requireNonNull(runResult);

    }

    public void printOutput() {

        System.out.println("Debug Input Stats:");
        System.out.println(userInput.getSolverSettingsInputDto());
        System.out.println(userInput.getUptimeAssumptionsInputDto());
        System.out.println(userInput.getCriticalBoundsInputDto());
        System.out.println(userInput.getCharacterStatInputDto());

        for (OptimizationResult result : runResult.solutions()) {
            printSolution(result);
        }

        System.out.printf("\nReturned %d distinct final-stat outcome(s) out of %d requested.\n", runResult.solutions().size(), runResult.requestedResultCount());
        System.out.println(runResult.stopReason());
        if (runResult.solutions().stream().anyMatch(result -> !result.solverStatus().equals("OPTIMAL"))) {
            System.out.println("Note: results are sorted by objective among outcomes found, but global ranking is not guaranteed when a solve is only FEASIBLE.");
        }
    }

    private void printSolution(OptimizationResult result) {
        System.out.printf("\n==================== SOLUTION RANK %d ====================\n", result.rank());
        System.out.printf("Solver Status: %s\n", result.solverStatus());

        System.out.println("\nFinal Stats");
        Integer rawAttack = result.finalStats().get(StatVariable.ATTACK);
        Integer totalAttackPowerUp = result.finalStats().get(StatVariable.ATTACK_POWER_UP);
        int characterAttack = rawAttack * ((totalAttackPowerUp) + OptimizationConfigurations.getScale()) / OptimizationConfigurations.getScale();
        System.out.printf("%-25s: %d\n", "Character Attack", characterAttack);
        for (StatVariable statEnum : StatVariable.values()) {
            Integer finalStat = result.finalStats().get(statEnum);
            String statName = statEnum.alias;
            if (statEnum.scale) {
                System.out.printf("%-25s: %.2f%%\n", statName, StatVariable.convertScaledToReadable(true, finalStat));
            } else {
                System.out.printf("%-25s: %d\n", statName, finalStat);
            }
        }

        System.out.println("\nEquipment Choices");
        for (EquipmentSlots slot : EquipmentSlots.values()) {
            StatVariable mainStat = result.mainStats().get(slot);
            List<StatVariable> subStat = result.subStats().get(slot);
            String subStatList = subStat.stream().map(s -> s.alias).collect(Collectors.joining(", "));
            System.out.printf("%-15s: MainSlot [%s], SubSlot [%s]\n", slot.alias, mainStat.alias, subStatList);
        }

        System.out.println("\nRune Choices");
        for (var entry : result.runeCounts().entrySet()) {
            System.out.printf("%-25s: %d Runes\n", entry.getKey().alias, entry.getValue());
        }

        System.out.println("\nEffective Damage Calculations");
        printDamage(result);


        System.out.println("\nSolver");
        System.out.printf("Objective Bound Rate: %.2f%%\n", 100 * result.scaledObjective() / result.bestObjectiveBound());
        System.out.printf("Wall Time (s): %f\n", result.wallTimeSeconds());
    }

    private String formatDamageString(String summary,
            StatusUptimeParameters statusUptimeParameters) {
        return String.format("%s [%.2f%% Physical, %.2f%% Elemental]", summary, statusUptimeParameters.physicalStatusUptime(), statusUptimeParameters.elementalStatusUptime());
    }

    private void printDamage(OptimizationResult result) {
        DamageCalculator damageCalculator = new DamageCalculator();

        DamageCalculator.DamageCalculatorPacket defaultUptimePacket = new DamageCalculator.DamageCalculatorPacket(result.finalStats(), userInput, statusUptimeParameters, statusDamageBonus);
        System.out.printf("%-50s: %.3f\n", formatDamageString("Uptime Factored", statusUptimeParameters), damageCalculator.calculateDamage(defaultUptimePacket));

        StatusUptimeParameters noUptime = new StatusUptimeParameters(0, 0);
        DamageCalculator.DamageCalculatorPacket noUptimePacket = new DamageCalculator.DamageCalculatorPacket(result.finalStats(), userInput, noUptime, statusDamageBonus);
        System.out.printf("%-50s: %.3f\n", formatDamageString("No Uptime Factored", statusUptimeParameters), damageCalculator.calculateDamage(noUptimePacket));

        StatusUptimeParameters physicalUptime = new StatusUptimeParameters(100, 0);
        DamageCalculator.DamageCalculatorPacket physicalUptimePacket = new DamageCalculator.DamageCalculatorPacket(result.finalStats(), userInput, physicalUptime, statusDamageBonus);
        System.out.printf("%-50s: %.3f\n", formatDamageString("Physical Uptime Only", statusUptimeParameters), damageCalculator.calculateDamage(physicalUptimePacket));

        StatusUptimeParameters elementalUptime = new StatusUptimeParameters(0, 100);
        DamageCalculator.DamageCalculatorPacket elementalUptimePacket = new DamageCalculator.DamageCalculatorPacket(result.finalStats(), userInput, elementalUptime, statusDamageBonus);
        System.out.printf("%-50s: %.3f\n", formatDamageString("Elemental Uptime Only", statusUptimeParameters), damageCalculator.calculateDamage(elementalUptimePacket));

        StatusUptimeParameters fullUptime = new StatusUptimeParameters(100, 100);
        DamageCalculator.DamageCalculatorPacket fullTimePacket = new DamageCalculator.DamageCalculatorPacket(result.finalStats(), userInput, fullUptime, statusDamageBonus);
        System.out.printf("%-50s: %.3f\n", formatDamageString("Full Uptime Factored", statusUptimeParameters), damageCalculator.calculateDamage(fullTimePacket));
    }
}
