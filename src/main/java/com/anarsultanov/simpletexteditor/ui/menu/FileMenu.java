package com.anarsultanov.simpletexteditor.ui.menu;

import com.anarsultanov.simpletexteditor.ui.FileHandler;
import com.anarsultanov.simpletexteditor.SimpleTextEditor;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class FileMenu extends JMenu {

    public FileMenu(SimpleTextEditor simpleTextEditor, FileHandler fileHandler) {
        super("File");
        JMenuItem open = new JMenuItem("Open");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        this.add(open);
        open.addActionListener(e -> {
            fileHandler.saveOld();
            if (fileHandler.getFileChooser().showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                fileHandler.readInFile(fileHandler.getFileChooser().getSelectedFile().getAbsolutePath());
                simpleTextEditor.setDocumentListener();
                simpleTextEditor.getAutoSuggestionPopUpWindow().setVisible(false);
            }
        });

        JMenuItem save = new JMenuItem("Save");
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        this.add(save);
        save.addActionListener(e -> {
            if (!fileHandler.getCurrentFileName().equals("Untitled")) {
                fileHandler.saveFile(fileHandler.getCurrentFileName());
            } else {
                fileHandler.saveFileAs();
            }
        });

        JMenuItem saveAs = new JMenuItem("Save As");
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
        this.add(saveAs);
        saveAs.addActionListener(e -> fileHandler.saveFileAs());

        this.addSeparator();

        JMenuItem exit = new JMenuItem("Exit");
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        this.add(exit);
        exit.addActionListener(e -> {
            fileHandler.saveOld();
            System.exit(0);
        });
    }
}
