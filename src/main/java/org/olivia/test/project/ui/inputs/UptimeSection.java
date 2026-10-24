package org.olivia.test.project.ui.inputs;

import org.olivia.test.project.inputs.dto.UptimeAssumptionsInputDto;
import org.olivia.test.project.ui.inputs.dto.UptimeAssumptionsInputUiData;

import javax.swing.*;

final class UptimeSection extends JPanel {
    private final JTextField physicalStatusUptime;
    private final JTextField elementalStatusUptime;

    UptimeSection(UptimeAssumptionsInputDto defaults) {
        physicalStatusUptime = InputSectionSupport.textField(defaults.physicalStatusUptime());
        elementalStatusUptime = InputSectionSupport.textField(defaults.elementalStatusUptime());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(InputSectionSupport.createSection("Estimated Status Uptime",
                InputSectionSupport.inputRow("Physical Uptime (up to 100%)", physicalStatusUptime, defaults.physicalStatusUptime()),
                InputSectionSupport.inputRow("Elemental Uptime (up to 100%)", elementalStatusUptime, defaults.elementalStatusUptime())));
    }

    UptimeAssumptionsInputUiData readUiData() {
        return new UptimeAssumptionsInputUiData(physicalStatusUptime.getText(), elementalStatusUptime.getText());
    }
}
