package org.olivia.test.project.config;

import org.olivia.test.project.config.enumerations.StatCategory;

import java.util.HashMap;
import java.util.Map;

public record SelectCount(Map<StatCategory, Integer> count) {
    public static SelectCount defaultSelectCount() {
        Map<StatCategory, Integer> count = new HashMap<>();
        count.put(StatCategory.MAIN, 1);
        count.put(StatCategory.SUBSTAT, 4);

        return new SelectCount(count);
    }
}
