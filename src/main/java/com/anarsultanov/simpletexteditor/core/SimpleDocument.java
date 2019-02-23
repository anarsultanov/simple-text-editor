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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleDocument {

    private final String text;

    private int numWords;  // The number of words in the document
    private int numSentences;  // The number of sentences in the document
    private int numSyllables;  // The number of syllables in the document

    /**
     * @param text The text of the document.
     */
    public SimpleDocument(String text) {
        this.text = text;
        processText();
    }

    public List<String> getTokens(String pattern) {
        ArrayList<String> tokens = new ArrayList<>();
        Pattern tokSplitter = Pattern.compile(pattern);
        Matcher m = tokSplitter.matcher(text);

        while (m.find()) {
            tokens.add(m.group());
        }

        return tokens;
    }

    private int countSyllables(String word) {
        int count = 0;
        boolean noOtherSyllables = true;
        Character prev = null;
        Character[] vowels = {'a', 'e', 'i', 'o', 'u', 'y'};
        for (int i = 0; i < word.length(); i++) {
            Character c = Character.toLowerCase(word.charAt(i));
            if (Arrays.asList(vowels).contains(c) && !(Arrays.asList(vowels).contains(prev))) {
                if (!(c.equals('e') && (i + 1 == word.length())) || noOtherSyllables) {
                    noOtherSyllables = false;
                    count++;
                }
            }
            prev = c;
        }
        return count;
    }

    private boolean isWord(String tok) {
        return !(tok.contains("!") || tok.contains(".") || tok.contains("?"));
    }

    private void processText() {
        List<String> tokens = getTokens("[!?.]+|[a-zA-Z]+");
        if (tokens.size() > 0 && isWord(tokens.get(0))) {
            numSentences++;
        }
        for (int i = 0; i < tokens.size(); i++) {
            if (isWord(tokens.get(i))) {
                numWords++;
                numSyllables += countSyllables(tokens.get(i));
            } else {
                if (i + 1 < tokens.size() && isWord(tokens.get(i + 1))) {
                    numSentences++;
                }
            }
        }

    }

    public int getNumSentences() {
        return numSentences;
    }

    public int getNumWords() {
        return numWords;
    }

    public int getNumSyllables() {
        return numSyllables;
    }

    public double getFleschScore() throws ArithmeticException {
        double syllables = getNumSyllables();
        double words = getNumWords();
        double sentences = getNumSentences();
        return 206.835 - 1.015 * (words / sentences) - 84.6 * (syllables / words);
    }
}
