package org.olivia.test.project.inputs.dto;

public class CriticalBoundsTranslationDto {
    private final Double minimum;
    private final Double maximum;
    private final double cap;

    public CriticalBoundsTranslationDto(Double minimum,
            Double maximum,
            double cap) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.cap = cap;

        if (this.cap < this.maximum) {
            throw new IllegalArgumentException();
        }
    }

    public CriticalBoundsTranslationDto(Double minimum,
            Double maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.cap = 100;

        if (this.cap < this.maximum) {
            throw new IllegalArgumentException();
        }
    }

    public static CriticalBoundsTranslationDto defaultCriticalBoundsInputSettings() {
        return new CriticalBoundsTranslationDto(20d, 100d, 100);
    }

    public Double minimum() {
        return minimum;
    }

    public Double maximum() {
        return maximum;
    }

    public double cap() {
        return cap;
    }
}
