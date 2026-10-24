package org.olivia.test.project.ui.inputs;

import org.olivia.test.project.inputs.dto.CharacterStatTranslationDto;
import org.olivia.test.project.ui.inputs.dto.CharacterStatInputUiDto;

import javax.swing.*;
import java.util.List;

final class CharacterDataSection {
    private final JTextField characterSheetAttack;
    private final JTextField physicalAdditionalAttack;
    private final JTextField elementalAdditionalAttack;
    private final JTextField physicalAdditionalDamageBonus;
    private final JTextField elementalAdditionalDamageBonus;
    private final SourceRowsPanel attackPowerUpSources;
    private final SourceRowsPanel statusAttackPowerUpSources;
    private final SourceRowsPanel criticalRateSources;
    private final SourceRowsPanel criticalDamageSources;
    private final JPanel generalAttackSection;
    private final JPanel statusAttackSection;
    private final JPanel criticalStatsSection;
    private final JPanel additionalDamageSection;

    CharacterDataSection(CharacterStatTranslationDto defaults) {
        characterSheetAttack = InputSectionSupport.textField(defaults.characterSheetAttack());
        physicalAdditionalAttack = InputSectionSupport.textField(defaults.statusAttackPower().physicalAdditionalAttackPower());
        elementalAdditionalAttack = InputSectionSupport.textField(defaults.statusAttackPower().elementalAdditionalAttackPower());
        physicalAdditionalDamageBonus = InputSectionSupport.textField(defaults.statusAdditionalDamageBonus().physicalAdditionalDamageBonus());
        elementalAdditionalDamageBonus = InputSectionSupport.textField(defaults.statusAdditionalDamageBonus().elementalAdditionalDamageBonus());
        attackPowerUpSources = sourceRows("Attack Power Up",
                defaults.attackPowerUpSources().stream().map(source -> new CharacterStatInputUiDto.SourceRow(source.source(), Double.toString(source.percent()))).toList());
        statusAttackPowerUpSources = sourceRows("Status Attack Power Up",
                defaults.statusAttackPower().statusAttackPowerUpSources().stream().map(source -> new CharacterStatInputUiDto.SourceRow(source.source(),
                        Double.toString(source.percent()))).toList());
        criticalRateSources = sourceRows("Critical Rate Sources",
                defaults.criticalRateSources().stream().map(source -> new CharacterStatInputUiDto.SourceRow(source.source(), Double.toString(source.percent()))).toList());
        criticalDamageSources = sourceRows("Critical Damage Sources",
                defaults.criticalDamageSources().stream().map(source -> new CharacterStatInputUiDto.SourceRow(source.source(), Double.toString(source.percent()))).toList());

        generalAttackSection = InputSectionSupport.createSection("General Attack",
                InputSectionSupport.inputRow("Character-Sheet Attack", characterSheetAttack, defaults.characterSheetAttack()),
                attackPowerUpSources);
        criticalStatsSection = InputSectionSupport.createSection("Critical Stats", criticalRateSources, criticalDamageSources);
        statusAttackSection = InputSectionSupport.createSection("Status Attack",
                InputSectionSupport.inputRow("Physical Additional Attack", physicalAdditionalAttack, defaults.statusAttackPower().physicalAdditionalAttackPower()),
                InputSectionSupport.inputRow("Elemental Additional Attack", elementalAdditionalAttack, defaults.statusAttackPower().elementalAdditionalAttackPower()),
                statusAttackPowerUpSources);
        additionalDamageSection = InputSectionSupport.createSection("Additional Damage Bonus",
                InputSectionSupport.inputRow("Physical Additional Damage Bonus (%)",
                        physicalAdditionalDamageBonus,
                        defaults.statusAdditionalDamageBonus().physicalAdditionalDamageBonus()),
                InputSectionSupport.inputRow("Elemental Additional Damage Bonus (%)",
                        elementalAdditionalDamageBonus,
                        defaults.statusAdditionalDamageBonus().elementalAdditionalDamageBonus()));
    }

    private static SourceRowsPanel sourceRows(String title,
            List<CharacterStatInputUiDto.SourceRow> defaults) {
        return new SourceRowsPanel(title, defaults);
    }

    JPanel generalAttackSection() {
        return generalAttackSection;
    }

    JPanel criticalStatsSection() {
        return criticalStatsSection;
    }

    JPanel statusAttackSection() {
        return statusAttackSection;
    }

    JPanel additionalDamageSection() {
        return additionalDamageSection;
    }

    CharacterStatInputUiDto readUiData() {
        return new CharacterStatInputUiDto(characterSheetAttack.getText(),
                physicalAdditionalAttack.getText(),
                elementalAdditionalAttack.getText(),
                physicalAdditionalDamageBonus.getText(),
                elementalAdditionalDamageBonus.getText(),
                attackPowerUpSources.readRows(),
                statusAttackPowerUpSources.readRows(),
                criticalRateSources.readRows(),
                criticalDamageSources.readRows());
    }
}
