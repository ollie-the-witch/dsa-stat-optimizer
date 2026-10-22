package org.olivia.test.project.ui;

import org.olivia.test.project.inputs.CharacterStatInputUiData;
import org.olivia.test.project.inputs.InputService;
import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.inputs.dto.CharacterStatInputDto;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;
import org.olivia.test.project.orchestrator.OptimizerOrchestrator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class OptimizerWindow extends JFrame {
    private final Inputs inputs = OptimizerOrchestrator.loadInput();
    private final InputService inputService = new InputService();
    private final FormFields form;
    private final JButton optimizeButton = new JButton("Optimize");
    private final JLabel statusLabel = new JLabel(" ");

    public OptimizerWindow() {
        super("Upgrade Optimizer");
        form = createFormFields(inputs.getCharacterStatInputDto());
        setContentPane(createWindowContent(form));
        configureWindow();
        optimizeButton.addActionListener(event -> handleOptimize());
    }

    private static List<CharacterStatInputUiData.SourceRow> attackPowerUpRows(CharacterStatInputDto defaults) {
        return defaults.attackPowerUpSources().stream().map(source -> new CharacterStatInputUiData.SourceRow(source.source(), Double.toString(source.percent()))).toList();
    }

    private static List<CharacterStatInputUiData.SourceRow> statusAttackPowerUpRows(CharacterStatInputDto defaults) {
        return defaults.statusAttackPower().statusAttackPowerUpSources().stream().map(source -> new CharacterStatInputUiData.SourceRow(source.source(), Double.toString(source.percent()))).toList();
    }

    private static List<CharacterStatInputUiData.SourceRow> criticalRateRows(CharacterStatInputDto defaults) {
        return defaults.criticalRateSources().stream().map(source -> new CharacterStatInputUiData.SourceRow(source.source(), Double.toString(source.percent()))).toList();
    }

    private static List<CharacterStatInputUiData.SourceRow> criticalDamageRows(CharacterStatInputDto defaults) {
        return defaults.criticalDamageSources().stream().map(source -> new CharacterStatInputUiData.SourceRow(source.source(), Double.toString(source.percent()))).toList();
    }

    private static SourceRowsPanel sourceRows(String title,
            List<CharacterStatInputUiData.SourceRow> defaults) {
        return new SourceRowsPanel(title, defaults);
    }

    private static JTextField textField(Object value) {
        return new JTextField(String.valueOf(value), 12);
    }

    private static JPanel inputRow(String label,
            JTextField field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.add(new JLabel(label), BorderLayout.CENTER);
        row.add(field, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return row;
    }

    private static JPanel createSection(String title,
            Component... components) {
        JPanel section = new JPanel();
        section.setBorder(BorderFactory.createTitledBorder(title));
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        for (Component component : components) {
            section.add(component);
        }
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        return section;
    }

    private FormFields createFormFields(CharacterStatInputDto defaults) {
        return new FormFields(textField(defaults.characterSheetAttack()), textField(defaults.statusAttackPower().physicalAdditionalAttackPower()), textField(defaults.statusAttackPower().elementalAdditionalAttackPower()), textField(defaults.statusAdditionalDamageBonus().physicalAdditionalDamageBonus()), textField(defaults.statusAdditionalDamageBonus().elementalAdditionalDamageBonus()), sourceRows("Attack Power Up Sources", attackPowerUpRows(defaults)), sourceRows("Status Attack Power Up", statusAttackPowerUpRows(defaults)), sourceRows("Critical Rate Sources", criticalRateRows(defaults)), sourceRows("Critical Damage Sources", criticalDamageRows(defaults)));
    }

    private JPanel createWindowContent(FormFields fields) {
        JPanel sections = new JPanel();
        sections.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        sections.setLayout(new BoxLayout(sections, BoxLayout.Y_AXIS));
        sections.add(createCharacterStatsSection(fields));
        sections.add(Box.createVerticalStrut(8));
        sections.add(createAttackPowerSection(fields));
        sections.add(Box.createVerticalStrut(8));
        sections.add(createCriticalStatsSection(fields));
        sections.add(Box.createVerticalStrut(8));
        sections.add(createAdditionalDamageSection(fields));

        JPanel footer = new JPanel(new BorderLayout(8, 8));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        footer.add(statusLabel, BorderLayout.CENTER);
        footer.add(optimizeButton, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout());
        content.add(new JScrollPane(sections), BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);
        return content;
    }

    private JPanel createCharacterStatsSection(FormFields fields) {
        return createSection("Character Stats", inputRow("Character-sheet attack", fields.attack()), inputRow("Physical additional attack", fields.physicalAttack()), inputRow("Elemental additional attack", fields.elementalAttack()));
    }

    private JPanel createAttackPowerSection(FormFields fields) {
        return createSection("Attack Power Up", fields.attackPowerUpSources(), fields.statusAttackPowerUpSources());
    }

    private JPanel createCriticalStatsSection(FormFields fields) {
        return createSection("Critical Stats", fields.criticalRateSources(), fields.criticalDamageSources());
    }

    private JPanel createAdditionalDamageSection(FormFields fields) {
        return createSection("Additional Damage Bonus", inputRow("Physical additional damage bonus (%)", fields.physicalAdditionalDamageBonus()), inputRow("Elemental additional damage bonus (%)", fields.elementalAdditionalDamageBonus()));
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(560, 680));
        pack();
        setLocationRelativeTo(null);
    }

    private void handleOptimize() {
        final OptimizerOrchestrator orchestrator;
        try {
            inputService.readCharacterStatInputDto(readCharacterStatUiData(), inputs);

            System.out.println("Debug Input Stats:");
            System.out.println(inputs.getSolverSettingsInputDto());
            System.out.println(inputs.getUptimeAssumptionsInputDto());
            System.out.println(inputs.getCriticalBoundsInputDto());
            System.out.println(inputs.getCharacterStatInputDto());
            orchestrator = new OptimizerOrchestrator(inputs);
        } catch (IllegalArgumentException exception) {
            showInputError(exception.getMessage());
            return;
        }

        optimizeButton.setEnabled(false);
        statusLabel.setText("Optimizing...");

        new SwingWorker<OptimizationRunResult, Void>() {
            @Override
            protected OptimizationRunResult doInBackground() {
                OptimizationModelBundle model = orchestrator.createOptimizationModel();
                return orchestrator.solverOptimzationProblem(model);
            }

            @Override
            protected void done() {
                optimizeButton.setEnabled(true);
                try {
                    OptimizationRunResult result = get();
                    orchestrator.printResultToConsole(inputs, result);
                    statusLabel.setText("Optimization complete. Results printed to the console.");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    statusLabel.setText("Optimization interrupted.");
                    showInputError("Optimization was interrupted.");
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    statusLabel.setText("Optimization failed.");
                    showInputError(cause == null ? exception.getMessage() : cause.getMessage());
                } catch (CancellationException exception) {
                    statusLabel.setText("Optimization cancelled.");
                    showInputError(exception.getMessage());
                }
            }
        }.execute();
    }

    private CharacterStatInputUiData readCharacterStatUiData() {
        return new CharacterStatInputUiData(form.attack().getText(), form.physicalAttack().getText(), form.elementalAttack().getText(), form.physicalAdditionalDamageBonus().getText(), form.elementalAdditionalDamageBonus().getText(), form.attackPowerUpSources().readRows(), form.statusAttackPowerUpSources().readRows(), form.criticalRateSources().readRows(), form.criticalDamageSources().readRows());
    }

    private void showInputError(String message) {
        JOptionPane.showMessageDialog(this, message == null ? "An unexpected error occurred." : message, "Input or optimization error", JOptionPane.ERROR_MESSAGE);
    }

    private record FormFields(JTextField attack, JTextField physicalAttack, JTextField elementalAttack, JTextField physicalAdditionalDamageBonus,
                              JTextField elementalAdditionalDamageBonus, SourceRowsPanel attackPowerUpSources, SourceRowsPanel statusAttackPowerUpSources,
                              SourceRowsPanel criticalRateSources, SourceRowsPanel criticalDamageSources) {
    }
}
