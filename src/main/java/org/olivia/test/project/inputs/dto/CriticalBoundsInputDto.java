package org.olivia.test.project.inputs.dto;

public record CriticalBoundsInputDto(Double minimum, Double maximum, int cap) {
    public static CriticalBoundsInputDto defaultCriticalBoundsInputSettings() {
        return new CriticalBoundsInputDto(20d, 100d, 10000);
    }
}
