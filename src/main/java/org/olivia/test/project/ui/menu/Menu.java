package org.olivia.test.project.ui.menu;


import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.olivia.test.project.ui.OptimizerWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu implements ActionListener {

    private final OptimizerWindow frame;


    private final JMenuBar menuBar = new JMenuBar();

    private final JMenu themeMenu = new JMenu("Theme");

    private final JMenuItem macLightTheme = new JMenuItem("Mac Light");
    private final JMenuItem macDarkTheme = new JMenuItem("Mac Dark (Dark)");
    private final JMenuItem flatLightTheme = new JMenuItem("Flat Light");
    private final JMenuItem flatDarkTheme = new JMenuItem("Flat Dark (Grey)");
    private final JMenuItem intellijLightTheme = new JMenuItem("IntelliJ Light");
    private final JMenuItem intellijDarkTheme = new JMenuItem("IntelliJ Dark (Grey)");

    public Menu(OptimizerWindow frame) {
        this.frame = frame;
    }

    public void createMenuBar() {
        frame.setJMenuBar(menuBar);
        addThemeMenu();
    }

    private void addThemeMenu() {
        menuBar.add(themeMenu);
        addThemeItem(themeMenu, macDarkTheme);
        addThemeItem(themeMenu, intellijDarkTheme);
        addThemeItem(themeMenu, flatDarkTheme);
        addThemeItem(themeMenu, macLightTheme);
        addThemeItem(themeMenu, intellijLightTheme);
        addThemeItem(themeMenu, flatLightTheme);
    }

    private void addThemeItem(JMenu menu,
            JMenuItem item) {
        menu.add(item);
        item.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == macLightTheme) {
            FlatMacLightLaf.setup();
        } else if (e.getSource() == macDarkTheme) {
            FlatMacDarkLaf.setup();
        } else if (e.getSource() == flatLightTheme) {
            FlatLightLaf.setup();
        } else if (e.getSource() == flatDarkTheme) {
            FlatDarkLaf.setup();
        } else if (e.getSource() == intellijLightTheme) {
            FlatIntelliJLaf.setup();
        } else if (e.getSource() == intellijDarkTheme) {
            FlatDarculaLaf.setup();
        }
        SwingUtilities.updateComponentTreeUI(frame);
    }
}