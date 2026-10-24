package org.olivia.test.project.data;

import org.olivia.test.project.data.enumerations.StatVariable;

import java.util.HashMap;
import java.util.Map;

public record StatIncreases(Map<StatVariable, int[]> mainStats, Map<StatVariable, int[]> subStats, Map<StatVariable, Integer> runes) {
    public static StatIncreases defaultStatIncreases() {
        Map<StatVariable, int[]> main = new HashMap<>();
        main.put(StatVariable.ATTACK_POWER_UP, new int[]{2386, 3408, 3068, 2386, 2386});
        main.put(StatVariable.CRITICAL_RATE, new int[]{1164, 1664, 1500, 1164, 1164});
        main.put(StatVariable.CRITICAL_DAMAGE, new int[]{1872, 2676, 2408, 1872, 1872});

        Map<StatVariable, int[]> sub = new HashMap<>();
        sub.put(StatVariable.ATTACK, new int[]{25, 35, 32, 25, 25});
        sub.put(StatVariable.ATTACK_POWER_UP, new int[]{318, 454, 409, 318, 318});
        sub.put(StatVariable.CRITICAL_RATE, new int[]{155, 222, 200, 155, 155});
        sub.put(StatVariable.CRITICAL_DAMAGE, new int[]{250, 357, 321, 250, 250});
        sub.put(StatVariable.PHYSICAL_ATTACK, new int[]{175, 250, 225, 175, 175});
        sub.put(StatVariable.ELEMENTAL_ATTACK, new int[]{87, 125, 112, 87, 87});
        //sub.put(StatVariable.STATUS_ATTACK_POWER_UP, new int[]{388, 555, 500, 388, 388});

        Map<StatVariable, Integer> rune = new HashMap<>();
        rune.put(StatVariable.ATTACK, 140);
        rune.put(StatVariable.CRITICAL_RATE, 850);
        rune.put(StatVariable.CRITICAL_DAMAGE, 1400);

        return new StatIncreases(main, sub, rune);
    }
}
