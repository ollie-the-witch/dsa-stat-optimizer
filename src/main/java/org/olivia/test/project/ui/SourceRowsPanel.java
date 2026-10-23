package org.olivia.test.project.ui;

import org.olivia.test.project.inputs.ui.CharacterStatInputUiData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

final class SourceRowsPanel extends JPanel {
    private final List<SourceRowFields> rows = new ArrayList<>();
    private final JPanel rowsContainer = new JPanel();
    private final List<CharacterStatInputUiData.SourceRow> defaults;

    SourceRowsPanel(String title,
            List<CharacterStatInputUiData.SourceRow> defaults) {
        this.defaults = List.copyOf(defaults);
        setBorder(BorderFactory.createTitledBorder(title));
        setLayout(new BorderLayout(4, 4));

        rowsContainer.setLayout(new BoxLayout(rowsContainer, BoxLayout.Y_AXIS));
        add(rowsContainer, BorderLayout.CENTER);

        JButton addButton = new JButton("Add source");
        JButton resetButton = new JButton("Reset defaults");
        addButton.addActionListener(event -> addRow("", "0"));
        resetButton.addActionListener(event -> resetRows());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        controls.add(addButton);
        controls.add(resetButton);
        add(controls, BorderLayout.SOUTH);

        resetRows();
        setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    List<CharacterStatInputUiData.SourceRow> readRows() {
        return rows.stream().map(row -> new CharacterStatInputUiData.SourceRow(row.sourceField.getText(), row.percentField.getText())).toList();
    }

    private void resetRows() {
        rows.clear();
        rowsContainer.removeAll();
        for (CharacterStatInputUiData.SourceRow row : defaults) {
            addRow(row.source(), row.percent());
        }
        refreshRows();
    }

    private void addRow(String source,
            String percent) {
        SourceRowFields row = new SourceRowFields(source, percent);
        rows.add(row);

        JPanel rowPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 3, 2, 3);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.weightx = 0;
        rowPanel.add(new JLabel("Source"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        rowPanel.add(row.sourceField, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0;
        rowPanel.add(new JLabel("%"), constraints);

        constraints.gridx = 3;
        constraints.weightx = 0;
        rowPanel.add(row.percentField, constraints);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(event -> {
            rows.remove(row);
            rowsContainer.remove(rowPanel);
            refreshRows();
        });
        constraints.gridx = 4;
        constraints.weightx = 0;
        rowPanel.add(removeButton, constraints);

        rowsContainer.add(rowPanel);
        refreshRows();
    }

    private void refreshRows() {
        rowsContainer.revalidate();
        rowsContainer.repaint();
        revalidate();
        repaint();
    }

    private static final class SourceRowFields {
        private final JTextField sourceField;
        private final JTextField percentField;

        private SourceRowFields(String source,
                String percent) {
            sourceField = new JTextField(source, 16);
            percentField = new JTextField(percent, 8);
        }
    }
}
