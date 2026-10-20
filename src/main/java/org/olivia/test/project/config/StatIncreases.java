package org.olivia.test.project.config;

import org.olivia.test.project.config.enumerations.StatVariableName;

import java.util.HashMap;
import java.util.Map;

public record StatIncreases(Map<StatVariableName, int[]> mainStats, Map<StatVariableName, int[]> subStats, Map<StatVariableName, Integer> runes) {
    public static StatIncreases defaultStatIncreases() {
        Map<StatVariableName, int[]> main = new HashMap<>();
        main.put(StatVariableName.ATTACK_POWER_UP, new int[]{2386, 3408, 3068, 2386, 2386});
        main.put(StatVariableName.CRITICAL_RATE, new int[]{1164, 1664, 1500, 1164, 1164});
        main.put(StatVariableName.CRITICAL_DAMAGE, new int[]{1872, 2676, 2408, 1872, 1872});

        Map<StatVariableName, int[]> sub = new HashMap<>();
        sub.put(StatVariableName.ATTACK, new int[]{25, 35, 32, 25, 25});
        sub.put(StatVariableName.ATTACK_POWER_UP, new int[]{318, 454, 409, 318, 318});
        sub.put(StatVariableName.CRITICAL_RATE, new int[]{155, 222, 200, 155, 155});
        sub.put(StatVariableName.CRITICAL_DAMAGE, new int[]{250, 357, 321, 250, 250});
        sub.put(StatVariableName.PHYSICAL_ATTACK, new int[]{175, 250, 225, 175, 175});
        sub.put(StatVariableName.ELEMENTAL_ATTACK, new int[]{87, 125, 112, 87, 87});
        sub.put(StatVariableName.STATUS_ATTACK_POWER_UP, new int[]{388, 555, 500, 388, 388});

        Map<StatVariableName, Integer> rune = new HashMap<>();
        rune.put(StatVariableName.ATTACK, 140);
        rune.put(StatVariableName.CRITICAL_RATE, 850);
        rune.put(StatVariableName.CRITICAL_DAMAGE, 1400);

        return new StatIncreases(main, sub, rune);
    }
}
