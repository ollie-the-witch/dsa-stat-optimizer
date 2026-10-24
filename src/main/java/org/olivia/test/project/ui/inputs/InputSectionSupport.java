package org.olivia.test.project.ui.inputs;

import javax.swing.*;
import java.awt.*;

final class InputSectionSupport {
    private InputSectionSupport() {
    }

    static JTextField textField(Object value) {
        return new JTextField(String.valueOf(value), 12);
    }

    static JPanel inputRow(String label,
            JTextField field,
            Object defaultValue) {
        String defaultText = String.valueOf(defaultValue);
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(event -> field.setText(defaultText));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.add(new JLabel(label));
        row.add(field);
        row.add(resetButton);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        return row;
    }

    static JPanel createSection(String title,
            Component... components) {
        JPanel section = new JPanel(new GridBagLayout());
        section.setBorder(BorderFactory.createTitledBorder(title));
        for (int row = 0; row < components.length; row++) {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.insets = new Insets(0, 0, row < components.length - 1 ? 4 : 0, 0);
            section.add(components[row], constraints);
        }
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        return section;
    }

    static JPanel verticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        return panel;
    }

    static void addColumn(JPanel container,
            Component component,
            int column,
            double weightX) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = 0;
        constraints.weightx = weightX;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(0, column == 0 ? 0 : 8, 0, 0);
        container.add(component, constraints);
    }
}
