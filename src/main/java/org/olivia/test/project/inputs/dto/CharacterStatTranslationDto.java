package org.olivia.test.project.inputs.dto;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public record CharacterStatTranslationDto(int characterSheetAttack, List<AttackPowerUpSources> attackPowerUpSources, StatusAttackPower statusAttackPower,
                                          StatusAdditionalDamageBonus statusAdditionalDamageBonus, List<CriticalRateSources> criticalRateSources,
                                          List<CriticalDamageSources> criticalDamageSources) {

    public static CharacterStatTranslationDto defaultCharacterStatInput() {
        List<CharacterStatTranslationDto.AttackPowerUpSources> attackPowerUpSourcesList = List.of(new CharacterStatTranslationDto.AttackPowerUpSources("title", 11.6),
                new CharacterStatTranslationDto.AttackPowerUpSources("karma", 15));

        List<CharacterStatTranslationDto.CriticalRateSources> criticalRateSourcesList = List.of(new CharacterStatTranslationDto.CriticalRateSources("character sheet", 32),
                new CharacterStatTranslationDto.CriticalRateSources("awakening", 10),
                new CharacterStatTranslationDto.CriticalRateSources("skills", 10));
        List<CharacterStatTranslationDto.CriticalDamageSources> criticalDamageSourcesList = List.of(new CharacterStatTranslationDto.CriticalDamageSources("character sheet", 82.3));

        CharacterStatTranslationDto.StatusAdditionalDamageBonus statusAdditionalDamageBonus = new CharacterStatTranslationDto.StatusAdditionalDamageBonus(15, 35);
        List<CharacterStatTranslationDto.StatusAttackPowerUpSources> statusAttackPowerUpSourcesList = List.of(new CharacterStatTranslationDto.StatusAttackPowerUpSources("base", 0.0));
        CharacterStatTranslationDto.StatusAttackPower statusAttackPower = new CharacterStatTranslationDto.StatusAttackPower(100, 425, statusAttackPowerUpSourcesList);

        return new CharacterStatTranslationDto(3757, attackPowerUpSourcesList, statusAttackPower, statusAdditionalDamageBonus, criticalRateSourcesList, criticalDamageSourcesList);
    }

    public double sumAttackPowerUp() {
        return CollectionUtils.isNotEmpty(attackPowerUpSources) ? attackPowerUpSources.stream().map(AttackPowerUpSources::percent).mapToDouble(f -> f).sum() : 0d;
    }

    public double sumCriticalRate() {
        return CollectionUtils.isNotEmpty(criticalRateSources) ? criticalRateSources.stream().map(CriticalRateSources::percent).mapToDouble(f -> f).sum() : 0d;
    }

    public double sumCriticalDamage() {
        return CollectionUtils.isNotEmpty(criticalDamageSources) ? criticalDamageSources.stream().map(CriticalDamageSources::percent).mapToDouble(f -> f).sum() : 0d;
    }

    public record StatusAdditionalDamageBonus(double physicalAdditionalDamageBonus, double elementalAdditionalDamageBonus) {

    }

    public record StatusAttackPower(int physicalAdditionalAttackPower, int elementalAdditionalAttackPower, List<StatusAttackPowerUpSources> statusAttackPowerUpSources) {
        public StatusAttackPower {
            statusAttackPowerUpSources = List.copyOf(statusAttackPowerUpSources);
        }

        public double sumStatusAttackPowerUp() {
            return statusAttackPowerUpSources.stream().mapToDouble(StatusAttackPowerUpSources::percent).sum();
        }

    }

    public record StatusAttackPowerUpSources(String source, double percent) {
    }

    public record AttackPowerUpSources(String source, double percent) {
    }

    public record CriticalRateSources(String source, double percent) {
    }

    public record CriticalDamageSources(String source, double percent) {
    }
}
