import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.lang.IllegalArgumentException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

/*
You need to build a dictionary class which is used to load up the contents of an English dictionary 
text file (basically a list of valid English words that we’ll provide) and allow the user to 
efficiently search for words in that dictionary (e.g., “Is the word “onomatopoeia” in the 
dictionary”, or “Give me a list of all words from the dictionary that follow the letter 
pattern 122134, like ‘GOOGLE’ or ‘TOOTHY’”). Your DictionaryImpl class MUST use your MyHash 
class to implement its data structures and MUST not use any STL container classes.

The DictionaryImpl class is responsible for loading all of the words from our provided dictionary 
and storing these words in a set of data structures which enable the class to be used to efficiently 
look up words and word patterns:
*/

/*
class DictionaryImpl
{
public:
	DictionaryImpl();
	~DictionaryImpl();
	bool loadDictionary(const std::string &dictFilename);
    bool isValidWord(const std::string& word) const;
	bool findPotentialCandidates(const std::string &cipherWord, 
		const std::string &curTranslation,
		std::vector<std::string> &matches) const; 
private:
// add your private data and methods here
};
*/

class DictionaryImpl {
    private MyHash<String, Integer> dict;
    private MyHash<String, List<String>> patternDict;
    
    DictionaryImpl(){
        dict = new MyHash<String, Integer>();
        patternDict = new MyHash<String, List<String>>();
    }
    
    // If your function is unable to open the filename specified in dictFilename, then it MUST return false. 
    // Otherwise, if it is able to open the dictionary file and load its words, it MUST return true.
    boolean loadDictionary(String dictFilename){  
        try{
            Scanner scanner = new Scanner(new File(dictFilename));

            while(scanner.hasNextLine()){
                String word = scanner.nextLine().toLowerCase();
                dict.associate(word, 0);
                
                String letterPattern = findLetterPattern(word);
                List<String> list = new LinkedList<String>();
                //System.out.println(word + ":" + letterPattern);
                if(patternDict.find(letterPattern) != null)
                    list = patternDict.find(letterPattern);
                
                list.add(word);
                patternDict.associate(letterPattern, list);
            }
            scanner.close();
        } catch(FileNotFoundException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    // MUST return true if the specified word is in the currently-loaded dictionary, or false otherwise. 
    // This function MUST be case insensitive
    boolean isValidWord(String word){
        word = word.toLowerCase();
        return (dict.find(word) != null) ? true : false;
    }
    
    // Find a group of English words in your dictionary that have the same letter pattern as the cipherWord, 
    // and which are consistent with the current English translation, curTranslation
    List<String> findPotentialCandidates(String cipherWord, String curTranslation){
        cipherWord = cipherWord.toLowerCase();
        curTranslation = curTranslation.toLowerCase();
        
        if(cipherWord.matches(".*[^a-z'].*") || curTranslation.matches(".*[^a-z'?].*"))
            throw new IllegalArgumentException("cipherWord or curTranslation is not in the correct format");
        
        String letterPattern = findLetterPattern(cipherWord);
        List<String> candidates = new LinkedList<String>();
        
        // No word with such letter pattern exists in dictionary
        if(patternDict.find(letterPattern) == null) return null;
        
        List<String> list = patternDict.find(letterPattern);
        List<String> result = new LinkedList<String>();
        for(String word : list){
            if(matchesCurTranslation(word, curTranslation))
                result.add(word);
        }
        
        return (result.size() != 0) ? result : null;
    }
    
    
    private boolean matchesCurTranslation(String word, String curTranslation){
        for(int i=0; i<word.length(); i++){
            if(curTranslation.charAt(i) != '?' && word.charAt(i) != curTranslation.charAt(i))
                return false;
        }
        return true;
    }
    
    private String findLetterPattern(String s){
        MyHash<Character, Integer> distinct = new MyHash<>();
        StringBuilder sb = new StringBuilder();
        int num = 1;
        
        for(int i=0; i<s.length(); i++){
            if(distinct.find(s.charAt(i)) != null)
                sb.append(distinct.find(s.charAt(i)));
            else{
                distinct.associate(s.charAt(i), num);
                sb.append(num++);
            }
        }
        return sb.toString();
    }
}