package org.olivia.test.project.outputs;

import org.olivia.test.project.data.StatusDamageBonus;
import org.olivia.test.project.data.StatusUptimeParameters;

public class DamageCalculator {

    public void calculateDamage(DamageCalculatorPacket packet) {

    }

    public record DamageCalculatorPacket(
            Integer attack,
            Integer attackPowerUp,
            Integer criticalRate,
            Integer criticalDamage,
            Integer physicalAttack,
            Integer elementalAttack,
            Integer statusAttackPowerUp,
            StatusUptimeParameters uptimeParameters,
            StatusDamageBonus statusDamageBonus){

    }
}
