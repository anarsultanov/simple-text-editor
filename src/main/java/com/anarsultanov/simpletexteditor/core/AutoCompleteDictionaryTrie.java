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

package com.anarsultanov.simpletexteditor.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class AutoCompleteDictionaryTrie {

    private TrieNode root;
    private int size;

    public AutoCompleteDictionaryTrie() {
        root = new TrieNode();
    }

    public static AutoCompleteDictionaryTrie fromPath(Path path) {
        try (Stream<String> lines = Files.lines(path)) {
            AutoCompleteDictionaryTrie trie = new AutoCompleteDictionaryTrie();
            lines.forEach(trie::addWord);
            return trie;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @return true if the word was successfully added or false if it already exists
     * in the dictionary.
     */
    public boolean addWord(String word) {
        boolean added;
        String newWord = word.toLowerCase();
        char[] charArray = newWord.toCharArray();
        TrieNode node = root;
        for (char c : charArray) {
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

    public int size() {
        return size;
    }

    public boolean isWord(String s) {
        String newWord = s.toLowerCase();
        char[] charArray = newWord.toCharArray();
        TrieNode node = root;
        for (char c : charArray) {
            if (node.getChild(c) != null) {
                node = node.getChild(c);
            } else {
                return false;
            }
        }
        return node.endsWord();
    }

    /**
     * @param prefix         The text to use at the word stem
     * @param numCompletions The maximum number of predictions desired.
     * @return A list containing the up to numCompletions best predictions
     */
    public List<String> predictCompletions(String prefix, int numCompletions) {
        String newWord = prefix.toLowerCase();
        char[] charArray = newWord.toCharArray();
        TrieNode node = root;
        LinkedList<String> completions = new LinkedList<>();
        for (char c : charArray) {
            if (node.getChild(c) != null) {
                node = node.getChild(c);
            } else {
                return completions;
            }
        }
        LinkedList<TrieNode> queue = new LinkedList<>();
        queue.addLast(node);
        while (!queue.isEmpty() && completions.size() < numCompletions) {
            TrieNode completion = queue.removeFirst();
            if (completion.endsWord()) {
                completions.add(completion.getText());
            }
            TrieNode child;
            for (Character c : completion.getValidNextCharacters()) {
                child = completion.getChild(c);
                queue.addLast(child);
            }
        }

        return completions;
    }

    private class TrieNode {
        private HashMap<Character, TrieNode> children;
        private String text;  // Maybe omit for space
        private boolean isWord;

        /**
         * Create a new TrieNode
         */
        private TrieNode() {
            children = new HashMap<>();
            text = "";
            isWord = false;
        }

        /**
         * Create a new TrieNode given a text String to store in it
         */
        private TrieNode(String text) {
            this();
            this.text = text;
        }

        /**
         * @param c The next character in the key
         * @return The TrieNode that character links to, or null if that link
         * is not in the trie.
         */
        private TrieNode getChild(Character c) {
            return children.get(c);
        }

        /**
         * @param c The character that will link to the new node
         * @return The newly created TrieNode, or null if the node is already
         * in the trie.
         */
        private TrieNode insert(Character c) {
            if (children.containsKey(c)) {
                return null;
            }

            TrieNode next = new TrieNode(text + c.toString());
            children.put(c, next);
            return next;
        }

        private String getText() {
            return text;
        }

        private void setEndsWord(boolean b) {
            isWord = b;
        }

        private boolean endsWord() {
            return isWord;
        }

        private Set<Character> getValidNextCharacters() {
            return children.keySet();
        }
    }
}


