package com.anarsultanov.simpletexteditor.ui.label;

import com.anarsultanov.simpletexteditor.SimpleTextEditor;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OnclickSuggestionLabel extends AbstractSuggestionLabel {
    private final SimpleTextEditor simpleTextEditor;
    private final JTextComponent textComponent;
    private final Color suggestionsTextColor = Color.RED.darker();
    private int wordPosition;

    public OnclickSuggestionLabel(String string, int pos, SimpleTextEditor simpleTextEditor) {
        super(string);
        this.wordPosition = pos;
        this.simpleTextEditor = simpleTextEditor;
        this.textComponent = simpleTextEditor.getTextArea();
        initComponent();
    }

    private void initComponent() {
        setForeground(suggestionsTextColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                replaceWithSuggestedText(textComponent, wordPosition);
                simpleTextEditor.getAutoSuggestionPopUpWindow().setVisible(false);
                simpleTextEditor.getOnclickSuggestionPopUpWindow().setVisible(false);
            }
        });
    }


}