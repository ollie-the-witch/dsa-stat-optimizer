package org.olivia.test.project.data;

import org.olivia.test.project.data.config.OptimizationConfigurations;
import org.olivia.test.project.inputs.dto.UserInputsDto;

/**
 * Record for passing information about damage uptime
 *
 * @param physicalStatusUptime
 * @param elementalStatusUptime
 */
public record StatusUptimeParameters(double physicalStatusUptime, double elementalStatusUptime) {
    public static StatusUptimeParameters damageUptimeParameter(UserInputsDto userInputsDto) {
        return new StatusUptimeParameters(userInputsDto.assumptionsInputDto().physicalStatusUptime(), userInputsDto.assumptionsInputDto().elementalStatusUptime());
    }

    public long physicalStatusUptimeScaledLong() {
        return Math.round(physicalStatusUptime / 100 * OptimizationConfigurations.getDoubleScale());
    }

    public long elementalStatusUptimeScaledLong() {
        return Math.round(elementalStatusUptime / 100 * OptimizationConfigurations.getDoubleScale());
    }
}
