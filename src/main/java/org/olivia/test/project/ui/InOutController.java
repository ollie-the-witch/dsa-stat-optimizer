package org.olivia.test.project.ui;

import org.olivia.test.project.inputs.InputService;
import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;
import org.olivia.test.project.orchestrator.OptimizerBackendOrchestrator;
import org.olivia.test.project.ui.inputs.OptimizerInputPanel;
import org.olivia.test.project.ui.results.ResultsPanel;

import javax.swing.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class InOutController {

    private final InputService inputService = new InputService();

    private final OptimizerWindow parentOptimizerWindow;
    private final Inputs defaultInputs;

    private final OptimizerInputPanel inputPanel;
    private final ResultsPanel resultsPanel;
    
    private final JButton optimizeButton;
    private final JLabel statusLabel;


    InOutController(OptimizerWindow parentOptimizerWindow,
            Inputs defaultInputs,
            OptimizerInputPanel inputPanel,
            ResultsPanel resultsPanel) {
        this.parentOptimizerWindow = parentOptimizerWindow;

        this.defaultInputs = defaultInputs;

        this.inputPanel = inputPanel;
        this.resultsPanel = resultsPanel;

        this.optimizeButton = parentOptimizerWindow.optimizeButton();
        this.statusLabel = parentOptimizerWindow.statusLabel();
    }

    void handleOptimize() {

        if (!readInput(inputService, inputPanel, defaultInputs)) return;

        final OptimizerBackendOrchestrator backendOrchestrator = createOrchestratorWithInput(defaultInputs);
        if (backendOrchestrator == null) return;

        optimizeButton.setEnabled(false);
        statusLabel.setText("Optimizing...");

        new SwingWorker<OptimizationRunResult, Void>() {
            @Override
            protected OptimizationRunResult doInBackground() {
                OptimizationModelBundle model = backendOrchestrator.createOptimizationModel();
                return backendOrchestrator.solverOptimizationProblem(model);
            }

            @Override
            protected void done() {
                optimizeButton.setEnabled(true);
                displayResults();
            }

            private void displayResults() {
                try {
                    OptimizationRunResult result = get();
                    backendOrchestrator.printResultToConsole(defaultInputs, result);
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

    private OptimizerBackendOrchestrator createOrchestratorWithInput(Inputs defaultInputs) {
        try {
            return new OptimizerBackendOrchestrator(defaultInputs);
        } catch (IllegalArgumentException exception) {
            showInputError(exception.getMessage());
            return null;
        }
    }

    private boolean readInput(InputService inputService, OptimizerInputPanel inputPanel, Inputs defaultInputs) {
        try {
        OptimizerInputPanel.FormData formData = inputPanel.readUiData();
        inputService.readCharacterStatInputDto(formData.characterStats(), defaultInputs);
        inputService.readUptimeAssumptionsInputDto(formData.uptime(), defaultInputs);
        inputService.readCriticalBoundsInputDto(formData.criticalBounds(), defaultInputs);
        inputService.readSolverSettingsInputDto(formData.solverSettings(), defaultInputs);
        } catch (Exception exception) {
            showInputError(exception.getMessage());
            return false;
        }
        return true;
    }

    private void showInputError(String message) {
        JOptionPane.showMessageDialog(parentOptimizerWindow, message == null ? "An unexpected error occurred." : message, "Input or optimization error", JOptionPane.ERROR_MESSAGE);
    }

}
