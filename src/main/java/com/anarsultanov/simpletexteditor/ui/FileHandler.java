package com.anarsultanov.simpletexteditor.ui;

import com.anarsultanov.simpletexteditor.SimpleTextEditor;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
    private String currentFileName = "Untitled";
    private JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
    private SimpleTextEditor simpleTextEditor;
    private boolean changed;

    public FileHandler(SimpleTextEditor simpleTextEditor) {
        this.simpleTextEditor = simpleTextEditor;
    }

    public void readInFile(String fileName) {
        try (FileReader r = new FileReader(fileName)) {
            simpleTextEditor.getTextArea().read(r, null);
            currentFileName = fileName;
            simpleTextEditor.getFrame().setTitle(currentFileName);
            changed = false;
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(simpleTextEditor.getTextArea(), "Editor can't find the file called " + fileName);
        }
    }

    public void saveFile(String fileName) {
        try (FileWriter w = new FileWriter(fileName)) {
            simpleTextEditor.getTextArea().write(w);
            currentFileName = fileName;
            simpleTextEditor.getFrame().setTitle(currentFileName);
            changed = false;
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(simpleTextEditor.getTextArea(), "Editor can't find the file called " + fileName);
        }
    }

    public void saveFileAs() {
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
            saveFile(fileChooser.getSelectedFile().getAbsolutePath());
    }

    public void saveOld() {
        if (changed) {
            if (JOptionPane.showConfirmDialog(simpleTextEditor.getTextArea(), "Would you like to save " + currentFileName + " ?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                saveFile(currentFileName);
        }
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
