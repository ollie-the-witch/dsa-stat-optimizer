package org.olivia.test.project.inputs.dto;

public class CriticalBoundsInputDto {
    private final Double minimum;
    private final Double maximum;
    private final double cap;

    public CriticalBoundsInputDto(Double minimum,
            Double maximum,
            double cap) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.cap = cap;

        if (this.cap < this.maximum) {
            throw new IllegalArgumentException();
        }
    }

    public CriticalBoundsInputDto(Double minimum,
            Double maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.cap = 100;

        if (this.cap < this.maximum) {
            throw new IllegalArgumentException();
        }
    }

    public static CriticalBoundsInputDto defaultCriticalBoundsInputSettings() {
        return new CriticalBoundsInputDto(20d, 100d, 100);
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
