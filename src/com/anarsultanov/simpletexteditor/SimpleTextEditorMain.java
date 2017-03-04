/*
Copyright (c) 2017 Anar Sultanov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.anarsultanov.simpletexteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

public class SimpleTextEditorMain {

	private static SimpleTextEditorMain window;
	private JFrame frame;
	
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem miOpen;
	private JMenuItem miSave;
	private JMenuItem miSaveAs;
	private JMenuItem miExit;	
	private JMenu mnEdit;
	private JMenuItem miUndo;
	private JMenuItem miRedo;
	private JMenuItem miCut;
	private JMenuItem miCopy;
	private JMenuItem miPaste;
	private JMenuItem miDelete;
	private JMenuItem miSelect;
	private JMenu mnOptions;	
	private JMenuItem miDist;
	private JMenuItem miGen;
	private JCheckBoxMenuItem chkbxSpell;
	private JCheckBoxMenuItem chkbxSugg;
	private JMenu mnHelp;
	private JMenuItem miAbout;

	private JPopupMenu rightCLickMenu;
	private JMenuItem rcmCut;
	private JMenuItem rcmCopy;
	private JMenuItem rcmPaste;
	private JMenuItem rcmDelete;
	private JMenuItem rcmSelect;
	
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JPanel panel;
	private JLabel lblFlesch;
	private JTextField textField;
	private JButton btnFlesch;
	
	UndoManager manager;
	private JFileChooser dialog;
	private MarkovTextGenerator texGenFrame;
	private static EditDistance editDistanceFrame;
	private static About about;
	
	private JPanel suggestionsPanel;
    private JWindow autoSuggestionPopUpWindow;
    private JWindow onclickSuggestionPopUpWindow;
    private String typedWord;
        
    private int tW, tH;
    private final float opacity = 0.75f;
    
    private final Color popUpBackground = Color.WHITE.brighter();
    private final Color suggestionsTextColor = Color.BLUE;
    private final Color suggestionFocusedColor = Color.GRAY;
    private final Color onclickSuggestionsTextColor = Color.RED.darker();
	private DefaultHighlighter highlighter;
	private DefaultHighlightPainter hPainter;

	private boolean suggestions = false;
	private boolean spellcheck = false;
	
	private static AutoCompleteDictionaryTrie ac;
	private NearbyWords nw;
	private static String dictFile = "/data/dict.txt";
	
	private String currentFile = "Untitled";
	private boolean changed = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new SimpleTextEditorMain();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SimpleTextEditorMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setDictionary(dictFile);
		frame = new JFrame(currentFile);
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		dialog = new JFileChooser(System.getProperty("user.dir"));
		
		autoSuggestionPopUpWindow = new JWindow(frame);        
		autoSuggestionPopUpWindow.setOpacity(opacity);
		onclickSuggestionPopUpWindow = new JWindow(frame);        
		onclickSuggestionPopUpWindow.setOpacity(opacity);

        suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new GridLayout(0, 1));
        suggestionsPanel.setBackground(popUpBackground);
        
        manager = new UndoManager();
        MenuListener menuListener = new MenuListener();
    	/**
    	 * Menu Bar
    	 */ 
		menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		miOpen = new JMenuItem("Open");
		miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnFile.add(miOpen);
		miOpen.addActionListener(menuListener);
		
		miSave = new JMenuItem("Save");
		miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(miSave);
		miSave.addActionListener(menuListener);
		
		miSaveAs = new JMenuItem("Save As");
		miSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		mnFile.add(miSaveAs);
		miSaveAs.addActionListener(menuListener);
		
		mnFile.addSeparator();
		
		miExit = new JMenuItem("Exit");
		miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		mnFile.add(miExit);
		miExit.addActionListener(menuListener);
		
		mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		miUndo = new JMenuItem("Undo");
		mnEdit.add(miUndo);
		miUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		miUndo.addActionListener(menuListener);
		miUndo.setEnabled(false);
		
		miRedo = new JMenuItem("Redo");
		mnEdit.add(miRedo);
		miRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
		miRedo.addActionListener(menuListener);
		miRedo.setEnabled(false);
		
		mnEdit.addSeparator();
		
		miCut = new JMenuItem(new DefaultEditorKit.CutAction());
		miCut.setText("Cut");
		miCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		mnEdit.add(miCut);    
		
	    miCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
	    miCopy.setText("Copy");
	    miCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
	    mnEdit.add(miCopy);
	    
	    miPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
	    miPaste.setText("Paste");
	    miPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
	    mnEdit.add(miPaste);
	    
	    miDelete = new JMenuItem("Delete");
	    mnEdit.add(miDelete);
	    miDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
	    miDelete.addActionListener(menuListener);
	    
	    mnEdit.addSeparator();
	    
	    miSelect = new JMenuItem("Select all");
	    mnEdit.add(miSelect);
	    miSelect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
	    miSelect.addActionListener(menuListener);
		
		mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		miDist = new JMenuItem("Edit Distance");
		miDist.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnOptions.add(miDist);
		miDist.addActionListener(menuListener);	
		
		miGen = new JMenuItem("Generate Text");
		miGen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnOptions.add(miGen);
		miGen.addActionListener(menuListener);	
		
		mnOptions.addSeparator();
		
		chkbxSpell = new JCheckBoxMenuItem("SpellChecking");
		mnOptions.add(chkbxSpell);
		chkbxSpell.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		chkbxSpell.addActionListener(menuListener);		
		
		chkbxSugg = new JCheckBoxMenuItem("AutoSuggestions");
		mnOptions.add(chkbxSugg);
		chkbxSugg.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		chkbxSugg.addActionListener(menuListener);
		
		
		mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		miAbout = new JMenuItem("About");
		miAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mnHelp.add(miAbout);
		miAbout.addActionListener(menuListener);
			    
		/**
		 * Right Click Menu
		 */   
	    rightCLickMenu = new JPopupMenu();
	    
	    rcmCut = new JMenuItem(new DefaultEditorKit.CutAction());
	    rcmCut.setText("Cut");
	    rightCLickMenu.add(rcmCut);    
	    rcmCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
	    rcmCopy.setText("Copy");
	    rightCLickMenu.add(rcmCopy);
	    rcmPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
	    rcmPaste.setText("Paste");
	    rightCLickMenu.add(rcmPaste);
	    rcmDelete = new JMenuItem("Delete");
	    rcmDelete.addActionListener(menuListener);
	    rightCLickMenu.add(rcmDelete);
	    rightCLickMenu.addSeparator();
	    rcmSelect = new JMenuItem("Select all");
	    rcmSelect.addActionListener(menuListener);
	    rightCLickMenu.add(rcmSelect);
		/**
		 * Main Text Area
		 */	    
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		highlighter = (DefaultHighlighter) textArea.getHighlighter();
		hPainter = new DefaultHighlightPainter(new Color(0xFF6464));   

        addKeyBindingToRequestFocusInPopUpWindow();
	    setDocumentListener();
		
	    //Listeners for pop up windows
		textArea.addMouseListener(new MouseAdapter()
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
	        	Highlight[] hls = highlighter.getHighlights();
        		Point pt = new Point(e.getX(), e.getY());
        		int pos = textArea.viewToModel(pt);
        		String clickedWord = getClickedWord(textArea.getText(), pos);
        		System.out.println(clickedWord);
        		for (Highlight hl: hls){
        			int start = hl.getStartOffset();
        			int end = hl.getEndOffset();
        			if (pos >= start && pos < end){
        				showPossibleCorrections(clickedWord, pos);
        				clickOnHighlightedWord = true;
            	        break;
        			}
        		clickOnHighlightedWord = false;
        		}
	        }
	        
	        private void doPop(MouseEvent e){
	            rightCLickMenu.show(e.getComponent(), e.getX(), e.getY());
	        }
	        
            public void mouseClicked(MouseEvent me)
            {
            	autoSuggestionPopUpWindow.setVisible(false);
            	if (me.getButton() == MouseEvent.BUTTON1){
                	onclickSuggestionPopUpWindow.setVisible(false);
            	}
            }
        });
		textArea.addKeyListener(new KeyListener(){
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
		/**
		 * Flesch Index Panel
		 */     	       
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		lblFlesch = new JLabel("Flesch Index");
		lblFlesch.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(lblFlesch);
		
		textField = new JTextField();
		textField.setEditable(false);
		panel.add(textField);
		textField.setColumns(10);
		
		btnFlesch = new JButton("Count");
		panel.add(btnFlesch);
		btnFlesch.addActionListener(new ActionListener()
		{
			  public void actionPerformed(ActionEvent e)
			  {
				  handleFleschIndex();
			  }
			});
	}
	
	/**
	 * Menu items listener
	 */
	class MenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem choice = (JMenuItem) e.getSource();
			
			if (choice == miOpen) {
				saveOld();
	        	if(dialog.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					readInFile(dialog.getSelectedFile().getAbsolutePath());
					setDocumentListener();
					autoSuggestionPopUpWindow.setVisible(false);
				}
			} else if (choice == miSave) {
				if(!currentFile.equals("Untitled")){
    				saveFile(currentFile);
            	} else {
    				saveFileAs();
            	}
			} else if (choice == miSaveAs) {
				saveFileAs();
			} else if (choice == miExit) {
				saveOld();
    			System.exit(0);
			} else if (choice == miUndo) {
				manager.undo();
			    if(manager.canUndo()){
			    	miUndo.setEnabled(true);
			    } else {
			    	miUndo.setEnabled(false);
			    }
			} else if (choice == miRedo) {
				manager.redo();
			    if(manager.canRedo()){
			    	miRedo.setEnabled(true);
			    } else {
			    	miRedo.setEnabled(false);
			    }
			} else if (choice == miDelete || choice == rcmDelete ) {
		    	textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
			} else if (choice == miSelect || choice == rcmSelect ) {
		    	textArea.selectAll();
			} else if (choice == miDist) {
				editDistanceFrame = new EditDistance();
				editDistanceFrame.setTitle("Edit Distance");
				editDistanceFrame.setVisible(true);
			} else if (choice == miGen){
				if (textArea.getText().length() >= 500){
					texGenFrame = new MarkovTextGenerator(textArea.getText());
			    	texGenFrame.setTitle("Markov Text Generator");
			        texGenFrame.setVisible(true);
				} else {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(textArea, "Not enough words for the generator.\nInsert text with at least 500 characters.");
				}	    	
		    } else if (choice == chkbxSpell) {
				if (chkbxSpell.isSelected()){
					spellcheck = true;
					highlightIncorrectWords();
				} else {
					spellcheck = false;
					highlighter.removeAllHighlights();
				}	
			} else if (choice == chkbxSugg) {
				if (chkbxSugg.isSelected()){
					suggestions = true;
				} else {
					suggestions = false;
					autoSuggestionPopUpWindow.setVisible(false);
				}		
			} else if (choice == miAbout) {
				about = new About();
				about.setTitle("About");
				about.setVisible(true);
			}
		}
	};
	
	/**
	 * Helper methods
	 * TODO: try to move most to other classes
	 */
	private void showPossibleCorrections(String clickedWord, int pos) {
		suggestionsPanel.removeAll();//remove previos words/jlabels that were added
        //used to calcualte size of JWindow as new Jlabels are added
        tW = 0;
        tH = 0;
        boolean suggestionAdded = false;
        int number = 6;
        List<String> suggestions = nw.suggestions(clickedWord, number);
        for (String sug : suggestions) {
        	OnclickSuggestionLabel onClickLabel = new OnclickSuggestionLabel(sug, pos, onclickSuggestionsTextColor, window);
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
	
	private void readInFile(String fileName) {
		try {
			FileReader r = new FileReader(fileName);
			textArea.read(r,null);
			r.close();
			currentFile = fileName;
			frame.setTitle(currentFile);
			changed = false;
		}
		catch(IOException e) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(textArea, "Editor can't find the file called "+fileName);
		}
	}
	
	private void saveFile(String fileName) {
		try {
			FileWriter w = new FileWriter(fileName);
			textArea.write(w);
			w.close();
			currentFile = fileName;
			frame.setTitle(currentFile);
			changed = false;
		}
		catch(IOException e) {
		}
	}
	
	private void saveFileAs() {
		if(dialog.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
			saveFile(dialog.getSelectedFile().getAbsolutePath());
	}
	
	private void saveOld() {
		if(changed) {
			if(JOptionPane.showConfirmDialog(textArea, "Would you like to save "+ currentFile +" ?","Save",JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION)
				saveFile(currentFile);
		}
	}
	
	private void setDocumentListener() {
		Document doc = textArea.getDocument();
		doc.addUndoableEditListener(manager);
		doc.addDocumentListener(new DocumentListener() {
		    @Override
		    public void removeUpdate(DocumentEvent e) {
		    	if (e.getLength() == 1){
		    		handleSpellChanges();
		    	} else {
		    		autoSuggestionPopUpWindow.setVisible(false);
		    	}
		    	handleUndoRedoChanges();
		    }
	
		    @Override
		    public void insertUpdate(DocumentEvent e) {
		    	if (e.getLength() == 1){
		    		handleSpellChanges();
		    	} else {
		    		autoSuggestionPopUpWindow.setVisible(false);
		    	}
		    	handleUndoRedoChanges();
		    }
	
		    @Override
		    public void changedUpdate(DocumentEvent arg0) {
		    	if (arg0.getLength() == 1){
		    		handleSpellChanges();
		    	} else {
		    		autoSuggestionPopUpWindow.setVisible(false);
		    	}
		    	handleUndoRedoChanges();
		    }
		    
		    private void handleSpellChanges(){
		    	if (spellcheck){
		    		highlightIncorrectWords();
		    	}
		    	if (suggestions){
		    		checkForAndShowSuggestions();
		    	} 
		    }
		    private void handleUndoRedoChanges(){
		    	changed = true;	
		    	if (manager.canUndo()) {
		    		miUndo.setEnabled(true);
		    	}
		    	if (manager.canRedo()) {
		    		miRedo.setEnabled(true);
		    	}
		    }
		});
	}
	
	private void setDictionary(String dictionary) {
		InputStream in = this.getClass().getResourceAsStream(dictionary);
		ac = new AutoCompleteDictionaryTrie();
        BufferedReader reader = null;
        try {
            String nextWord;
            reader = new BufferedReader(new InputStreamReader(in));
            while ((nextWord = reader.readLine()) != null) {
            	ac.addWord(nextWord);
            }
        } catch (IOException e) {
            System.err.println("Problem loading dictionary file: " + dictionary);
            e.printStackTrace();
        }
        nw = new NearbyWords(ac);
        //System.out.println(dict.size());
    }
	
	private void highlightIncorrectWords(){
		highlighter.removeAllHighlights();
        String contText  = textArea.getText();

        SimpleDocument document = new SimpleDocument(contText);       
        Pattern pattern;
        Matcher matcher;
        for (String tok: document.getTokens("[a-zA-Z]+")){
        	if (!ac.isWord(tok)){
        		
        	    try
        	        {
        	        pattern = Pattern.compile("\\b"+tok+"\\b");
        	        }
        	    catch(Exception e)
        	        {
        	        e.printStackTrace();
        	        return;
        	        }
        	    matcher = pattern.matcher(contText);
        	    while( matcher.find() )
                	{
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
                            if (i < max) {
                                sl.setFocused(true);
                                autoSuggestionPopUpWindow.toFront();
                                autoSuggestionPopUpWindow.requestFocusInWindow();
                                suggestionsPanel.requestFocusInWindow();
                                suggestionsPanel.getComponent(i).requestFocusInWindow();
                                lastFocusableIndex = i;
                                break;
                            }
                        }
                    }
                } else {//only a single suggestion was given
                    autoSuggestionPopUpWindow.setVisible(false);
                    setFocusToTextField();
                    checkForAndShowSuggestions();//fire method as if document listener change occured and fired it
                }
            }
        });
    }
    
    private void setFocusToTextField() {
        frame.toFront();
        frame.requestFocusInWindow();
        textArea.requestFocusInWindow();
    }
    
    public ArrayList<SuggestionLabel> getAddedSuggestionLabels() {
        ArrayList<SuggestionLabel> sls = new ArrayList<>();
        for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
            if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
            	SuggestionLabel sl = (SuggestionLabel) suggestionsPanel.getComponent(i);
                sls.add(sl);
            }
        }
        return sls;
    }

    private void checkForAndShowSuggestions() {
        typedWord = getCurrentlyTypedWord();
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
            showPopUpWindow();
            setFocusToTextField();
        }
    }
    
    private void addLabelToPanel(JLabel label) {
        calculatePopUpWindowSize(label);
        suggestionsPanel.add(label);
    }
    
    public String getCurrentlyTypedWord() {//get newest word after last white spaceif any or the first word if no white spaces
        String text = textArea.getText();
        int pos = textArea.getCaretPosition();
        if (pos < text.length()) {
        	text = text.substring(0, pos+1);
        }
        
        String wordBeingTyped = "";
        text = text.replaceAll("(\\r|\\n)", " ");
        if (text.contains(" ")) {
             wordBeingTyped = text.substring(text.lastIndexOf(" "));
        } else {
            wordBeingTyped = text;
        }      
        return wordBeingTyped.trim();
    }

    private void calculatePopUpWindowSize(JLabel label) {
        //so we can size the JWindow correctly
        if (tW < label.getPreferredSize().width) {
            tW = label.getPreferredSize().width + 25;
        }
        tH += label.getPreferredSize().height;
    }
    private void showOnclickPopUpWindow(int clickPosition) {
    	onclickSuggestionPopUpWindow.getContentPane().add(suggestionsPanel);
    	onclickSuggestionPopUpWindow.setMinimumSize(new Dimension(20, 30));
    	onclickSuggestionPopUpWindow.setSize(tW, tH);
    	onclickSuggestionPopUpWindow.setVisible(true);
    	int pos = clickPosition;
        int windowX = 0;
        int windowY = 0;

        Rectangle rect = null;
        try {
            rect = textArea.modelToView(pos);
        } catch (BadLocationException ex) {
				ex.printStackTrace();
        }

        windowX = (int) (frame.getX() + rect.getX() + textArea.getX() + 15);
        windowY = (int) (frame.getY() + 20 + rect.getY() + textArea.getY() + (rect.getHeight() * 3));

        //show the pop up
        onclickSuggestionPopUpWindow.setLocation(windowX, windowY);
        onclickSuggestionPopUpWindow.setMinimumSize(new Dimension(20 , 30));
        onclickSuggestionPopUpWindow.revalidate();
        onclickSuggestionPopUpWindow.repaint();
    }
    
    private void showPopUpWindow() {
        autoSuggestionPopUpWindow.getContentPane().add(suggestionsPanel);
        autoSuggestionPopUpWindow.setMinimumSize(new Dimension(20, 30));
        autoSuggestionPopUpWindow.setSize(tW, tH);
        autoSuggestionPopUpWindow.setVisible(true);

        int windowX = 0;
        int windowY = 0;

        Rectangle rect = null;
        int dotPosition = textArea.getCaretPosition();
        try {
            rect = textArea.modelToView(dotPosition);
        } catch (BadLocationException ex) {
        	try { // when removeUpdate catch an exception and change the terms
				rect = textArea.modelToView(dotPosition-1);
			} catch (BadLocationException e) {
				rect = new Rectangle(0,0);
			}
        }

        windowX = (int) (frame.getX() + rect.getX() + textArea.getX() + 15);
        windowY = (int) (frame.getY() + 20 + rect.getY() + textArea.getY() + (rect.getHeight() * 3));

        //show the pop up
        autoSuggestionPopUpWindow.setLocation(windowX, windowY);
        autoSuggestionPopUpWindow.setMinimumSize(new Dimension(20 , 30));
        autoSuggestionPopUpWindow.revalidate();
        autoSuggestionPopUpWindow.repaint();

    }

    boolean wordTyped(String typedWord) {

        if (typedWord.isEmpty()) {
            return false;
        }
        //System.out.println("Typed word: " + typedWord);

        boolean suggestionAdded = false;

        List<String> completions = ac.predictCompletions(typedWord, 6);
        for (String word : completions) {
        	SuggestionLabel suggestionLabel = new SuggestionLabel(word, suggestionFocusedColor, suggestionsTextColor, this);
        	addLabelToPanel(suggestionLabel);
        	//addWordToSuggestions(word);
            suggestionAdded = true;
        }
        return suggestionAdded;
    }
    
	private void handleFleschIndex() {
		String text = textArea.getText();
		double fIndex = 0;
		
		// check if text input
		if(!text.equals("")) {
			
			// create Document representation of  current text
			SimpleDocument doc = new SimpleDocument (text);
			
			fIndex = doc.getFleschScore();
			
			//get string with two decimal places for index to
			String fString = String.format("%.2f", fIndex);
			
			// display string in text field
			textField.setText(fString);
			
		}
		else {
			// reset text field
			textField.setText("");
			//mainApp.showInputErrorDialog("No text entered.");
		}	
	}
	
	public String getClickedWord(String content, int caretPosition) {
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
	
	public static JFrame getEditDistanceFrame(){
        return editDistanceFrame;
    }
	
	public static JFrame getAboutFrame(){
        return about;
    }
	
    public JWindow getAutoSuggestionPopUpWindow() {
        return autoSuggestionPopUpWindow;
    }
    
    public JWindow getOnclickSuggestionPopUpWindow() {
        return onclickSuggestionPopUpWindow;
    }

    public Window getContainer() {
        return frame;
    }

    public JTextComponent getTextField() {
        return textArea;
    }
    
    public static AutoCompleteDictionaryTrie getDictionary() {
        return ac;
        //TODO: add label to suggestion panel
    }

    public void addToDictionary(String word) {
        ac.addWord(word);
        //TODO: add label to suggestion panel
    }
}

class SuggestionLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	private boolean focused = false;
    private final JWindow autoSuggestionsPopUpWindow;
    private final JTextComponent textComponent;
    private Color suggestionsTextColor, suggestionBorderColor;

    public SuggestionLabel(String string, final Color borderColor, Color suggestionsTextColor, SimpleTextEditorMain textEditor) {
        super(string);

        this.suggestionsTextColor = suggestionsTextColor;
        this.textComponent = textEditor.getTextField();
        this.suggestionBorderColor = borderColor;
        this.autoSuggestionsPopUpWindow = textEditor.getAutoSuggestionPopUpWindow();

        initComponent();
    }

    private void initComponent() {
        setFocusable(true);
        setForeground(suggestionsTextColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                replaceWithSuggestedText();

                autoSuggestionsPopUpWindow.setVisible(false);
            }
        });

        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "Enter released");
        getActionMap().put("Enter released", new AbstractAction() {
          

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent ae) {
                replaceWithSuggestedText();
                autoSuggestionsPopUpWindow.setVisible(false);
            }
        });
    }

    public void setFocused(boolean focused) {
        if (focused) {
            setBorder(new LineBorder(suggestionBorderColor));
        } else {
            setBorder(null);
        }
        repaint();
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }
    
    private void replaceWithSuggestedText() {
        String suggestedWord = getText();
        String text = textComponent.getText();
        int pos = textComponent.getCaretPosition();
        String textBeforePos= text.substring(0,pos); 
        String textAfterPos= text.substring(pos); 
        String temp;
        int wordsEnd;
        int wordsStart;
        temp = textAfterPos.replaceAll("[^a-zA-Z]", " ");
        if (temp.contains(" ")) {
        	wordsEnd = pos + temp.indexOf(" ");
        } else {
        	wordsEnd = pos + temp.length();
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

class OnclickSuggestionLabel extends JLabel {
	private static final long serialVersionUID = 1L;
    private final JWindow onclickSuggestionsPopUpWindow;
    private final JWindow autoSuggestionsPopUpWindow;
    private final JTextComponent textComponent;
    private Color suggestionsTextColor;
    private int wordPosition;
    
	public OnclickSuggestionLabel(String string, int pos, Color suggestionsTextColor, SimpleTextEditorMain textEditor) {
		super(string);
		this.wordPosition = pos;
	    this.suggestionsTextColor = suggestionsTextColor;
	    this.textComponent = textEditor.getTextField();
	    this.onclickSuggestionsPopUpWindow = textEditor.getOnclickSuggestionPopUpWindow();
	    this.autoSuggestionsPopUpWindow = textEditor.getAutoSuggestionPopUpWindow();
	    initComponent();
	}
	
	private void initComponent() {
        setForeground(suggestionsTextColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                replaceWithSuggestedText();

                onclickSuggestionsPopUpWindow.setVisible(false);
                autoSuggestionsPopUpWindow.setVisible(false);
            }
        });
	}

    private void replaceWithSuggestedText() {
        String suggestedWord = getText();
        String text = textComponent.getText();
        int pos = wordPosition;
        String textBeforePos= text.substring(0,pos); 
        String textAfterPos= text.substring(pos); 
        String temp;
        int wordsEnd;
        int wordsStart;
        temp = textAfterPos.replaceAll("[^a-zA-Z]", " ");
        if (temp.contains(" ")) {
        	wordsEnd = pos + temp.indexOf(" ");
        } else {
        	wordsEnd = pos + temp.length();
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

