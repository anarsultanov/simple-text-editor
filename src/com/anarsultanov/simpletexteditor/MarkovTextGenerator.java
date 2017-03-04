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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.ScrollPaneConstants;


public class MarkovTextGenerator extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea;
	private JTextField textField;

	// The list of words with their next words
	private List<ListNode> wordList; 
	
	// The starting "word"
	private String starter;
	
	// The random number generator
	private Random rnGenerator;
	/**
	 * Create the frame.
	 * @param source text
	 */
	public MarkovTextGenerator(String source) {
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		wordList = new LinkedList<ListNode>();
		starter = "";
		rnGenerator = new Random();
		retrain(source);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		Box verticalBox = Box.createVerticalBox();
		panel.add(verticalBox);
		
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		
		JLabel lblNumWords = new JLabel("Number of words: ");
		lblNumWords.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNumWords.setHorizontalAlignment(SwingConstants.LEFT);
		horizontalBox.add(lblNumWords);
		
		textField = new JTextField();
		horizontalBox.add(textField);
		textField.setColumns(10);
		
		JButton btnButton = new JButton("Generate");
		btnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox.add(btnButton);
		btnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					handleGenerate();						
			}
		});
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.NORTH);
		
		JLabel lblMarkovTextGenerator = new JLabel("Markov Text Generator");
		lblMarkovTextGenerator.setFont(new Font("Tahoma", Font.BOLD, 20));
		panel_1.add(lblMarkovTextGenerator);
	}
	
	public void train(String sourceText)
	{
		String[] sourceWords = sourceText.split(" +");
		starter = sourceWords[0];
		String prevWord = starter;
		boolean alreadyNode = false;
		for (String w: sourceWords){
			if (w == sourceWords[0]) continue;
			for(ListNode word: wordList){
				if (prevWord.equals(word.getWord())){
					word.addNextWord(w);
					alreadyNode = true;
					break;
				}
			}
			if (!alreadyNode){
				ListNode word = new ListNode(prevWord);
				word.addNextWord(w);
				wordList.add(word);
			}
			prevWord = w;
			alreadyNode = false;
		}
		for(ListNode word: wordList){
			if (sourceWords[sourceWords.length-1].equals(word.getWord())){
				word.addNextWord(starter);
				alreadyNode = true;
				break;
			}
		}
		if (!alreadyNode){
			ListNode word = new ListNode(sourceWords[sourceWords.length-1]);
			word.addNextWord(starter);
			wordList.add(word);
		}
	}
	
	public String generateText(int numWords) {
		String currWord = starter;
		String output = "";
		if (numWords > 0){
			output = output.concat(currWord);
		}
		while (numWords > 1) {
			output = output.concat(" ");
			for(ListNode word: wordList){
				if (currWord.equals(word.getWord())){
					String w = word.getRandomNextWord(rnGenerator);
					output = output.concat(w);
					currWord = w;
					break;
				}
			}			
			numWords--;
		}
		return output;
	}
	
	public void retrain(String sourceText)
	{
		wordList = new LinkedList<ListNode>();
		String[] sourceWords = sourceText.split(" +");
		starter = sourceWords[0];
		String prevWord = starter;
		boolean alreadyNode = false;
		for (String w: sourceWords){
			if (w == sourceWords[0]) continue;
			for(ListNode word: wordList){
				if (prevWord.equals(word.getWord())){
					word.addNextWord(w);
					alreadyNode = true;
					break;
				}
			}
			if (!alreadyNode){
				ListNode word = new ListNode(prevWord);
				word.addNextWord(w);
				wordList.add(word);
			}
			prevWord = w;
			alreadyNode = false;
		}
		for(ListNode word: wordList){
			if (sourceWords[sourceWords.length-1].equals(word.getWord())){
				word.addNextWord(starter);
				alreadyNode = true;
				break;
			}
		}
		if (!alreadyNode){
			ListNode word = new ListNode(sourceWords[sourceWords.length-1]);
			word.addNextWord(starter);
			wordList.add(word);
		}
	}
	
	private void handleGenerate() {
    	if(isInputValid()) {
    		String mText = generateText(Integer.parseInt(textField.getText()));
    		setResult(mText);
    	}
    	else {
    		Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(textArea, "Invalid input.\nMust enter number > 0.");
    	}    
    }
	
	public void setResult(String result) {
		textArea.setText(result);
	}
	
    private boolean isInputValid() {
    	String numString = textField.getText();
    	return !(numString.equals("") || !isInteger(numString)
    			|| (Integer.parseInt(numString) <= 0));
    }
    
    /**
     * Checks if string is integer
     * 
     * @param str
     * @return true if string is able to be parsed as an integer.
     */
    public static boolean isInteger(String str) {  
        try  {  
            Integer.parseInt(str);  
        }  
        catch(NumberFormatException nfe) {  
            return false;  
        } 

        return true;  
    }
}

class ListNode
{
    // The word that is linking to the next words
	private String word;
	
	// The next words that could follow it
	private List<String> nextWords;
	
	ListNode(String word)
	{
		this.word = word;
		nextWords = new LinkedList<String>();
	}
	
	public String getWord()
	{
		return word;
	}

	public void addNextWord(String nextWord)
	{
		nextWords.add(nextWord);
	}
	
	public String getRandomNextWord(Random generator)
	{
		String randomWord = nextWords.get(generator.nextInt(nextWords.size()));
	    return randomWord;
	}

	public String toString()
	{
		String toReturn = word + ": ";
		for (String s : nextWords) {
			toReturn += s + "->";
		}
		toReturn += "\n";
		return toReturn;
	}
	
}

