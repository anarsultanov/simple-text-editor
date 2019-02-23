package com.anarsultanov.simpletexteditor.ui.menu;

import com.anarsultanov.simpletexteditor.ui.frame.AboutFrame;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class HelpMenu extends JMenu {

    public HelpMenu() {
        super("Help");

        JMenuItem miAbout = new JMenuItem("About");
        miAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        miAbout.addActionListener(e -> {
            AboutFrame about = new AboutFrame();
            about.setTitle("About");
            about.setVisible(true);
        });
        this.add(miAbout);
    }
}
