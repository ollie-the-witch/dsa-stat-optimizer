package org.olivia.test.project.inputs.dto;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;

public record CharacterStatInputDto(int characterSheetAttack, List<AttackPowerUpSources> attackPowerUpSources, StatusAttackPower statusAttackPower,
                                    StatusAdditionalDamageBonus statusAdditionalDamageBonus, List<CriticalRateSources> criticalRateSources,
                                    List<CriticalDamageSources> criticalDamageSources) {

    public static CharacterStatInputDto defaultCharacterStatInput() {
        List<CharacterStatInputDto.AttackPowerUpSources> attackPowerUpSourcesList = List.of(new CharacterStatInputDto.AttackPowerUpSources("title", 11.6, true), new CharacterStatInputDto.AttackPowerUpSources("karma", 15, true), new CharacterStatInputDto.AttackPowerUpSources("battle will", 0, false));
        List<CharacterStatInputDto.CriticalRateSources> criticalRateSourcesList = List.of(new CharacterStatInputDto.CriticalRateSources("character sheet", 32), new CharacterStatInputDto.CriticalRateSources("awakening", 10), new CharacterStatInputDto.CriticalRateSources("skills", 10));
        List<CharacterStatInputDto.CriticalDamageSources> criticalDamageSourcesList = List.of(new CharacterStatInputDto.CriticalDamageSources("character sheet", 82.3));
        CharacterStatInputDto.StatusAdditionalDamageBonus statusAdditionalDamageBonus = new CharacterStatInputDto.StatusAdditionalDamageBonus(15, 35);
        CharacterStatInputDto.StatusAttackPower statusAttackPower = new CharacterStatInputDto.StatusAttackPower(100, 425, 0);

        return new CharacterStatInputDto(3757, attackPowerUpSourcesList, statusAttackPower, statusAdditionalDamageBonus, criticalRateSourcesList, criticalDamageSourcesList);
    }

    public double sumOptimizationAttackPowerUp() {
        return CollectionUtils.isNotEmpty(attackPowerUpSources) ? attackPowerUpSources.stream().filter(AttackPowerUpSources::optimizationFactor).map(AttackPowerUpSources::percent).mapToDouble(f -> f).sum() : 0d;
    }

    public double sumAdditionalAttackPowerUp() {
        return CollectionUtils.isNotEmpty(attackPowerUpSources) ? attackPowerUpSources.stream().filter(Predicate.not(AttackPowerUpSources::optimizationFactor)).map(AttackPowerUpSources::percent).mapToDouble(f -> f).sum() : 0d;
    }

    public double sumCriticalRate() {
        return CollectionUtils.isNotEmpty(criticalRateSources) ? criticalRateSources.stream().map(CriticalRateSources::percent).mapToDouble(f -> f).sum() : 0d;
    }

    public double sumCriticalDamage() {
        return CollectionUtils.isNotEmpty(criticalDamageSources) ? criticalDamageSources.stream().map(CriticalDamageSources::percent).mapToDouble(f -> f).sum() : 0d;
    }

    public record StatusAdditionalDamageBonus(double physicalAdditionalDamageBonus, double elementalAdditionalDamageBonus) {

    }

    public record StatusAttackPower(int physicalAdditionalAttackPower, int elementalAdditionalAttackPower, double existingStatusAttackPowerUp) {

    }

    public record AttackPowerUpSources(String source, double percent, boolean optimizationFactor) {
    }

    public record CriticalRateSources(String source, double percent) {
    }

    public record CriticalDamageSources(String source, double percent) {
    }
}
