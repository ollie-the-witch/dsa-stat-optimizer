package org.olivia.test.project.ui;

import org.olivia.test.project.data.StatusDamageBonus;
import org.olivia.test.project.data.StatusUptimeParameters;
import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.optimizationSolver.OptimizationResult;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;
import org.olivia.test.project.outputs.DamageCalculator;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

final class ResultsPanel extends JPanel {
    private static final int BUILD_COLUMN = 8;
    private final Window owner;
    private final ResultsTableModel tableModel = new ResultsTableModel();
    private final JLabel heading = new JLabel("Optimization Results: no results yet");
    private final JLabel stopReason = new JLabel(" ");
    private final JTable table = new JTable(tableModel);

    ResultsPanel(Window owner) {
        this.owner = owner;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        configureTable();
        add(createContent(), BorderLayout.CENTER);
    }

    void showResults(Inputs inputs,
            OptimizationRunResult runResult) {
        tableModel.setResults(inputs, runResult);
        heading.setText(String.format("Optimization Results: %d of %d requested", runResult.solutions().size(), runResult.requestedResultCount()));
        stopReason.setText(runResult.stopReason());
    }

    private JComponent createContent() {
        JPanel content = new JPanel(new BorderLayout(8, 6));
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 15f));
        content.add(heading, BorderLayout.NORTH);

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(1120, table.getRowHeight() * 6 + table.getTableHeader().getPreferredSize().height + 4));
        content.add(tableScrollPane, BorderLayout.CENTER);

        content.add(stopReason, BorderLayout.SOUTH);
        return content;
    }

    private void configureTable() {
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);
        table.setRowHeight(28);

        TableRowSorter<ResultsTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setSortable(BUILD_COLUMN, false);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(3, SortOrder.DESCENDING)));
        table.setRowSorter(sorter);
        table.getTableHeader().setReorderingAllowed(false);

        for (int column = 2; column <= 7; column++) {
            table.getColumnModel().getColumn(column).setCellRenderer(new NumericRenderer(column == 2));
        }

        table.getColumnModel().getColumn(BUILD_COLUMN).setCellRenderer(new BuildButtonRenderer());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                int viewRow = table.rowAtPoint(event.getPoint());
                int viewColumn = table.columnAtPoint(event.getPoint());
                if (viewRow < 0 || viewColumn != BUILD_COLUMN) {
                    return;
                }
                int modelRow = table.convertRowIndexToModel(viewRow);
                showBuild(tableModel.rowAt(modelRow));
            }
        });
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        table.getColumnModel().getColumn(0).setPreferredWidth(48);
        table.getColumnModel().getColumn(1).setPreferredWidth(85);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        for (int column = 3; column <= 7; column++) {
            table.getColumnModel().getColumn(column).setPreferredWidth(125);
        }
        table.getColumnModel().getColumn(BUILD_COLUMN).setPreferredWidth(100);
    }

    private void showBuild(ResultRow row) {
        new SolutionDetailsDialog(owner, row.result()).setVisible(true);
    }

    private static final class ResultsTableModel extends AbstractTableModel {
        private static final String[] COLUMNS = {"Rank", "Status", "Time (s)", "Configured Uptime", "Zero Uptime", "Physical Only", "Elemental Only", "Full Uptime", "Build"};
        private final List<ResultRow> rows;

        private ResultsTableModel() {
            rows = new ArrayList<>();
        }

        private static double calculateDamage(DamageCalculator calculator,
                OptimizationResult result,
                Inputs inputs,
                StatusUptimeParameters uptime,
                StatusDamageBonus damageBonus) {
            return calculator.calculateDamage(new DamageCalculator.DamageCalculatorPacket(result.finalStats(), inputs, uptime, damageBonus));
        }

        private void setResults(Inputs inputs,
                OptimizationRunResult runResult) {
            StatusUptimeParameters configuredUptime = StatusUptimeParameters.damageUptimeParameter(inputs.getUptimeAssumptionsInputDto());
            StatusDamageBonus damageBonus = StatusDamageBonus.damageBonusParameter(inputs.getCharacterStatInputDto().statusAdditionalDamageBonus());
            DamageCalculator calculator = new DamageCalculator();
            rows.clear();

            for (OptimizationResult result : runResult.solutions()) {
                double configured = calculateDamage(calculator, result, inputs, configuredUptime, damageBonus);
                double zero = calculateDamage(calculator, result, inputs, new StatusUptimeParameters(0, 0), damageBonus);
                double physical = calculateDamage(calculator, result, inputs, new StatusUptimeParameters(100, 0), damageBonus);
                double elemental = calculateDamage(calculator, result, inputs, new StatusUptimeParameters(0, 100), damageBonus);
                double full = calculateDamage(calculator, result, inputs, new StatusUptimeParameters(100, 100), damageBonus);
                rows.add(new ResultRow(result, configured, zero, physical, elemental, full));
            }
            fireTableDataChanged();
        }

        private ResultRow rowAt(int modelRow) {
            return rows.get(modelRow);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return switch (column) {
                case 0 -> Integer.class;
                case 2, 3, 4, 5, 6, 7 -> Double.class;
                default -> String.class;
            };
        }

        @Override
        public Object getValueAt(int rowIndex,
                int columnIndex) {
            ResultRow row = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> row.result().rank();
                case 1 -> row.result().solverStatus();
                case 2 -> row.result().wallTimeSeconds();
                case 3 -> row.configuredUptime();
                case 4 -> row.zeroUptime();
                case 5 -> row.physicalOnly();
                case 6 -> row.elementalOnly();
                case 7 -> row.fullUptime();
                case 8 -> "Show Build";
                default -> throw new IndexOutOfBoundsException("Unknown results column: " + columnIndex);
            };
        }
    }

    private record ResultRow(OptimizationResult result, double configuredUptime, double zeroUptime, double physicalOnly, double elementalOnly, double fullUptime) {
    }

    private static final class NumericRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat format;

        private NumericRenderer(boolean time) {
            format = time ? new DecimalFormat("0.00") : new DecimalFormat("#,##0.000");
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            setText(value instanceof Number number ? format.format(number) : "");
        }
    }

    private static final class BuildButtonRenderer extends JButton implements TableCellRenderer {
        private BuildButtonRenderer() {
            setText("Show Build");
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {
            setBackground(selected ? table.getSelectionBackground() : table.getBackground());
            setForeground(selected ? table.getSelectionForeground() : table.getForeground());
            return this;
        }
    }

}
