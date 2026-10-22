package org.olivia.test.project.inputs.dto;

public record UptimeAssumptionsInputDto(double physicalStatusUptime, double elementalStatusUptime) {
    public static UptimeAssumptionsInputDto defaultUptimeAssumptionsInputDto() {
        return new UptimeAssumptionsInputDto(10, 80);
    }
}
