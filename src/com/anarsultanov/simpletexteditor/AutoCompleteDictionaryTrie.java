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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AutoCompleteDictionaryTrie{

    private TrieNode root;
    private int size;
    

    public AutoCompleteDictionaryTrie()
	{
		root = new TrieNode();
	}
	
	
	/**
	 * 
	 * @return true if the word was successfully added or false if it already exists
	 * in the dictionary.
	 */
	public boolean addWord(String word)
	{
		boolean added = false;
	    String newWord = word.toLowerCase();
	    char[] charArray = newWord.toCharArray();
	    TrieNode node = root;
	    for(char c: charArray){
	    	node.insert(c);
	    	node = node.getChild(c);
	    }
	    if (node.endsWord()) {
	    	added = false;
	    } else {
	    	node.setEndsWord(true);
	    	added = true;
	    	size++;
	    }
	    return added;
	}
	
	public int size()
	{
	    return size;
	}

	public boolean isWord(String s) 
	{
		String newWord = s.toLowerCase();
	    char[] charArray = newWord.toCharArray();
	    TrieNode node = root;
	    for(char c: charArray){
	    	if (node.getChild(c) != null) {
		    	node = node.getChild(c);   		
	    	} else {
	    		return false;
	    	}
	    }
		return node.endsWord();
	}

	/** 
     * 
     * @param prefix The text to use at the word stem
     * @param numCompletions The maximum number of predictions desired.
     * @return A list containing the up to numCompletions best predictions
     */
     public List<String> predictCompletions(String prefix, int numCompletions) 
     {
    	String newWord = prefix.toLowerCase();
 		char[] charArray = newWord.toCharArray();
 	    TrieNode node = root;
 	    LinkedList<String>  completions = new LinkedList<String>();
 	    for(char c: charArray){
	    	if (node.getChild(c) != null) {
		    	node = node.getChild(c);   		
	    	} else {
	    		return completions;
	    	}
	    }
 	    LinkedList<TrieNode> queue = new LinkedList<TrieNode>();
 	    queue.addLast(node);
 	    while (!queue.isEmpty() && completions.size() < numCompletions) {
 	    	TrieNode completion = queue.removeFirst();
 	    	if (completion.endsWord()) {
 	    		completions.add(completion.getText());
 	    	}
 	    	TrieNode child = new TrieNode();
	    	for (Character c: completion.getValidNextCharacters()){
	    		child = completion.getChild(c);
	    		queue.addLast(child);
	    	}
 	    }
    	 
         return completions;
     }

 	// For debugging
 	public void printTree()
 	{
 		printNode(root);
 	}
 	
 	public void printNode(TrieNode curr)
 	{
 		if (curr == null) 
 			return;
 		
 		System.out.println(curr.getText());
 		
 		TrieNode next = null;
 		for (Character c : curr.getValidNextCharacters()) {
 			next = curr.getChild(c);
 			printNode(next);
 		}
 	}	
}

class TrieNode {
	private HashMap<Character, TrieNode> children; 
	private String text;  // Maybe omit for space
	private boolean isWord;
	
	/** Create a new TrieNode */
	public TrieNode()
	{
		children = new HashMap<Character, TrieNode>();
		text = "";
		isWord = false;
	}
	
	/** Create a new TrieNode given a text String to store in it */
	public TrieNode(String text)
	{
		this();
		this.text = text;
	}
	
	/**
	 * 
	 * @param c The next character in the key
	 * @return The TrieNode that character links to, or null if that link
	 *   is not in the trie.
	 */
	public TrieNode getChild(Character c)
	{
		return children.get(c);
	}
	
	/** 
	 * 
	 * @param c The character that will link to the new node
	 * @return The newly created TrieNode, or null if the node is already 
	 *     in the trie.
	 */
	public TrieNode insert(Character c)
	{
		if (children.containsKey(c)) {
			return null;
		}
		
		TrieNode next = new TrieNode(text + c.toString());
		children.put(c, next);
		return next;
	}
	
    public String getText()
	{
		return text;
	}

	public void setEndsWord(boolean b)
	{
		isWord = b;
	}

	public boolean endsWord()
	{
		return isWord;
	}
	
	public Set<Character> getValidNextCharacters()
	{
		return children.keySet();
	}

}
