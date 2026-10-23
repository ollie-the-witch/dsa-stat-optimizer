package org.olivia.test.project.outputs;

import org.olivia.test.project.data.StatusDamageBonus;
import org.olivia.test.project.data.StatusUptimeParameters;
import org.olivia.test.project.data.config.OptimizationConfigurations;
import org.olivia.test.project.data.enumerations.StatVariable;
import org.olivia.test.project.inputs.Inputs;

import java.util.Map;

public class DamageCalculator {

    public double calculateDamage(DamageCalculatorPacket packet) {
        long attack = packet.finalStats().get(StatVariable.ATTACK);
        double attackPowerUp = 1 + (packet.finalStats.get(StatVariable.ATTACK_POWER_UP) / OptimizationConfigurations.getDoubleScale());

        long physicalAttack = packet.finalStats.get(StatVariable.PHYSICAL_ATTACK);
        long elementalAttack = packet.finalStats.get(StatVariable.ELEMENTAL_ATTACK);

        double statusAttackPowerUp = 1 + (packet.finalStats().get(StatVariable.STATUS_ATTACK_POWER_UP) / OptimizationConfigurations.getDoubleScale());
        double physicalUptime = packet.uptimeParameters.physicalStatusUptime() / 100;
        double elementalUptime = packet.uptimeParameters.elementalStatusUptime() / 100;

        double criticalRate = packet.finalStats.get(StatVariable.CRITICAL_RATE) / OptimizationConfigurations.getDoubleScale();
        double criticalDamage = packet.finalStats.get(StatVariable.CRITICAL_DAMAGE) / OptimizationConfigurations.getDoubleScale();

        double statusPhysicalMultiplier = packet.statusDamageBonus.physicalDamageBonusMultiplier();
        double statusElementalMultiplier = packet.statusDamageBonus.elementalDamageBonusMultiplier();

        // Attack
        double totalAttack = attack * attackPowerUp;

        // Status Attack
        double effectivePhysicalAttack = physicalAttack * physicalUptime * statusAttackPowerUp;
        double effectiveElementalAttack = elementalAttack * elementalUptime * statusAttackPowerUp;

        // P
        double p = totalAttack + effectivePhysicalAttack + effectiveElementalAttack;

        // Q
        double q = 1 + (criticalRate * criticalDamage);

        // R = P * Q
        double r = p * q;

        // Z = R * Status Modifiers
        double statusModifiers = 1 + (statusPhysicalMultiplier * physicalUptime) + (elementalUptime * statusElementalMultiplier);
        return r * statusModifiers;

    }

    public record DamageCalculatorPacket(Map<StatVariable, Integer> finalStats, Inputs characterDataInputsDto, StatusUptimeParameters uptimeParameters,
                                         StatusDamageBonus statusDamageBonus) {

    }
}
