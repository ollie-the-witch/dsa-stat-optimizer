package org.olivia.test.project.inputs;

import java.util.List;

public class CharacterStatInput {

    int characterSheetAttack = 3757;
    int physicalAdditionalAttackPower = 0;
    int elementalAdditionalAttackPower = 425;
    double existingStatusAttackPowerUp = 0;

    double physicalAdditionalDamageBonus = 15;
    double elementalAdditionalDamageBonus = 35;

    List<AttackPowerUpSources> attackPowerUpSources = List.of(new AttackPowerUpSources("title", 11.6), new AttackPowerUpSources("karma", 15));
    List<CriticalRateSources> criticalRateSources = List.of(new CriticalRateSources("character sheet", 32), new CriticalRateSources("awakening", 10));
    List<CriticalDamageSources> criticalDamageSources = List.of(new CriticalDamageSources("character sheet", 82.3));

    public CharacterStatInput(int characterSheetAttack, int physicalAdditionalAttackPower, int elementalAdditionalAttackPower, double existingStatusAttackPowerUp, double physicalAdditionalDamageBonus, double elementalAdditionalDamageBonus, List<AttackPowerUpSources> attackPowerUpSources, List<CriticalRateSources> criticalRateSources, List<CriticalDamageSources> criticalDamageSources) {
        this.characterSheetAttack = characterSheetAttack;
        this.physicalAdditionalAttackPower = physicalAdditionalAttackPower;
        this.elementalAdditionalAttackPower = elementalAdditionalAttackPower;
        this.existingStatusAttackPowerUp = existingStatusAttackPowerUp;
        this.physicalAdditionalDamageBonus = physicalAdditionalDamageBonus;
        this.elementalAdditionalDamageBonus = elementalAdditionalDamageBonus;
        this.attackPowerUpSources = attackPowerUpSources;
        this.criticalRateSources = criticalRateSources;
        this.criticalDamageSources = criticalDamageSources;
    }

    public static class AttackPowerUpSources {
        String source;
        double percent;

        public AttackPowerUpSources(String source, double percent) {
            this.source = source;
            this.percent = percent;
        }
    }

    public static class CriticalRateSources {
        String source;
        double percent;

        public CriticalRateSources(String source, double percent) {
            this.source = source;
            this.percent = percent;
        }
    }

    public static class CriticalDamageSources {
        String source;
        double percent;

        public CriticalDamageSources(String source, double percent) {
            this.source = source;
            this.percent = percent;
        }
    }
}
