package org.olivia.test.project.ui;

import org.olivia.test.project.inputs.Inputs;
import org.olivia.test.project.orchestrator.OptimizerBackendOrchestrator;
import org.olivia.test.project.ui.inputs.OptimizerInputPanel;
import org.olivia.test.project.ui.results.ResultsPanel;

import javax.swing.*;
import java.awt.*;


public class OptimizerWindow extends JFrame {

    private final JButton optimizeButton = new JButton("Optimize");
    private final JLabel statusLabel = new JLabel(" ");

    public OptimizerWindow() {
        super("Upgrade Optimizer");
        Inputs defaultInputs = OptimizerBackendOrchestrator.loadDefaultInput();

        OptimizerInputPanel inputPanel = new OptimizerInputPanel(defaultInputs);
        ResultsPanel resultsPanel = new ResultsPanel(this);

        JSplitPane mainSplitPane = createWindowContent(inputPanel, resultsPanel);
        configureWindow(mainSplitPane);

        InOutController controller = new InOutController(this, defaultInputs, inputPanel, resultsPanel);
        optimizeButton.addActionListener(event -> controller.handleOptimize());
    }

    public JButton optimizeButton() {
        return optimizeButton;
    }

    public JLabel statusLabel() {
        return statusLabel;
    }

    private JSplitPane createWindowContent(OptimizerInputPanel inputsPanel,
            ResultsPanel resultsPanel) {
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

    private void configureWindow(JSplitPane mainSplitPane) {
        setContentPane(mainSplitPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 700));
        pack();
        mainSplitPane.setDividerLocation(mainSplitPane.getTopComponent().getPreferredSize().height);
        setLocationRelativeTo(null);
    }


}
