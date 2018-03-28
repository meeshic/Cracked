# Cracked

Note: Project reformatted C++ -> Java

Software program that cracks a given arbitrary encrypted message and returns all possible legitimate English translations, sorted alphabetically. The messages are encoded using the Random Substitution Cipher.

Algorithm used:
1. Tokenize cipher
2. Choose cipher word from tokens that has not been chosen and has the most encoded letters that do not have a translation
3. Find words in dictionary that match the cipher word's letter pattern (e.g. yummy -> 12331)
4. For each matched word, create a mapping (encoded->decoded)
5. Apply mapping on cipher
6a. If all words of cipher were translated and are valid, add to output & go to step 4.
6b. If not completely translated but valid, recurse with current mapping to step 2.
6c. If the translation is not valid, throw away current mapping.


**Class notes**:

MyHash
- Hash table implemented with arrays and non-built in linked lists.
- Chaining is used for collision resolution.

TokenizerImpl
- Tokenize sentence into list of words using given separator list ( ,\";:.!()[]{}&^%$#-)
- tokenize(...): O(N+K), where N=length of string to split, K=regex pattern length

DictionaryImpl
- loadDictionary(dictFilePath): uses Java I/O operations to read contents of dictionary text file
- findPotentialCandidates(...): O(Q), Q=# English words that match letter pattern of inputted cipher word. This is due to using
  letter patterns to index dictionary data.

TranslatorImpl
- A non-built in stack is used to keep track of valid mappings

DecoderImpl
- Cracks encoded message and returns all valid translations



*Observations*:
- The output is of ALL valid English word translations which means the sentence itself could not be grammatically correct, 
  though containing all valid English words. So future work could consist of validating sentences using automata theory.
