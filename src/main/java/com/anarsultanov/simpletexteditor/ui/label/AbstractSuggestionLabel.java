package com.anarsultanov.simpletexteditor.ui.label;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

abstract class AbstractSuggestionLabel extends JLabel {

    AbstractSuggestionLabel(String string) {
        super(string);
    }

    void replaceWithSuggestedText(JTextComponent textComponent, int wordPosition) {
        String suggestedWord = getText();
        String text = textComponent.getText();
        String textBeforePos = text.substring(0, wordPosition);
        String textAfterPos = text.substring(wordPosition);
        String temp;
        int wordsEnd;
        int wordsStart;
        temp = textAfterPos.replaceAll("[^a-zA-Z]", " ");
        if (temp.contains(" ")) {
            wordsEnd = wordPosition + temp.indexOf(" ");
        } else {
            wordsEnd = wordPosition + temp.length();
        }
        temp = textBeforePos.replaceAll("[^a-zA-Z]", " ");
        wordsStart = temp.lastIndexOf(" ") + 1;
        Document doc = textComponent.getDocument();
        try {
            doc.remove(wordsStart, wordsEnd - wordsStart);
            doc.insertString(wordsStart, suggestedWord, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
