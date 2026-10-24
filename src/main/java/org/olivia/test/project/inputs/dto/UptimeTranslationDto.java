package org.olivia.test.project.inputs.dto;

public record UptimeTranslationDto(double physicalStatusUptime, double elementalStatusUptime) {
    public static UptimeTranslationDto defaultUptimeAssumptionsInputDto() {
        return new UptimeTranslationDto(10, 80);
    }
}
