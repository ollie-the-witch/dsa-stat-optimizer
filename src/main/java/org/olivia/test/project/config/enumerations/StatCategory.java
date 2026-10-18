package org.olivia.test.project.config.enumerations;

public enum StatCategory {
    MAIN("Main"),
    SUBSTAT("Sub"),
    RUNE("Rune"),
    ;

    public final String alias;

    StatCategory(String alias) {
        this.alias = alias;
    }
}
