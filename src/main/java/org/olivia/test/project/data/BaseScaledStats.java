package org.olivia.test.project.data;

import org.olivia.test.project.data.config.OptimizationConfigurations;
import org.olivia.test.project.data.enumerations.StatVariable;
import org.olivia.test.project.inputs.dto.CharacterStatInputDto;

import java.util.HashMap;
import java.util.Map;

public record BaseScaledStats(Map<StatVariable, Integer> baseStats) {

    public static BaseScaledStats calculateBaseStats(CharacterStatInputDto characterStatInputDto) {

        int scale = OptimizationConfigurations.getScale();

        int totalAttack = characterStatInputDto.characterSheetAttack();
        double totalAttackUp = characterStatInputDto.sumAttackPowerUp();
        int baseAttack = (int) Math.round(totalAttack / (1 + totalAttackUp / 100));

        double totalCriticalRate = characterStatInputDto.sumCriticalRate();
        double totalCriticalDamage = characterStatInputDto.sumCriticalDamage();

        int physicalAdditionalAttackPower = characterStatInputDto.statusAttackPower().physicalAdditionalAttackPower();
        int elementalAdditionalAttackPower = characterStatInputDto.statusAttackPower().elementalAdditionalAttackPower();
        double statusAttackPowerUp = characterStatInputDto.statusAttackPower().sumStatusAttackPowerUp();

        Map<StatVariable, Integer> baseStatsMap = new HashMap<>();
        baseStatsMap.put(StatVariable.ATTACK, baseAttack);
        baseStatsMap.put(StatVariable.ATTACK_POWER_UP, (int) (totalAttackUp * scale / 100));
        baseStatsMap.put(StatVariable.CRITICAL_RATE, (int) (totalCriticalRate * scale / 100));
        baseStatsMap.put(StatVariable.CRITICAL_DAMAGE, (int) (totalCriticalDamage * scale / 100));
        baseStatsMap.put(StatVariable.PHYSICAL_ATTACK, physicalAdditionalAttackPower);
        baseStatsMap.put(StatVariable.ELEMENTAL_ATTACK, elementalAdditionalAttackPower);
        baseStatsMap.put(StatVariable.STATUS_ATTACK_POWER_UP, (int) (statusAttackPowerUp * scale / 100));

        return new BaseScaledStats(baseStatsMap);
    }
}
