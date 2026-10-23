package org.olivia.test.project.ui;

import org.olivia.test.project.inputs.InputService;
import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.inputs.dto.CharacterStatInputDto;
import org.olivia.test.project.inputs.dto.CriticalBoundsInputDto;
import org.olivia.test.project.inputs.dto.SolverSettingsInputDto;
import org.olivia.test.project.inputs.dto.UptimeAssumptionsInputDto;
import org.olivia.test.project.inputs.ui.CharacterStatInputUiData;
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
    private final CharacterDataForm characterDataForm;
    private final UptimeDataForm uptimeDataForm;
    private final CriticalBoundsForm criticalBoundsForm;
    private final SolverSettingsForm solverSettingsForm;
    private final JButton optimizeButton = new JButton("Optimize");
    private final JLabel statusLabel = new JLabel(" ");

    public OptimizerWindow() {
        super("Upgrade Optimizer");
        characterDataForm = createFormFields(defaultInputs.getCharacterStatInputDto());
        uptimeDataForm = createFormFields(defaultInputs.getUptimeAssumptionsInputDto());
        criticalBoundsForm = createFormFields(defaultInputs.getCriticalBoundsInputDto());
        solverSettingsForm = createFormFields(defaultInputs.getSolverSettingsInputDto());
        setContentPane(createWindowContent(characterDataForm, uptimeDataForm, criticalBoundsForm, solverSettingsForm, defaultInputs));
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

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.add(new JLabel(label));
        row.add(field);
        row.add(resetButton);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        return row;
    }

    private static JPanel createSection(String title,
            Component... components) {
        JPanel section = new JPanel();
        section.setBorder(BorderFactory.createTitledBorder(title));
        section.setLayout(new GridBagLayout());
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

    private static JPanel verticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        return panel;
    }

    private static void addColumn(JPanel container,
            Component component,
            int column,
            int row,
            double weightX) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.weightx = weightX;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(0, column == 0 ? 0 : 8, 0, 0);
        container.add(component, constraints);
    }

    private CharacterDataForm createFormFields(CharacterStatInputDto defaults) {
        return new CharacterDataForm(textField(defaults.characterSheetAttack()),
                textField(defaults.statusAttackPower().physicalAdditionalAttackPower()),
                textField(defaults.statusAttackPower().elementalAdditionalAttackPower()),
                textField(defaults.statusAdditionalDamageBonus().physicalAdditionalDamageBonus()),
                textField(defaults.statusAdditionalDamageBonus().elementalAdditionalDamageBonus()),
                CharacterDataUISection.sourceRows("Attack Power Up", CharacterDataUISection.attackPowerUpRows(defaults)),
                CharacterDataUISection.sourceRows("Status Attack Power Up", CharacterDataUISection.statusAttackPowerUpRows(defaults)),
                CharacterDataUISection.sourceRows("Critical Rate Sources", CharacterDataUISection.criticalRateRows(defaults)),
                CharacterDataUISection.sourceRows("Critical Damage Sources", CharacterDataUISection.criticalDamageRows(defaults)));
    }

    private UptimeDataForm createFormFields(UptimeAssumptionsInputDto defaults) {
        return new UptimeDataForm(textField(defaults.physicalStatusUptime()), textField(defaults.elementalStatusUptime()));
    }

    private CriticalBoundsForm createFormFields(CriticalBoundsInputDto defaults) {
        return new CriticalBoundsForm(textField(defaults.minimum()), textField(defaults.maximum()), textField(defaults.cap()));
    }

    private SolverSettingsForm createFormFields(SolverSettingsInputDto defaults) {
        return new SolverSettingsForm(textField(defaults.resultCount()), textField(defaults.timeLimit()), textField(defaults.workerCount()));
    }

    private JPanel createWindowContent(CharacterDataForm characterFields,
            UptimeDataForm uptimeFields,
            CriticalBoundsForm criticalBoundsFields,
            SolverSettingsForm solverSettingsFields,
            Inputs defaults) {
        CharacterStatInputDto characterDefaults = defaults.getCharacterStatInputDto();
        JPanel sections = new JPanel(new GridBagLayout());
        sections.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel characterColumn = verticalPanel();
        characterColumn.add(CharacterDataUISection.createGeneralAttackSection(characterFields, characterDefaults));
        characterColumn.add(Box.createVerticalStrut(8));
        characterColumn.add(CharacterDataUISection.createCriticalStatsSection(characterFields));

        JPanel criticalAndSolverColumn = verticalPanel();
        criticalAndSolverColumn.add(CriticalBoundsUiSection.createSection(criticalBoundsFields, defaults.getCriticalBoundsInputDto()));
        criticalAndSolverColumn.add(Box.createVerticalStrut(8));
        criticalAndSolverColumn.add(SolverSettingsUiSection.createSection(solverSettingsFields, defaults.getSolverSettingsInputDto()));

        JPanel uptimeColumn = verticalPanel();
        uptimeColumn.add(CharacterDataUISection.createStatusAttackSection(characterFields, characterDefaults));
        uptimeColumn.add(Box.createVerticalStrut(8));
        uptimeColumn.add(CharacterDataUISection.createAdditionalDamageSection(characterFields, characterDefaults));
        uptimeColumn.add(Box.createVerticalStrut(8));
        uptimeColumn.add(UptimeDataUiSection.createSection(uptimeFields, defaults.getUptimeAssumptionsInputDto()));

        addColumn(sections, characterColumn, 0, 0, 1.4);
        addColumn(sections, uptimeColumn, 1, 0, 1);
        addColumn(sections, criticalAndSolverColumn, 2, 0, 1);

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
        setMinimumSize(new Dimension(1050, 680));
        pack();
        setLocationRelativeTo(null);
    }

    private void handleOptimize() {
        final OptimizerOrchestrator orchestrator;
        try {
            inputService.readCharacterStatInputDto(CharacterDataUISection.readCharacterStatUiData(characterDataForm), defaultInputs);
            inputService.readUptimeAssumptionsInputDto(UptimeDataUiSection.readUptimeDataUiData(uptimeDataForm), defaultInputs);
            inputService.readCriticalBoundsInputDto(CriticalBoundsUiSection.readCriticalBoundsUiData(criticalBoundsForm), defaultInputs);
            inputService.readSolverSettingsInputDto(SolverSettingsUiSection.readSolverSettingsUiData(solverSettingsForm), defaultInputs);

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

        private static CharacterStatInputUiData readCharacterStatUiData(CharacterDataForm form) {
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

        private static JPanel createGeneralAttackSection(CharacterDataForm fields,
                CharacterStatInputDto defaults) {
            return createSection("General Attack",
                    inputRow("Character-sheet attack", fields.attack(), defaults.characterSheetAttack()),
                    fields.attackPowerUpSources());
        }

        private static JPanel createStatusAttackSection(CharacterDataForm fields,
                CharacterStatInputDto defaults) {
            return createSection("Status Attack",
                    inputRow("Physical additional attack", fields.physicalAttack(), defaults.statusAttackPower().physicalAdditionalAttackPower()),
                    inputRow("Elemental additional attack", fields.elementalAttack(), defaults.statusAttackPower().elementalAdditionalAttackPower()),
                    fields.statusAttackPowerUpSources());
        }

        private static JPanel createCriticalStatsSection(CharacterDataForm fields) {
            return createSection("Critical Stats", fields.criticalRateSources(), fields.criticalDamageSources());
        }

        private static JPanel createAdditionalDamageSection(CharacterDataForm fields,
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
        private static JPanel createSection(UptimeDataForm fields,
                UptimeAssumptionsInputDto defaults) {
            return OptimizerWindow.createSection("Uptime",
                    inputRow("Physical status uptime (%)", fields.physicalStatusUptime(), defaults.physicalStatusUptime()),
                    inputRow("Elemental status uptime (%)", fields.elementalStatusUptime(), defaults.elementalStatusUptime()));
        }

        private static UptimeAssumptionsInputUiData readUptimeDataUiData(UptimeDataForm form) {
            return new UptimeAssumptionsInputUiData(form.physicalStatusUptime().getText(), form.elementalStatusUptime().getText());
        }
    }

    private static class CriticalBoundsUiSection {
        private static JPanel createSection(CriticalBoundsForm fields,
                CriticalBoundsInputDto defaults) {
            return OptimizerWindow.createSection("Critical Bounds",
                    inputRow("Minimum (%)", fields.minimum(), defaults.minimum()),
                    inputRow("Maximum (%)", fields.maximum(), defaults.maximum()),
                    inputRow("Hard cap", fields.cap(), defaults.cap()));
        }

        private static CriticalBoundsInputUiDto readCriticalBoundsUiData(CriticalBoundsForm form) {
            return new CriticalBoundsInputUiDto(form.minimum().getText(), form.maximum().getText(), form.cap().getText());
        }
    }

    private static class SolverSettingsUiSection {
        private static JPanel createSection(SolverSettingsForm fields,
                SolverSettingsInputDto defaults) {
            return OptimizerWindow.createSection("Solver Settings",
                    inputRow("Result count", fields.resultCount(), defaults.resultCount()),
                    inputRow("Time limit (s)", fields.timeLimit(), defaults.timeLimit()),
                    inputRow("Workers", fields.workerCount(), defaults.workerCount()));
        }

        private static SolverSettingsInputUiDto readSolverSettingsUiData(SolverSettingsForm form) {
            return new SolverSettingsInputUiDto(form.resultCount().getText(), form.timeLimit().getText(), form.workerCount().getText());
        }
    }

    private record CharacterDataForm(JTextField attack, JTextField physicalAttack, JTextField elementalAttack, JTextField physicalAdditionalDamageBonus,
                                     JTextField elementalAdditionalDamageBonus, SourceRowsPanel attackPowerUpSources, SourceRowsPanel statusAttackPowerUpSources,
                                     SourceRowsPanel criticalRateSources, SourceRowsPanel criticalDamageSources) {
    }

    private record UptimeDataForm(JTextField physicalStatusUptime, JTextField elementalStatusUptime) {
    }

    private record CriticalBoundsForm(JTextField minimum, JTextField maximum, JTextField cap) {
    }

    private record SolverSettingsForm(JTextField resultCount, JTextField timeLimit, JTextField workerCount) {
    }
}
