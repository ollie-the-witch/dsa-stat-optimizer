package org.olivia.test.project.ui;

import org.olivia.test.project.inputs.InputService;
import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;
import org.olivia.test.project.orchestrator.OptimizerOrchestrator;
import org.olivia.test.project.ui.inputs.OptimizerInputPanel;
import org.olivia.test.project.ui.results.ResultsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;


public class OptimizerWindow extends JFrame {
    private final Inputs defaultInputs = OptimizerOrchestrator.loadInput();
    private final InputService inputService = new InputService();
    private final OptimizerInputPanel inputPanel;
    private final JButton optimizeButton = new JButton("Optimize");
    private final JLabel statusLabel = new JLabel(" ");
    private final ResultsPanel resultsPanel;
    private final JSplitPane mainSplitPane;

    public OptimizerWindow() {
        super("Upgrade Optimizer");
        inputPanel = new OptimizerInputPanel(defaultInputs);
        resultsPanel = new ResultsPanel(this);
        mainSplitPane = createWindowContent(inputPanel);
        setContentPane(mainSplitPane);
        configureWindow();
        optimizeButton.addActionListener(event -> handleOptimize());
    }


    private JSplitPane createWindowContent(OptimizerInputPanel inputsPanel) {
        JPanel footer = new JPanel(new BorderLayout(8, 8));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        footer.add(statusLabel, BorderLayout.CENTER);
        footer.add(optimizeButton, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout());
        content.add(inputsPanel, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);
        content.setMinimumSize(new Dimension(0, content.getPreferredSize().height));
        resultsPanel.setMinimumSize(new Dimension(0, 300));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, content, resultsPanel);
        splitPane.setResizeWeight(0.66);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerSize(2);
        return splitPane;
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 700));
        pack();
        mainSplitPane.setDividerLocation(mainSplitPane.getTopComponent().getPreferredSize().height);
        setLocationRelativeTo(null);
    }

    private void handleOptimize() {
        final OptimizerOrchestrator orchestrator;
        try {
            OptimizerInputPanel.FormData formData = inputPanel.readUiData();
            inputService.readCharacterStatInputDto(formData.characterStats(), defaultInputs);
            inputService.readUptimeAssumptionsInputDto(formData.uptime(), defaultInputs);
            inputService.readCriticalBoundsInputDto(formData.criticalBounds(), defaultInputs);
            inputService.readSolverSettingsInputDto(formData.solverSettings(), defaultInputs);

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
                    statusLabel.setText("Optimization complete.");
                    resultsPanel.showResults(defaultInputs, result);
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

}
