package com.anarsultanov.simpletexteditor;

import com.anarsultanov.simpletexteditor.core.AutoCompleteDictionaryTrie;
import com.anarsultanov.simpletexteditor.core.NearbyWords;
import com.anarsultanov.simpletexteditor.core.SimpleDocument;
import com.anarsultanov.simpletexteditor.ui.FileHandler;
import com.anarsultanov.simpletexteditor.ui.FleschIndexPanel;
import com.anarsultanov.simpletexteditor.ui.TextArea;
import com.anarsultanov.simpletexteditor.ui.label.OnclickSuggestionLabel;
import com.anarsultanov.simpletexteditor.ui.label.SuggestionLabel;
import com.anarsultanov.simpletexteditor.ui.menu.EditMenu;
import com.anarsultanov.simpletexteditor.ui.menu.FileMenu;
import com.anarsultanov.simpletexteditor.ui.menu.HelpMenu;
import com.anarsultanov.simpletexteditor.ui.menu.OptionsMenu;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleTextEditor {

    private static final float OPACITY = 0.75f;

    private JFrame frame;
    private JTextArea textArea;
    private Highlighter highlighter;
    private DefaultHighlighter.DefaultHighlightPainter hPainter;

    private JPanel suggestionsPanel;
    private JWindow autoSuggestionPopUpWindow;
    private JWindow onclickSuggestionPopUpWindow;

    private AutoCompleteDictionaryTrie autoCompleteDictionaryTrie;
    private NearbyWords nearbyWords;
    private UndoManager undoManager;
    private FileHandler fileHandler;

    private EditMenu editMenu;

    private boolean suggestions = false;
    private boolean spellcheck = false;
    private int tW, tH;

    private SimpleTextEditor() {
        try {
            URL resource = SimpleTextEditor.class.getClassLoader().getResource("dict.txt");
            assert resource != null;
            Path path = Path.of(resource.toURI());
            autoCompleteDictionaryTrie = AutoCompleteDictionaryTrie.fromPath(path);
            nearbyWords = new NearbyWords(autoCompleteDictionaryTrie);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        undoManager = new UndoManager();
        fileHandler = new FileHandler(this);
        frame = new JFrame(fileHandler.getCurrentFileName());
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(new FileMenu(this, fileHandler));
        editMenu = new EditMenu(this);
        jMenuBar.add(editMenu);
        jMenuBar.add(new OptionsMenu(this));
        jMenuBar.add(new HelpMenu());

        frame.getContentPane().add(jMenuBar, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        autoSuggestionPopUpWindow = new JWindow(frame);
        autoSuggestionPopUpWindow.setOpacity(OPACITY);
        onclickSuggestionPopUpWindow = new JWindow(frame);
        onclickSuggestionPopUpWindow.setOpacity(OPACITY);
        suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new GridLayout(0, 1));
        suggestionsPanel.setBackground(Color.WHITE.brighter());
        textArea = new TextArea(this);
        scrollPane.setViewportView(textArea);
        highlighter = textArea.getHighlighter();
        hPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0xFF6464));
        frame.getContentPane().add(new FleschIndexPanel(textArea), BorderLayout.SOUTH);

        addKeyBindingToRequestFocusInPopUpWindow();
        setDocumentListener();

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new SimpleTextEditor().getFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public NearbyWords getNearbyWords() {
        return nearbyWords;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public JWindow getAutoSuggestionPopUpWindow() {
        return autoSuggestionPopUpWindow;
    }

    public JWindow getOnclickSuggestionPopUpWindow() {
        return onclickSuggestionPopUpWindow;
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public Highlighter getHighlighter() {
        return highlighter;
    }

    public void setSuggestions(boolean suggestions) {
        this.suggestions = suggestions;
    }

    public void setSpellcheck(boolean spellcheck) {
        this.spellcheck = spellcheck;
    }

    public void showPossibleCorrections(String clickedWord, int pos) {
        suggestionsPanel.removeAll();//remove previos words/jlabels that were added
        //used to calcualte size of JWindow as new Jlabels are added
        tW = 0;
        tH = 0;
        boolean suggestionAdded = false;
        int number = 6;
        List<String> suggestions = nearbyWords.suggestions(clickedWord, number);
        for (String sug : suggestions) {
            OnclickSuggestionLabel onClickLabel = new OnclickSuggestionLabel(sug, pos, this);
            addLabelToPanel(onClickLabel);
            suggestionAdded = true;
        }
        if (!suggestionAdded) {
            if (onclickSuggestionPopUpWindow.isVisible()) {
                onclickSuggestionPopUpWindow.setVisible(false);
            }
        } else {
            showOnclickPopUpWindow(pos);
            setFocusToTextField();
        }
    }

    public void highlightIncorrectWords() {
        highlighter.removeAllHighlights();
        String contText = textArea.getText();

        SimpleDocument document = new SimpleDocument(contText);
        Pattern pattern;
        Matcher matcher;
        for (String tok : document.getTokens("[a-zA-Z]+")) {
            if (!autoCompleteDictionaryTrie.isWord(tok)) {

                try {
                    pattern = Pattern.compile("\\b" + tok + "\\b");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                matcher = pattern.matcher(contText);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();

                    try {
                        highlighter.addHighlight(start, end, hPainter);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setDocumentListener() {
        Document doc = textArea.getDocument();
        doc.addUndoableEditListener(undoManager);
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (e.getLength() == 1) {
                    handleSpellChanges();
                }
                handleUndoRedoChanges();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                handleUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleUpdate(e);
            }

            private void handleUpdate(DocumentEvent e) {
                if (e.getLength() == 1) {
                    handleSpellChanges();
                } else {
                    autoSuggestionPopUpWindow.setVisible(false);
                }
                handleUndoRedoChanges();
            }

            private void handleSpellChanges() {
                if (spellcheck) {
                    highlightIncorrectWords();
                }
                if (suggestions) {
                    checkForAndShowSuggestions();
                }
            }

            private void handleUndoRedoChanges() {
                fileHandler.setChanged(true);
                if (undoManager.canUndo()) {
                    editMenu.enableUndo();
                }
                if (undoManager.canRedo()) {
                    editMenu.enableRedo();
                }
            }
        });
    }

    private void addKeyBindingToRequestFocusInPopUpWindow() {
        textArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
        textArea.getActionMap().put("Down released", new AbstractAction() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent ae) {//focuses the first label on pop window
                for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
                    if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
                        ((SuggestionLabel) suggestionsPanel.getComponent(i)).setFocused(true);
                        autoSuggestionPopUpWindow.toFront();
                        autoSuggestionPopUpWindow.requestFocusInWindow();
                        suggestionsPanel.requestFocusInWindow();
                        suggestionsPanel.getComponent(i).requestFocusInWindow();
                        break;
                    }
                }
            }
        });

        suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
        suggestionsPanel.getActionMap().put("Down released", new AbstractAction() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            int lastFocusableIndex = 0;

            @Override
            public void actionPerformed(ActionEvent ae) {//allows scrolling of labels in pop window (I know very hacky for now :))

                ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
                int max = sls.size();

                if (max > 1) {//more than 1 suggestion
                    for (int i = 0; i < max; i++) {
                        SuggestionLabel sl = sls.get(i);
                        if (sl.isFocused()) {
                            if (lastFocusableIndex == max - 1) {
                                lastFocusableIndex = 0;
                                sl.setFocused(false);
                                autoSuggestionPopUpWindow.setVisible(false);
                                setFocusToTextField();
                                checkForAndShowSuggestions();//fire method as if document listener change occured and fired it

                            } else {
                                sl.setFocused(false);
                                lastFocusableIndex = i;
                            }
                        } else if (lastFocusableIndex <= i) {
                            sl.setFocused(true);
                            autoSuggestionPopUpWindow.toFront();
                            autoSuggestionPopUpWindow.requestFocusInWindow();
                            suggestionsPanel.requestFocusInWindow();
                            suggestionsPanel.getComponent(i).requestFocusInWindow();
                            lastFocusableIndex = i;
                            break;
                        }
                    }
                } else { //only a single suggestion was given
                    autoSuggestionPopUpWindow.setVisible(false);
                    setFocusToTextField();
                    checkForAndShowSuggestions();//fire method as if document listener change occured and fired it
                }
            }
        });
    }

    private void checkForAndShowSuggestions() {
        String typedWord = getCurrentlyTypedWord();
        suggestionsPanel.removeAll();//remove previos words/jlabels that were added

        //used to calcualte size of JWindow as new Jlabels are added
        tW = 0;
        tH = 0;

        boolean added = wordTyped(typedWord);

        if (!added) {
            if (autoSuggestionPopUpWindow.isVisible()) {
                autoSuggestionPopUpWindow.setVisible(false);
            }
        } else {
            showAutoPopUpWindow();
            setFocusToTextField();
        }
    }

    private void setFocusToTextField() {
        frame.toFront();
        frame.requestFocusInWindow();
        textArea.requestFocusInWindow();
    }

    private void showOnclickPopUpWindow(int clickPosition) {
        Rectangle2D rect;
        try {
            rect = textArea.modelToView2D(clickPosition);
        } catch (BadLocationException ex) {
            rect = new Rectangle(0, 0);
        }
        showPopUpWindow(onclickSuggestionPopUpWindow, rect);
    }

    private void showAutoPopUpWindow() {
        Rectangle2D rect;
        int dotPosition = textArea.getCaretPosition();
        try {
            rect = textArea.modelToView2D(dotPosition);
        } catch (BadLocationException ex) {
            try { // when removeUpdate catch an exception and change the terms
                rect = textArea.modelToView2D(dotPosition - 1);
            } catch (BadLocationException e) {
                rect = new Rectangle(0, 0);
            }
        }
        showPopUpWindow(autoSuggestionPopUpWindow, rect);
    }

    private void showPopUpWindow(JWindow window, Rectangle2D rect) {
        window.getContentPane().add(suggestionsPanel);
        window.setMinimumSize(new Dimension(20, 30));
        window.setSize(tW, tH);
        window.setVisible(true);

        int windowX = (int) (frame.getX() + rect.getX() + textArea.getX() + 15);
        int windowY = (int) (frame.getY() + 20 + rect.getY() + textArea.getY() + (rect.getHeight() * 3));

        //show the pop up
        window.setLocation(windowX, windowY);
        window.setMinimumSize(new Dimension(20, 30));
        window.revalidate();
        window.repaint();
    }

    private String getCurrentlyTypedWord() {//get newest word after last white spaceif any or the first word if no white spaces
        String text = textArea.getText();
        int pos = textArea.getCaretPosition();
        if (pos < text.length()) {
            text = text.substring(0, pos + 1);
        }

        String wordBeingTyped;
        text = text.replaceAll("([\\r\\n])", " ");
        if (text.contains(" ")) {
            wordBeingTyped = text.substring(text.lastIndexOf(" "));
        } else {
            wordBeingTyped = text;
        }
        return wordBeingTyped.trim();
    }

    private boolean wordTyped(String typedWord) {

        if (typedWord.isEmpty()) {
            return false;
        }
        //System.out.println("Typed word: " + typedWord);

        boolean suggestionAdded = false;

        List<String> completions = autoCompleteDictionaryTrie.predictCompletions(typedWord, 6);
        for (String word : completions) {
            SuggestionLabel suggestionLabel = new SuggestionLabel(word, this);
            addLabelToPanel(suggestionLabel);
            //addWordToSuggestions(word);
            suggestionAdded = true;
        }
        return suggestionAdded;
    }

    private void addLabelToPanel(JLabel label) {
        calculatePopUpWindowSize(label);
        suggestionsPanel.add(label);
    }

    private void calculatePopUpWindowSize(JLabel label) {
        //so we can size the JWindow correctly
        if (tW < label.getPreferredSize().width) {
            tW = label.getPreferredSize().width + 25;
        }
        tH += label.getPreferredSize().height;
    }

    private ArrayList<SuggestionLabel> getAddedSuggestionLabels() {
        ArrayList<SuggestionLabel> sls = new ArrayList<>();
        for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
            if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
                SuggestionLabel sl = (SuggestionLabel) suggestionsPanel.getComponent(i);
                sls.add(sl);
            }
        }
        return sls;
    }
}
