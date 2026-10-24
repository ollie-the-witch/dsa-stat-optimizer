package org.olivia.test.project.ui.inputs;

import org.olivia.test.project.inputs.dto.SolverSettingsTranslationDto;
import org.olivia.test.project.ui.inputs.dto.SolverSettingsInputUiDto;

import javax.swing.*;

final class SolverSettingsSection extends JPanel {
    private final JTextField resultCount;
    private final JTextField timeLimit;
    private final JTextField workerCount;

    SolverSettingsSection(SolverSettingsTranslationDto defaults) {
        resultCount = InputSectionSupport.textField(defaults.resultCount());
        timeLimit = InputSectionSupport.textField(defaults.timeLimit());
        workerCount = InputSectionSupport.textField(defaults.workerCount());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(InputSectionSupport.createSection("Solver Settings",
                InputSectionSupport.inputRow("Result count", resultCount, defaults.resultCount()),
                InputSectionSupport.inputRow("Time limit (s)", timeLimit, defaults.timeLimit()),
                InputSectionSupport.inputRow("Workers", workerCount, defaults.workerCount())));
    }

    SolverSettingsInputUiDto readUiData() {
        return new SolverSettingsInputUiDto(resultCount.getText(), timeLimit.getText(), workerCount.getText());
    }
}
