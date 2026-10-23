package org.olivia.test.project.ui;

import org.olivia.test.project.inputs.ui.CharacterStatInputUiData;
import org.olivia.test.project.inputs.InputService;
import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.inputs.dto.CharacterStatInputDto;
import org.olivia.test.project.inputs.ui.CriticalBoundsInputUiDto;
import org.olivia.test.project.inputs.ui.SolverSettingsInputUiDto;
import org.olivia.test.project.inputs.ui.UptimeAssumptionsInputUiData;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;
import org.olivia.test.project.orchestrator.OptimizerOrchestrator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;


public class OptimizerWindow extends JFrame {
    private final Inputs defaultInputs = OptimizerOrchestrator.loadInput();
    private final InputService inputService = new InputService();
    private final CharacterDataField form;
    private final JButton optimizeButton = new JButton("Optimize");
    private final JLabel statusLabel = new JLabel(" ");

    public OptimizerWindow() {
        super("Upgrade Optimizer");
        form = createFormFields(defaultInputs.getCharacterStatInputDto());
        setContentPane(createWindowContent(form, defaultInputs.getCharacterStatInputDto()));
        configureWindow();
        optimizeButton.addActionListener(event -> handleOptimize());
    }


    private static JTextField textField(Object value) {
        return new JTextField(String.valueOf(value), 12);
    }

    private static JPanel inputRow(String label,
            JTextField field,
            Object defaultValue) {
        String defaultText = String.valueOf(defaultValue);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(event -> field.setText(defaultText));

        JPanel fieldAndReset = new JPanel(new BorderLayout(4, 0));
        fieldAndReset.add(field, BorderLayout.CENTER);
        fieldAndReset.add(resetButton, BorderLayout.EAST);

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.add(new JLabel(label), BorderLayout.CENTER);
        row.add(fieldAndReset, BorderLayout.EAST);
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

    private CharacterDataField createFormFields(CharacterStatInputDto defaults) {
        return new CharacterDataField(textField(defaults.characterSheetAttack()),
                textField(defaults.statusAttackPower().physicalAdditionalAttackPower()),
                textField(defaults.statusAttackPower().elementalAdditionalAttackPower()),
                textField(defaults.statusAdditionalDamageBonus().physicalAdditionalDamageBonus()),
                textField(defaults.statusAdditionalDamageBonus().elementalAdditionalDamageBonus()),
                CharacterDataUISection.sourceRows("Attack Power Up Sources", CharacterDataUISection.attackPowerUpRows(defaults)),
                CharacterDataUISection.sourceRows("Status Attack Power Up", CharacterDataUISection.statusAttackPowerUpRows(defaults)),
                CharacterDataUISection.sourceRows("Critical Rate Sources", CharacterDataUISection.criticalRateRows(defaults)),
                CharacterDataUISection.sourceRows("Critical Damage Sources", CharacterDataUISection.criticalDamageRows(defaults)));
    }

    private JPanel createWindowContent(CharacterDataField fields,
            CharacterStatInputDto defaults) {
        JPanel sections = new JPanel();
        sections.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        sections.setLayout(new BoxLayout(sections, BoxLayout.Y_AXIS));
        sections.add(CharacterDataUISection.createCharacterStatsSection(fields, defaults));
        sections.add(Box.createVerticalStrut(8));
        sections.add(CharacterDataUISection.createAttackPowerSection(fields));
        sections.add(Box.createVerticalStrut(8));
        sections.add(CharacterDataUISection.createCriticalStatsSection(fields));
        sections.add(Box.createVerticalStrut(8));
        sections.add(CharacterDataUISection.createAdditionalDamageSection(fields, defaults));

        JPanel footer = new JPanel(new BorderLayout(8, 8));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        footer.add(statusLabel, BorderLayout.CENTER);
        footer.add(optimizeButton, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout());
        content.add(new JScrollPane(sections), BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);
        return content;
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
            inputService.readCharacterStatInputDto(CharacterDataUISection.readCharacterStatUiData(form), defaultInputs);
            inputService.readUptimeAssumptionsInputDto(UptimeDataUiSection.readUptimeDataUiData(form), defaultInputs);
            inputService.readCriticalBoundsInputDto(CriticalBoundsUiSection.readCriticalBoundsUiData(form),defaultInputs);
            inputService.readSolverSettingsInputDto(SolverSettingsUiSection.readSolverSettingsUiData(form),defaultInputs);

            System.out.println("Debug Input Stats:");
            System.out.println(defaultInputs.getSolverSettingsInputDto());
            System.out.println(defaultInputs.getUptimeAssumptionsInputDto());
            System.out.println(defaultInputs.getCriticalBoundsInputDto());
            System.out.println(defaultInputs.getCharacterStatInputDto());
            orchestrator = new OptimizerOrchestrator(defaultInputs);
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
                    orchestrator.printResultToConsole(defaultInputs, result);
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


    private void showInputError(String message) {
        JOptionPane.showMessageDialog(this, message == null ? "An unexpected error occurred." : message, "Input or optimization error", JOptionPane.ERROR_MESSAGE);
    }

    private static class CharacterDataUISection {

        private static CharacterStatInputUiData readCharacterStatUiData(CharacterDataField form) {
            return new CharacterStatInputUiData(form.attack().getText(),
                    form.physicalAttack().getText(),
                    form.elementalAttack().getText(),
                    form.physicalAdditionalDamageBonus().getText(),
                    form.elementalAdditionalDamageBonus().getText(),
                    form.attackPowerUpSources().readRows(),
                    form.statusAttackPowerUpSources().readRows(),
                    form.criticalRateSources().readRows(),
                    form.criticalDamageSources().readRows());
        }

        private static JPanel createCharacterStatsSection(CharacterDataField fields,
                CharacterStatInputDto defaults) {
            return createSection("Character Stats",
                    inputRow("Character-sheet attack", fields.attack(), defaults.characterSheetAttack()),
                    inputRow("Physical additional attack", fields.physicalAttack(), defaults.statusAttackPower().physicalAdditionalAttackPower()),
                    inputRow("Elemental additional attack", fields.elementalAttack(), defaults.statusAttackPower().elementalAdditionalAttackPower()));
        }

        private static JPanel createAttackPowerSection(CharacterDataField fields) {
            return createSection("Attack Power Up", fields.attackPowerUpSources(), fields.statusAttackPowerUpSources());
        }

        private static JPanel createCriticalStatsSection(CharacterDataField fields) {
            return createSection("Critical Stats", fields.criticalRateSources(), fields.criticalDamageSources());
        }

        private static JPanel createAdditionalDamageSection(CharacterDataField fields,
                CharacterStatInputDto defaults) {
            return createSection("Additional Damage Bonus",
                    inputRow("Physical additional damage bonus (%)",
                            fields.physicalAdditionalDamageBonus(),
                            defaults.statusAdditionalDamageBonus().physicalAdditionalDamageBonus()),
                    inputRow("Elemental additional damage bonus (%)",
                            fields.elementalAdditionalDamageBonus(),
                            defaults.statusAdditionalDamageBonus().elementalAdditionalDamageBonus()));
        }

        private static List<CharacterStatInputUiData.SourceRow> attackPowerUpRows(CharacterStatInputDto defaults) {
            return defaults.attackPowerUpSources().stream().map(source -> new CharacterStatInputUiData.SourceRow(source.source(), Double.toString(source.percent()))).toList();
        }

        private static List<CharacterStatInputUiData.SourceRow> statusAttackPowerUpRows(CharacterStatInputDto defaults) {
            return defaults.statusAttackPower().statusAttackPowerUpSources().stream().map(source -> new CharacterStatInputUiData.SourceRow(source.source(),
                    Double.toString(source.percent()))).toList();
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
    }

    private static class UptimeDataUiSection {
        private static UptimeAssumptionsInputUiData readUptimeDataUiData(CharacterDataField form) {
            return null;
        }
    }

    private static class CriticalBoundsUiSection {
        private static CriticalBoundsInputUiDto readCriticalBoundsUiData(CharacterDataField form) {
            return null;
        }
    }

    private static class SolverSettingsUiSection {
        private static SolverSettingsInputUiDto readSolverSettingsUiData(CharacterDataField form) {
            return null;
        }
    }

    private record CharacterDataField(JTextField attack, JTextField physicalAttack, JTextField elementalAttack, JTextField physicalAdditionalDamageBonus,
                                      JTextField elementalAdditionalDamageBonus, SourceRowsPanel attackPowerUpSources, SourceRowsPanel statusAttackPowerUpSources,
                                      SourceRowsPanel criticalRateSources, SourceRowsPanel criticalDamageSources) {
    }
}
