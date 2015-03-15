# WordFinder
WordFinder is a standalone Java GUI application (using Swing) that allows a user to search the data provided by the WordNet project. WordNet is an application that provides dictionary-type data. The data consists of a set of terms, and each term has a part of speech and a definition. WordFinder is available for free (the link is at the bottom of this page), and offers a number of options for searching:

* Wildcard searches (using '*' and '%')
* Soundex (words that sound similar)
* Regular expression
* Contains
* Anagram

![WordFinder](http://argonium.github.io/wf.png)

The first two search modes reuse the code featured elsewhere on this web site. A wildcard search means the '\*' and '%' characters have a special meaning: '\*' means to match any string of consecutive characters (zero or more), and '%' means to match any one character. A Soundex search means to match on words that sound similar. A regular expression search means to allow the use of regular expressions in the search term. If you don't know what a regular expression is, don't use this option.

One useful feature of this application is the ability to search based on not just a term, but also include a word or phrase from the term's definition. This can be helpful if, say, you're solving a crossword puzzle, and want to include a word from the puzzle clue in the definition search.

There is currently no help file, but there is tooltip text for most of the controls, so the interface should be easy to understand. One possible source of confusion may be the two "Go" buttons on the Search page. The first one, under "Find terms by word", will cause the software to search for a match based on just the term entered by the user. This is the most common means of searching. The second "Go" button, under "Find terms by definition", will cause the software to search for a match based on both the term entered in the first text field and the definition string entered in the second text field. Since the source data is organized as a set of terms, with a part of speech and definition associated with each term, this lets the user search for a match on term and definition (as described in the paragraph above). When searching by definition, only the text entered in the first text field can include a regular expression or the use of '\*' and '%'; the text entered in the second text field (definition) cannot use any special characters.

To run the appication, build it via Ant ('ant clean dist'), and then open via 'java -jar wordfinder.jar' (or double-click wordfinder.jar). The data file is embedded in the jar file.

Part of the code is copyright JGoodies Karsten Lentzsch. This is limited to portions of the GUI.

The source code is released under the MIT license (other than the JGoodies code).
