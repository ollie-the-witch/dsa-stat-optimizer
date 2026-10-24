package org.olivia.test.project.inputs.dto;

public record CriticalBoundsTranslationDto(Double minimum, Double maximum, double cap) {
    public CriticalBoundsTranslationDto {
        if (cap < maximum) {
            throw new IllegalArgumentException("The hard cap must not be lower than the preferred maximum.");
        }

    }

    public static CriticalBoundsTranslationDto defaultCriticalBoundsInputSettings() {
        return new CriticalBoundsTranslationDto(20d, 100d, 100);
    }
}
