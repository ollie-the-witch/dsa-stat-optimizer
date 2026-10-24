package org.olivia.test.project.ui.results;

import org.olivia.test.project.data.config.OptimizationConfigurations;
import org.olivia.test.project.data.enumerations.EquipmentSlots;
import org.olivia.test.project.data.enumerations.StatVariable;
import org.olivia.test.project.optimizationSolver.OptimizationResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

final class SolutionDetailsDialog extends JDialog {
    SolutionDetailsDialog(Window owner,
            OptimizationResult result) {
        super(owner, "Solution " + result.rank() + " — " + result.solverStatus(), ModalityType.MODELESS);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(createContent(result));
        pack();
        setLocationRelativeTo(owner);
    }

    private static void addContentSection(JPanel content,
            JComponent section,
            int row) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(row == 0 ? 0 : 10, 0, 0, 0);
        content.add(section, constraints);
    }

    private static void addStat(JPanel panel,
            String name,
            String spacer,
            String value) {
        JLabel nameLabel = new JLabel(name + ":");
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        panel.add(nameLabel);
        panel.add(new JLabel(spacer));
        panel.add(new JLabel(value));
    }

    private static void addEmptyStat(JPanel panel) {
        panel.add(new JLabel());
        panel.add(new JLabel());
    }

    private static Dimension sizeEquipmentColumns(JTable table) {
        FontMetrics metrics = table.getFontMetrics(table.getFont());
        int totalWidth = 0;
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            int preferredWidth = metrics.stringWidth(table.getColumnName(columnIndex)) + 24;
            for (int row = 0; row < table.getRowCount(); row++) {
                Object value = table.getValueAt(row, columnIndex);
                if (value != null) {
                    preferredWidth = Math.max(preferredWidth, metrics.stringWidth(value.toString()) + 24);
                }
            }
            table.getColumnModel().getColumn(columnIndex).setPreferredWidth(preferredWidth);
            totalWidth += preferredWidth;
        }
        Dimension tableSize = new Dimension(totalWidth, table.getRowHeight() * table.getRowCount());
        table.setPreferredSize(tableSize);
        return tableSize;
    }

    private static String formatInteger(StatVariable stat,
            OptimizationResult result) {
        return Integer.toString(result.finalStats().getOrDefault(stat, 0));
    }

    private JComponent createContent(OptimizationResult result) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        addContentSection(content, createFinalStats(result), 0);
        addContentSection(content, createEquipmentChoices(result), 1);
        addContentSection(content, createRuneAllocation(result), 2);
        return new JScrollPane(content);
    }

    private JPanel createFinalStats(OptimizationResult result) {
        JPanel stats = new JPanel(new GridLayout(0, 3, 12, 6));
        stats.setBorder(BorderFactory.createTitledBorder("Final Stats"));

        int scale = OptimizationConfigurations.getScale();
        int attack = result.finalStats().getOrDefault(StatVariable.ATTACK, 0);
        int attackPowerUp = result.finalStats().getOrDefault(StatVariable.ATTACK_POWER_UP, 0);
        int characterAttack = attack * (attackPowerUp + scale) / scale;

        String spacer = "      ";

        addStat(stats, "Character Attack", spacer, Integer.toString(characterAttack));
        addStat(stats, "Base Attack", spacer, Integer.toString(attack));
        addStat(stats, StatVariable.ATTACK_POWER_UP.alias, spacer, formatPercent(StatVariable.ATTACK_POWER_UP, result));
        addStat(stats, StatVariable.CRITICAL_RATE.alias, spacer, formatPercent(StatVariable.CRITICAL_RATE, result));
        addStat(stats, StatVariable.CRITICAL_DAMAGE.alias, spacer, formatPercent(StatVariable.CRITICAL_DAMAGE, result));
        addStat(stats, StatVariable.PHYSICAL_ATTACK.alias, spacer, formatInteger(StatVariable.PHYSICAL_ATTACK, result));
        addStat(stats, StatVariable.ELEMENTAL_ATTACK.alias, spacer, formatInteger(StatVariable.ELEMENTAL_ATTACK, result));
        addStat(stats, StatVariable.STATUS_ATTACK_POWER_UP.alias, spacer, formatPercent(StatVariable.STATUS_ATTACK_POWER_UP, result));
        return stats;
    }

    private JPanel createEquipmentChoices(OptimizationResult result) {
        String[] columns = {"Equipment", "Main Slot", "Sub Slot"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row,
                    int column) {
                return false;
            }
        };

        for (EquipmentSlots slot : EquipmentSlots.values()) {
            StatVariable mainStat = result.mainStats().get(slot);
            List<StatVariable> subStats = result.subStats().getOrDefault(slot, List.of());
            model.addRow(new Object[]{slot.alias, mainStat == null ? "" : mainStat.alias, subStats.stream().map(stat -> stat.alias).collect(Collectors.joining(", "))});
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        Dimension tableSize = sizeEquipmentColumns(table);
        JPanel tableContent = new JPanel(new BorderLayout());
        tableContent.add(table.getTableHeader(), BorderLayout.NORTH);
        tableContent.add(table, BorderLayout.CENTER);
        tableContent.setPreferredSize(new Dimension(tableSize.width, tableSize.height + table.getTableHeader().getPreferredSize().height));
        tableContent.setMaximumSize(tableContent.getPreferredSize());

        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder("Equipment Choices"));
        section.add(tableContent, BorderLayout.CENTER);
        return section;
    }

    private JPanel createRuneAllocation(OptimizationResult result) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder("Rune Allocation"));
        JPanel rows = new JPanel(new GridLayout(0, 3, 12, 4));
        for (var entry : result.runeCounts().entrySet()) {
            rows.add(new JLabel(entry.getKey().alias));
            rows.add(new JLabel("    "));
            rows.add(new JLabel(Integer.toString(entry.getValue())));
        }
        section.add(rows, BorderLayout.NORTH);
        return section;
    }

    private String formatPercent(StatVariable stat,
            OptimizationResult result) {
        int scaledValue = result.finalStats().getOrDefault(stat, 0);
        return new DecimalFormat("0.00").format(StatVariable.convertScaledToReadable(true, scaledValue)) + "%";
    }
}
