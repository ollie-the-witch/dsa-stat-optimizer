package org.olivia.test.project.ui.inputs.dto;

import java.util.List;

public record CharacterStatInputUiDto(String characterSheetAttack, String physicalAdditionalAttackPower, String elementalAdditionalAttackPower,
                                      String physicalAdditionalDamageBonus, String elementalAdditionalDamageBonus, List<SourceRow> attackPowerUpSources,
                                      List<SourceRow> statusAttackPowerUpSources, List<SourceRow> criticalRateSources, List<SourceRow> criticalDamageSources) {
    public CharacterStatInputUiDto {
        attackPowerUpSources = List.copyOf(attackPowerUpSources);
        statusAttackPowerUpSources = List.copyOf(statusAttackPowerUpSources);
        criticalRateSources = List.copyOf(criticalRateSources);
        criticalDamageSources = List.copyOf(criticalDamageSources);
    }

    public record SourceRow(String source, String percent) {
    }
}
