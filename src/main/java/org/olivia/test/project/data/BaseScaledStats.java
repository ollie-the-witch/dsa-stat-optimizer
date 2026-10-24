package org.olivia.test.project.data;

import org.olivia.test.project.data.config.OptimizationConfigurations;
import org.olivia.test.project.data.enumerations.StatVariable;
import org.olivia.test.project.inputs.dto.CharacterStatTranslationDto;

import java.util.HashMap;
import java.util.Map;

public record BaseScaledStats(Map<StatVariable, Integer> baseStats) {

    public static BaseScaledStats calculateBaseStats(CharacterStatTranslationDto characterStatTranslationDto) {

        int scale = OptimizationConfigurations.getScale();

        int totalAttack = characterStatTranslationDto.characterSheetAttack();
        double totalAttackUp = characterStatTranslationDto.sumAttackPowerUp();
        int baseAttack = (int) Math.round(totalAttack / (1 + totalAttackUp / 100));

        double totalCriticalRate = characterStatTranslationDto.sumCriticalRate();
        double totalCriticalDamage = characterStatTranslationDto.sumCriticalDamage();

        int physicalAdditionalAttackPower = characterStatTranslationDto.statusAttackPower().physicalAdditionalAttackPower();
        int elementalAdditionalAttackPower = characterStatTranslationDto.statusAttackPower().elementalAdditionalAttackPower();
        double statusAttackPowerUp = characterStatTranslationDto.statusAttackPower().sumStatusAttackPowerUp();

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
