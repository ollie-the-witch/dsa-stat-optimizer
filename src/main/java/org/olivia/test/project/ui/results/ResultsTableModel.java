package org.olivia.test.project.ui.results;

import org.olivia.test.project.data.StatusDamageBonus;
import org.olivia.test.project.data.StatusUptimeParameters;
import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.optimizationSolver.OptimizationResult;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;
import org.olivia.test.project.outputs.DamageCalculator;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

final class ResultsTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"Rank", "Status", "Time (s)", "Configured Uptime", "Zero Uptime", "Physical Only", "Elemental Only", "Full Uptime", "Build"};
    private final List<ResultRow> rows = new ArrayList<>();

    private static double calculateDamage(DamageCalculator calculator,
            OptimizationResult result,
            Inputs inputs,
            StatusUptimeParameters uptime,
            StatusDamageBonus damageBonus) {
        return calculator.calculateDamage(new DamageCalculator.DamageCalculatorPacket(result.finalStats(), inputs, uptime, damageBonus));
    }

    void setResults(Inputs inputs,
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

    OptimizationResult resultAt(int modelRow) {
        return rows.get(modelRow).result();
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

    private record ResultRow(OptimizationResult result, double configuredUptime, double zeroUptime, double physicalOnly, double elementalOnly, double fullUptime) {
    }
}
