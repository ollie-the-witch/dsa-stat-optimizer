package org.olivia.test.project.data.enumerations;

import org.olivia.test.project.data.config.OptimizationConfigurations;

public enum StatVariable {
    ATTACK("atk", "Attack", false), ATTACK_POWER_UP("atkUp", "Attack Power Up", true), CRITICAL_RATE("cr", "Critical Rate", true), CRITICAL_DAMAGE("cd", "Critical Damage", true)
    , PHYSICAL_ATTACK("physAtk", "Physical Status Attack", false), ELEMENTAL_ATTACK("eleAtk", "Elemental Status Attack", false), STATUS_ATTACK_POWER_UP("statusAtkUp",
            "Status Attack Power Up", true),
    ;

    public final String abbrev;
    public final String alias;
    public final boolean scale;

    StatVariable(String abbrev,
            String alias,
            boolean scale) {
        this.abbrev = abbrev;
        this.alias = alias;
        this.scale = scale;
    }

    public static Double convertScaledToReadable(boolean scale,
            int scaled) {
        return scale ? scaled * 100 / OptimizationConfigurations.getDoubleScale() : null;
    }
}
