package org.olivia.test.project.config;

import org.olivia.test.project.config.enumerations.StatVariableName;
import org.olivia.test.project.inputs.dto.CharacterStatInputDto;
import org.olivia.test.project.inputs.dto.UserInputsDto;

import java.util.HashMap;
import java.util.Map;

public record BaseStats(Map<StatVariableName, Integer> baseStats) {

    public static BaseStats calculateBaseStats(UserInputsDto userInputsDto) {

        int scale = OptimizationConfigurations.getScale();

        CharacterStatInputDto characterStatInputDto = userInputsDto.characterStatInputDto();
        int totalAttack = characterStatInputDto.characterSheetAttack();
        double totalAttackUp = characterStatInputDto.sumOptimizationAttackPowerUp();
        int baseAttack = (int) (totalAttack / (1 + (totalAttackUp / 100)));

        double totalCriticalRate = characterStatInputDto.sumCriticalRate();
        double totalCriticalDamage = characterStatInputDto.sumCriticalDamage();

        int physicalAdditionalAttackPower = characterStatInputDto.statusAttackPower().physicalAdditionalAttackPower();
        int elementalAdditionalAttackPower = characterStatInputDto.statusAttackPower().elementalAdditionalAttackPower();
        double existingStatusAttackPowerUp = characterStatInputDto.statusAttackPower().existingStatusAttackPowerUp();

        Map<StatVariableName, Integer> baseStatsMap = new HashMap<>();
        baseStatsMap.put(StatVariableName.ATTACK, baseAttack);
        baseStatsMap.put(StatVariableName.ATTACK_POWER_UP, (int) (totalAttackUp / 100 * scale));
        baseStatsMap.put(StatVariableName.CRITICAL_RATE, (int) (totalCriticalRate / 100 * scale));
        baseStatsMap.put(StatVariableName.CRITICAL_DAMAGE, (int) (totalCriticalDamage / 100 * scale));
        baseStatsMap.put(StatVariableName.PHYSICAL_ATTACK, physicalAdditionalAttackPower);
        baseStatsMap.put(StatVariableName.ELEMENTAL_ATTACK, elementalAdditionalAttackPower);
        baseStatsMap.put(StatVariableName.STATUS_ATTACK_POWER_UP, (int) (existingStatusAttackPowerUp / 100 * scale));

        return new BaseStats(baseStatsMap);
    }
}
