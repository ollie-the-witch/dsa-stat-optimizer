package org.olivia.test.project.config.enumerations;

public enum StatVariableName {
    ATTACK("atk"),
    ATTACK_POWER_UP("atkUp"),
    CRITICAL_RATE("cr"),
    CRITICAL_DAMAGE("cd"),
    PHYSICAL_ATTACK("physAtk"),
    ELEMENTAL_ATTACK("eleAtk"),
    STATUS_ATTACK_POWER_UP("statusAtkUp"),
    ;

    public final String aliases;

    StatVariableName(String aliases) {
        this.aliases = aliases;
    }
}
