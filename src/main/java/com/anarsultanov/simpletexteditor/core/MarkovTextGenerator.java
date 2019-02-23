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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MarkovTextGenerator {

    private Random rnGenerator;

    // The starting word
    private String starter;

    // The list of words with their next words
    private Map<String, List<String>> wordList;

    public MarkovTextGenerator(String source) {
        wordList = new HashMap<>();
        starter = "";
        rnGenerator = new Random();
        train(source);
    }

    public void train(String sourceText) {
        doTrain(sourceText);
    }


    public void retrain(String sourceText) {
        wordList = new HashMap<>();
        doTrain(sourceText);
    }

    private void doTrain(String sourceText) {
        String[] sourceWords = sourceText.split(" +");
        starter = sourceWords[0];
        String prevWord = starter;
        for (int i = 1; i < sourceWords.length; i++) {
            String word = sourceWords[i];
            wordList.computeIfAbsent(prevWord, v -> new ArrayList<>()).add(word);
            prevWord = word;
        }
        wordList.computeIfAbsent(sourceWords[sourceWords.length - 1], v -> new ArrayList<>()).add(starter);
    }

    public String generateText(int numWords) {
        String currWord = starter;
        StringBuilder output = new StringBuilder();
        if (numWords > 0) {
            output.append(currWord);
        }
        while (numWords > 1) {
            output.append(" ");
            List<String> nextWords = wordList.get(currWord);
            String w = nextWords.get(rnGenerator.nextInt(nextWords.size()));
            output.append(w);
            currWord = w;
            numWords--;
        }
        return output.toString();
    }
}