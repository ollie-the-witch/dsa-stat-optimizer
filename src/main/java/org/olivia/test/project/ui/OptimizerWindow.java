package org.olivia.test.project.ui;

import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.optimizationModel.OptimizationModelBundle;
import org.olivia.test.project.optimizationSolver.OptimizationRunResult;
import org.olivia.test.project.orchestrator.OptimizerOrchestrator;

import javax.swing.*;
import java.awt.*;

public class OptimizerWindow extends JFrame {

    // Template
    private final JTextField attackField = new JTextField("3757", 10);
    private final JButton optimizeButton = new JButton("Optimize");

    public OptimizerWindow() {
        super("Upgrade Optimizer");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Character-sheet attack:"));
        form.add(attackField);
        form.add(new JLabel()); // spacer in the second column
        form.add(optimizeButton);

        setContentPane(form);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        optimizeButton.addActionListener(event -> handleOptimize());
    }

    private void handleOptimize() {
        try {
            // Simulate reading input
            int attack = Integer.parseInt(attackField.getText().trim());
            Inputs inputs = OptimizerOrchestrator.loadInput();
            optimizeButton.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Read attack value: " + attack);


            OptimizerOrchestrator orchestrator = new OptimizerOrchestrator(inputs);


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
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Invalid input", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a whole number for character-sheet attack.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }
}