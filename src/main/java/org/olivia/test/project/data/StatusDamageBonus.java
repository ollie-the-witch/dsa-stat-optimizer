package org.olivia.test.project.data;

import org.olivia.test.project.inputs.dto.UserInputsDto;

/**
 * Record for passing information about damage uptime
 *
 * @param physicalStatusUptime
 * @param elementalStatusUptime
 */
public record StatusDamageBonus(double physicalStatusUptime, double elementalStatusUptime) {
    public static StatusDamageBonus damageBonusParameter(UserInputsDto userInputsDto) {
        return new StatusDamageBonus(userInputsDto.characterStatInputDto().statusAdditionalDamageBonus().physicalAdditionalDamageBonus(),
                userInputsDto.characterStatInputDto().statusAdditionalDamageBonus().elementalAdditionalDamageBonus());
    }

    public double physicalDamageBonusMultiplier() {
        return (physicalStatusUptime / 100);
    }

    public double elementalDamageBonusMultiplier() {
        return (elementalStatusUptime / 100);
    }
}
