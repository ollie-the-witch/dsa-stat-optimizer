package org.olivia.test.project.ui.inputs;

import org.olivia.test.project.inputs.dto.CriticalBoundsInputDto;
import org.olivia.test.project.inputs.ui.CriticalBoundsInputUiDto;

import javax.swing.*;

final class CriticalBoundsSection extends JPanel {
    private final JTextField minimum;
    private final JTextField maximum;
    private final JTextField cap;

    CriticalBoundsSection(CriticalBoundsInputDto defaults) {
        minimum = InputSectionSupport.textField(defaults.minimum());
        maximum = InputSectionSupport.textField(defaults.maximum());
        cap = InputSectionSupport.textField(defaults.cap());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(InputSectionSupport.createSection("Critical Bounds",
                InputSectionSupport.inputRow("Preferred Minimum (%)", minimum, defaults.minimum()),
                InputSectionSupport.inputRow("Preferred Maximum (%)", maximum, defaults.maximum()),
                InputSectionSupport.inputRow("Hard cap", cap, defaults.cap())));
    }

    CriticalBoundsInputUiDto readUiData() {
        return new CriticalBoundsInputUiDto(minimum.getText(), maximum.getText(), cap.getText());
    }
}
