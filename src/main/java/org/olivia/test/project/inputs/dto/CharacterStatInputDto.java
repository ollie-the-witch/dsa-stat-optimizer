package org.olivia.test.project.inputs.dto;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public record CharacterStatInputDto(int characterSheetAttack, StatusAttackPower statusAttackPower, AdditionalDamageBonus additionalDamageBonus,
                                    List<AttackPowerUpSources> attackPowerUpSources, List<CriticalRateSources> criticalRateSources,
                                    List<CriticalDamageSources> criticalDamageSources) {
    public double sumOptimizationAttackPowerUp() {
        return CollectionUtils.isNotEmpty(attackPowerUpSources) ?
                attackPowerUpSources.stream().filter(AttackPowerUpSources::optimizationFactor).map(AttackPowerUpSources::percent).mapToDouble(f -> f).sum() : 0d;
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

    public record AdditionalDamageBonus(double physicalAdditionalDamageBonus, double elementalAdditionalDamageBonus) {

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
