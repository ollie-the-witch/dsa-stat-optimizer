package org.olivia.test.project.ui.inputs;

import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.ui.inputs.dto.CharacterStatInputUiData;
import org.olivia.test.project.ui.inputs.dto.CriticalBoundsInputUiDto;
import org.olivia.test.project.ui.inputs.dto.SolverSettingsInputUiDto;
import org.olivia.test.project.ui.inputs.dto.UptimeAssumptionsInputUiData;

import javax.swing.*;
import java.awt.*;

public final class OptimizerInputPanel extends JPanel {
    private final CharacterDataSection characterData;
    private final UptimeSection uptime;
    private final CriticalBoundsSection criticalBounds;
    private final SolverSettingsSection solverSettings;

    public OptimizerInputPanel(Inputs defaults) {
        characterData = new CharacterDataSection(defaults.getCharacterStatInputDto());
        uptime = new UptimeSection(defaults.getUptimeAssumptionsInputDto());
        criticalBounds = new CriticalBoundsSection(defaults.getCriticalBoundsInputDto());
        solverSettings = new SolverSettingsSection(defaults.getSolverSettingsInputDto());

        JPanel characterColumn = InputSectionSupport.verticalPanel();
        characterColumn.add(characterData.generalAttackSection());
        characterColumn.add(Box.createVerticalStrut(8));
        characterColumn.add(characterData.criticalStatsSection());

        JPanel uptimeColumn = InputSectionSupport.verticalPanel();
        uptimeColumn.add(characterData.statusAttackSection());
        uptimeColumn.add(Box.createVerticalStrut(8));
        uptimeColumn.add(characterData.additionalDamageSection());
        uptimeColumn.add(Box.createVerticalStrut(8));
        uptimeColumn.add(uptime);

        JPanel solverColumn = InputSectionSupport.verticalPanel();
        solverColumn.add(criticalBounds);
        solverColumn.add(Box.createVerticalStrut(8));
        solverColumn.add(solverSettings);

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        InputSectionSupport.addColumn(this, characterColumn, 0, 1.4);
        InputSectionSupport.addColumn(this, uptimeColumn, 1, 1);
        InputSectionSupport.addColumn(this, solverColumn, 2, 1);
    }

    public FormData readUiData() {
        return new FormData(characterData.readUiData(), uptime.readUiData(), criticalBounds.readUiData(), solverSettings.readUiData());
    }

    public record FormData(CharacterStatInputUiData characterStats, UptimeAssumptionsInputUiData uptime, CriticalBoundsInputUiDto criticalBounds,
                           SolverSettingsInputUiDto solverSettings) {
    }
}
