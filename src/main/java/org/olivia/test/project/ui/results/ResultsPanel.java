package org.olivia.test.project.ui.results;

import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.optimizationSolver.OptimizationResult;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

public final class ResultsPanel extends JPanel {
    private static final int BUILD_COLUMN = 8;
    private final Window owner;
    private final ResultsTableModel tableModel = new ResultsTableModel();
    private final JLabel heading = new JLabel("Optimization Results: no results yet");
    private final JLabel stopReason = new JLabel(" ");
    private final JTable table = new JTable(tableModel);

    public ResultsPanel(Window owner) {
        this.owner = owner;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        configureTable();
        add(createContent(), BorderLayout.CENTER);
    }

    public void showResults(Inputs inputs,
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
                showBuild(tableModel.resultAt(modelRow));
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

    private void showBuild(OptimizationResult result) {
        new SolutionDetailsDialog(owner, result).setVisible(true);
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
