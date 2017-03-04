#QOGAL
##Simple Text Editor

This project demonstrates different data structures and performance at work with them.

![Скриншот](https://github.com/AnarSultanov/SimpleTextEditor/raw/master/screenshot.png)

For text processing in project implemented class SimpleDocument, that gives the opportunity to count the number of syllables, words and sentences in a single pass through the text and used to calculate Flesch score (readability index) of the text. Also in this class implemented methods to separate text by tokens and check if a string is a word.

To store and handle data in the project implemented several classes.
- AutoCompleteDictionaryTrie class is trie implementation, to store autocomplete dictionary. It is used for prediction of words by prefix and display suggestions. 
- NearbyWords class is used for displaying possible correction options of misspeled words. It return the list of strings that are one modification away from the input string. 
- Class WPTree implemented for storage nearby words and build chain from them for the Edit Distance function. This is a standard tree with each node having any number of possible children. Relationship between nodes is that a child is one character mutation (deletion, insertion, or substitution) away from its parent.
- Another class for data storage and processing is implemented in the text generator. ListNode class used to store the word that is linking to the next words and the next words that could follow it and generate text from Markof's chains.

GUI created using the Swing package.
Any third-party libraries are not used in backend of the project. All functions work correct with a minimum number of bugs.