package com.anarsultanov.simpletexteditor.ui;

import com.anarsultanov.simpletexteditor.SimpleTextEditor;
import com.anarsultanov.simpletexteditor.ui.menu.RightClickMenu;

import javax.swing.*;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TextArea extends JTextArea {

    public TextArea(SimpleTextEditor simpleTextEditor) {
        JTextArea textArea = this;
        JWindow onclickSuggestionPopUpWindow = simpleTextEditor.getOnclickSuggestionPopUpWindow();
        JWindow autoSuggestionPopUpWindow = simpleTextEditor.getAutoSuggestionPopUpWindow();
        Highlighter highlighter = getHighlighter();
        RightClickMenu rightClickMenu = new RightClickMenu(simpleTextEditor);

        this.setWrapStyleWord(true);
        this.setLineWrap(true);

        this.addMouseListener(new MouseAdapter()
        {
            boolean clickOnHighlightedWord = false;
            public void mousePressed(MouseEvent e){
                if (e.getButton() == MouseEvent.BUTTON3){
                    checkClick(e);
                    if (!clickOnHighlightedWord && e.isPopupTrigger()){
                        onclickSuggestionPopUpWindow.setVisible(false);
                        doPop(e);
                    }
                }
            }

            public void mouseReleased(MouseEvent e){

                if (e.getButton() == MouseEvent.BUTTON3){
                    checkClick(e);
                    if (!clickOnHighlightedWord && e.isPopupTrigger()){
                        onclickSuggestionPopUpWindow.setVisible(false);
                        doPop(e);
                    }
                }

            }

            private void checkClick(MouseEvent e){
                Highlighter.Highlight[] hls = highlighter.getHighlights();
                Point pt = new Point(e.getX(), e.getY());
                int pos = textArea.viewToModel2D(pt);
                String clickedWord = getClickedWord(textArea.getText(), pos);
                System.out.println(clickedWord);
                for (Highlighter.Highlight hl: hls){
                    int start = hl.getStartOffset();
                    int end = hl.getEndOffset();
                    if (pos >= start && pos < end){
                        simpleTextEditor.showPossibleCorrections(clickedWord, pos);
                        clickOnHighlightedWord = true;
                        break;
                    }
                    clickOnHighlightedWord = false;
                }
            }

            private void doPop(MouseEvent e){
                rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
            }

            public void mouseClicked(MouseEvent me)
            {
                autoSuggestionPopUpWindow.setVisible(false);
                if (me.getButton() == MouseEvent.BUTTON1){
                    onclickSuggestionPopUpWindow.setVisible(false);
                }
            }
        });
        this.addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT){
                    autoSuggestionPopUpWindow.setVisible(false);
                }
                onclickSuggestionPopUpWindow.setVisible(false);


                if (autoSuggestionPopUpWindow.isVisible()) {
                    textArea.getActionMap().get("caret-down").setEnabled(false);
                } else {
                    textArea.getActionMap().get("caret-down").setEnabled(true);
                }
            }
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private String getClickedWord(String content, int caretPosition) {
        try {
            if (content.length() == 0) {
                return "";
            }
            //replace non breaking character with space
            content = content.replaceAll("[^A-Za-z0-9]", " ");
            int selectionStart = content.lastIndexOf(" ", caretPosition - 1);
            if (selectionStart == -1) {
                selectionStart = 0;
            } else {
                //ignore space character
                selectionStart += 1;
            }
            content = content.substring(selectionStart);
            int i = 0;
            String temp;
            int length = content.length();
            while (i != length && !(temp = content.substring(i, i + 1)).equals(" ") && !temp.equals("\n")) {
                i++;
            }
            content = content.substring(0, i);
            //int selectionEnd = content.length() + selectionStart;
            return content;
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }
}
