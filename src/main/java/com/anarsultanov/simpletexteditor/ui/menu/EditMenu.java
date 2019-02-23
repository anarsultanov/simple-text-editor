package com.anarsultanov.simpletexteditor.ui.menu;

import com.anarsultanov.simpletexteditor.SimpleTextEditor;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class EditMenu extends JMenu {

    private JMenuItem undo;
    private JMenuItem redo;

    public EditMenu(SimpleTextEditor simpleTextEditor) {
        super("Edit");
        undo = new JMenuItem("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undo.addActionListener(e -> {
            simpleTextEditor.getUndoManager().undo();
            if (simpleTextEditor.getUndoManager().canUndo()) {
                undo.setEnabled(true);
            } else {
                undo.setEnabled(false);
            }
        });
        undo.setEnabled(false);
        this.add(undo);

        redo = new JMenuItem("Redo");
        this.add(redo);
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redo.addActionListener(e -> {
            simpleTextEditor.getUndoManager().redo();
            if (simpleTextEditor.getUndoManager().canRedo()) {
                redo.setEnabled(true);
            } else {
                redo.setEnabled(false);
            }
        });
        redo.setEnabled(false);
        this.add(undo);

        this.addSeparator();

        JMenuItem cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        this.add(cut);

        JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        this.add(copy);

        JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        this.add(paste);

        JMenuItem delete = new JMenuItem("Delete");
        delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        delete.addActionListener(e -> simpleTextEditor.getTextArea()
                .replaceRange("", simpleTextEditor.getTextArea().getSelectionStart(), simpleTextEditor.getTextArea().getSelectionEnd()));
        this.add(delete);

        this.addSeparator();

        JMenuItem select = new JMenuItem("Select all");
        select.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        select.addActionListener(e -> simpleTextEditor.getTextArea().selectAll());
        this.add(select);
    }

    public void enableUndo() {
        undo.setEnabled(true);
    }

    public void enableRedo() {
        redo.setEnabled(true);
    }
}
