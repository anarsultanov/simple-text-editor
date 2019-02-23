package com.anarsultanov.simpletexteditor.ui.label;

import com.anarsultanov.simpletexteditor.SimpleTextEditor;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SuggestionLabel extends AbstractSuggestionLabel {
    private boolean focused = false;
    private final SimpleTextEditor simpleTextEditor;
    private final JTextComponent textComponent;
    private final Color suggestionsTextColor = Color.BLUE;
    private final Color suggestionFocusedColor = Color.GRAY;

    public SuggestionLabel(String string, SimpleTextEditor simpleTextEditor) {
        super(string);
        this.simpleTextEditor = simpleTextEditor;
        this.textComponent = simpleTextEditor.getTextArea();
        initComponent();
    }

    private void initComponent() {
        setFocusable(true);
        setForeground(suggestionsTextColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                replaceWithSuggestedText(textComponent, textComponent.getCaretPosition());
                simpleTextEditor.getAutoSuggestionPopUpWindow().setVisible(false);
            }
        });

        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "Enter released");
        getActionMap().put("Enter released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                replaceWithSuggestedText(textComponent, textComponent.getCaretPosition());
                simpleTextEditor.getAutoSuggestionPopUpWindow().setVisible(false);
            }
        });
    }

    public void setFocused(boolean focused) {
        if (focused) {
            setBorder(new LineBorder(suggestionFocusedColor));
        } else {
            setBorder(null);
        }
        repaint();
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }
}