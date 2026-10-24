package org.olivia.test.project.data;

import org.olivia.test.project.data.config.OptimizationConfigurations;
import org.olivia.test.project.inputs.dto.UptimeTranslationDto;

/**
 * Record for passing information about damage uptime
 *
 * @param physicalStatusUptime
 * @param elementalStatusUptime
 */
public record StatusUptimeParameters(double physicalStatusUptime, double elementalStatusUptime) {
    public static StatusUptimeParameters damageUptimeParameter(UptimeTranslationDto characterDataInputsDto) {
        return new StatusUptimeParameters(characterDataInputsDto.physicalStatusUptime(), characterDataInputsDto.elementalStatusUptime());
    }

    public long physicalStatusUptimeScaledLong() {
        return Math.round(physicalStatusUptime / 100 * OptimizationConfigurations.getDoubleScale());
    }

    public long elementalStatusUptimeScaledLong() {
        return Math.round(elementalStatusUptime / 100 * OptimizationConfigurations.getDoubleScale());
    }
}
