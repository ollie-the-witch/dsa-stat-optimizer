package org.olivia.test.project.ui;

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
            int attack = Integer.parseInt(attackField.getText().trim());
            JOptionPane.showMessageDialog(this, "Read attack value: " + attack);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a whole number for character-sheet attack.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }
}