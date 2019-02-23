package com.anarsultanov.simpletexteditor.ui.menu;

import com.anarsultanov.simpletexteditor.SimpleTextEditor;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;

public class RightClickMenu extends JPopupMenu {

    public RightClickMenu(SimpleTextEditor simpleTextEditor) {
        JMenuItem rcmCut = new JMenuItem(new DefaultEditorKit.CutAction());
        rcmCut.setText("Cut");
        this.add(rcmCut);

        JMenuItem rcmCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
        rcmCopy.setText("Copy");
        this.add(rcmCopy);

        JMenuItem rcmPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
        rcmPaste.setText("Paste");
        this.add(rcmPaste);

        JMenuItem rcmDelete = new JMenuItem("Delete");
        rcmDelete.addActionListener(e -> simpleTextEditor.getTextArea()
                .replaceRange("", simpleTextEditor.getTextArea().getSelectionStart(), simpleTextEditor.getTextArea().getSelectionEnd()))        ;
        this.add(rcmDelete);

        this.addSeparator();

        JMenuItem rcmSelect = new JMenuItem("Select all");
        rcmSelect.addActionListener(e -> simpleTextEditor.getTextArea().selectAll());
        this.add(rcmSelect);
    }
}
