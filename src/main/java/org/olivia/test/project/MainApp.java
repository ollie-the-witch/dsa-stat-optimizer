package org.olivia.test.project;


import org.olivia.test.project.ui.OptimizerWindow;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {


        SwingUtilities.invokeLater(() -> new OptimizerWindow().setVisible(true));


        //OptimizerOrchestrator orchestrator = new OptimizerOrchestrator();
        //Inputs inputs = orchestrator.loadInput();
        //orchestrator.solveOptimizationProblem(inputs);
    }
}