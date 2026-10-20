package org.olivia.test.project.data;

import org.olivia.test.project.data.config.OptimizationConfigurations;
import org.olivia.test.project.inputs.dto.UserInputsDto;

/**
 * Record for passing information about damage uptime
 *
 * @param physicalStatusUptime
 * @param elementalStatusUptime
 */
public record StatusDamageBonus(double physicalStatusUptime, double elementalStatusUptime) {
    public static StatusUptimeParameters damageBonusParameter(UserInputsDto userInputsDto) {
        return new StatusUptimeParameters(userInputsDto.characterStatInputDto().statusAdditionalDamageBonus().physicalAdditionalDamageBonus(), userInputsDto.characterStatInputDto().statusAdditionalDamageBonus().elementalAdditionalDamageBonus());
    }

    public double physicalDamageBonusMultiplier() {
        return 1 + (physicalStatusUptime / 100);
    }

    public double elementalDamageBonusMultiplier() {
        return 1 + (elementalStatusUptime / 100);
    }
}
