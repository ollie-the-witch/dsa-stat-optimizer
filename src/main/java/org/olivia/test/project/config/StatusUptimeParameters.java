package org.olivia.test.project.config;

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
}
