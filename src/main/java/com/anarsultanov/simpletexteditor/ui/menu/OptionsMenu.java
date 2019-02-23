package com.anarsultanov.simpletexteditor.ui.menu;

import com.anarsultanov.simpletexteditor.SimpleTextEditor;
import com.anarsultanov.simpletexteditor.ui.frame.EditDistanceFrame;
import com.anarsultanov.simpletexteditor.ui.frame.TextGeneratorFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class OptionsMenu extends JMenu {

    public OptionsMenu(SimpleTextEditor simpleTextEditor) {
        super("Options");

        JMenuItem dist = new JMenuItem("Edit Distance");
        dist.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        dist.addActionListener(e -> {
            EditDistanceFrame editDistanceFrame = new EditDistanceFrame(simpleTextEditor.getNearbyWords());
            editDistanceFrame.setTitle("Edit Distance");
            editDistanceFrame.setVisible(true);
        });
        this.add(dist);

        JMenuItem gen = new JMenuItem("Generate Text");
        gen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        gen.addActionListener(e -> {
            if (simpleTextEditor.getTextArea().getText().length() >= 500) {
                TextGeneratorFrame texGenFrame = new TextGeneratorFrame(simpleTextEditor.getTextArea().getText());
                texGenFrame.setTitle("Markov Text Generator");
                texGenFrame.setVisible(true);
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(simpleTextEditor.getTextArea(), "Not enough words for the generator.\nInsert text with at least 500 characters.");
            }
        });
        this.add(gen);

        this.addSeparator();

        JCheckBoxMenuItem chkbxSpell = new JCheckBoxMenuItem("SpellChecking");
        chkbxSpell.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        chkbxSpell.addActionListener(e -> {
            if (chkbxSpell.isSelected()) {
                simpleTextEditor.setSpellcheck(true);
                simpleTextEditor.highlightIncorrectWords();
            } else {
                simpleTextEditor.setSpellcheck(false);
                simpleTextEditor.getHighlighter().removeAllHighlights();
            }
        });
        this.add(chkbxSpell);

        JCheckBoxMenuItem chkbxSugg = new JCheckBoxMenuItem("AutoSuggestions");
        chkbxSugg.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        chkbxSugg.addActionListener(e -> {
            if (chkbxSugg.isSelected()) {
                simpleTextEditor.setSuggestions(true);
            } else {
                simpleTextEditor.setSuggestions(false);
                simpleTextEditor.getAutoSuggestionPopUpWindow().setVisible(false);
            }
        });
        this.add(chkbxSugg);
    }
}
