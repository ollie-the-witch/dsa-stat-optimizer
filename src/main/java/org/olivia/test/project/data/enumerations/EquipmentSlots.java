package org.olivia.test.project.data.enumerations;

public enum EquipmentSlots {
    HELMET("Helmet", 0), TOP("Top", 1), BOTTOM("Bottom", 2), GLOVES("Gloves", 3), BOOTS("Boots", 4),
    ;

    public final String alias;
    public final int index;

    EquipmentSlots(String alias,
            int index) {
        this.alias = alias;
        this.index = index;
    }
}
