import java.util.List;
import java.util.ArrayList;

/*
class DecoderImpl
{
public:
	DecoderImpl();
	bool load(const std::string& dictFilename);
	bool crack(const std::string &cipher, std::vector<std::string>& output);
private:
	// Add your own private member functions and data here.
};
*/

// Note: You may assume that encrypted messages will never have any other characters, such as ? signs, @s, tabs, etc


class DecoderImpl{
    private DictionaryImpl d;
    private TokenizerImpl tokenizer;
    private TranslatorImpl translator;
    private boolean[] seen;
    private String cipher;
    private String[] tokens;
    
    boolean load(String dictFilename){
        d = new DictionaryImpl();
        return d.loadDictionary(dictFilename);
    }
    
    boolean crack(String cipher, List<String> output){
        this.cipher = cipher;
        // start with an empty mapping
        translator = new TranslatorImpl();
        // tokenize encoded message into separate words
        tokenizer = new TokenizerImpl(" ,\";:.!()[]{}&^%$#-");
        tokens = tokenizer.tokenize(cipher);
        seen = new boolean[tokens.length];
        
        decodeMessage(0, output);
        
        return (output.size() != 0);
    }  
    
    // Find index of word that has not been chosen yet AND has most encrypted letters
    private int findNextWord(){
        int maxLength = 0;
        int index = -1;
        for(int i=0; i<tokens.length; i++){
            int numEncrypted = findNumEncrypted(tokens[i]);
            if(!seen[i] && numEncrypted > maxLength){
                maxLength = numEncrypted;
                index = i;
            }
        }
        return index;
    }    
    
    private void decodeMessage(int valid, List<String> output){
        int index = findNextWord();
        String cipherWord = tokens[index];
        seen[index] = true;
        
        // translate chosen word using current mapping table
        String partialDecode = translator.getTranslation(cipherWord);
        
        // create a list of all valid English words that match
        List<String> matches = new ArrayList<String>(); 
        
        // no matches, then current mapping is wrong
        if(!d.findPotentialCandidates(cipherWord, partialDecode, matches)){
            seen[index] = false;
            translator.popMapping();
            return;
        }
        
        // number of valid words needed in sentence to be a valid translation
        int currValid = valid+1;
        for(String candidate : matches){
            List<MappingPair> mappings = new ArrayList<MappingPair>();
            for(int i=0; i<candidate.length(); i++){
                if(Character.isLetter(candidate.charAt(i))){
                    MappingPair mp = new MappingPair();
                    mp.encryptedLetter = cipherWord.charAt(i);
                    mp.decryptedLetter = candidate.charAt(i);
                    mappings.add(mp);
                }
            }

            if(translator.pushMapping(mappings)){
                // use partial translation table to translate entire message
                String potential = translator.getTranslation(cipher);
                int numValid = findValidWords(potential);
                // full valid translation found
                if(numValid == tokens.length)
                    output.add(potential);
                // potential valid translation found
                else if(numValid >= currValid)
                    decodeMessage(numValid, output); 
                
                translator.popMapping();
            }
        }
        seen[index] = false;
        return;
    }
    
    // Find number of remaining encrypted letters in word
    private int findNumEncrypted(String word){
        String translated = translator.getTranslation(word);
        int num = 0;
        
        for(int i=0; i<translated.length(); i++){
            if(translated.charAt(i) == '?')
                num++;
        }
        return num;
    }
    
    // Find number of valid words in cipher translation
    private int findValidWords(String potential){
        String[] sentence = tokenizer.tokenize(potential);
        int validWords = 0;
        
        for(String word : sentence){
            // Check if word is fully translated
            if(!word.matches(".*[?].*")){
                // Check if word is in dictionary
                if(!d.isValidWord(word))
                    return -1;
                validWords++;
            }
        }
        return validWords;
    }   
}