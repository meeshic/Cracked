
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


class DecoderImpl{
    private DictionaryImpl d;
    private TokenizerImpl tokenizer;
    private TranslatorImpl translator;
    private MyHash<Integer, Boolean> seen;
    private MyHash<Integer, Integer> numEncrypted;
    private String cipher;
    
    boolean load(String dictFilename){
        d = new DictionaryImpl();
        return d.load(dictFilename);
    }
    
    boolean crack(String cipher, List<String> output){
        this.cipher = cipher;
        // start with an empty mapping
        translator = new TranslatorImpl();
        // tokenize encoded message into separate words
        tokenizer = new TokenizerImpl("..");
        seen = new MyHash<>();
        numEncrypted = new MyHash<>();
        
        String[] tokens = tokenizer.tokenize(cipher);
        for(int i=0; i<tokens.length; i++){
            numEncrypted.associate(i, tokens[i].length());
        }
                
        decodeMessage(tokens, output);
        
        return (output.size() != 0);
    }
    
    private void decodeMessage(String[] tokens, List<String> output){
        // Base case(s):
        // finish when all words have been seen?
            // if(seen.size() == tokens.length)
        
        int index = findNextWord(tokens);
        findMappings(tokens[index], output);
        seen.associate(index, true);
        decodeMessage(tokens, output);
    }
    
    private int findNextWord(String[] tokens){
        // find word that has not been chosen yet AND has most encrypted letters
        int maxLength = 0;
        int index = -1;
        
        for(int i=0; i<tokens.length; i++){
            if(seen.find(tokens[i]) == null && numEncrypted.find(i) > maxLength){
                maxLength = numEncrypted.find(i);
                index = i;
            }
        }
        return index;
    }
    
    
    private void findMappings(String cipherWord, List<String> output){
        // translate chosen word using current mapping table
        String partialDecode = translator.translate(cipherWord);
        
        // create a list of all valid English words that match
        List<String> matches = new ArrayList<String>(); 
        d.findPotentialCandidates(cipherWord, partialDecode, matches);
        
        // no matches, then current mapping is wrong
        if(matches.size() == 0){
            translator.popMapping();
            return;
        }
        
        for(String candidate : matches){
            List<MappingPair> mappings = new ArrayList<MappingPair>();
            for(int i=0; i<candidate.length(); i++){
                if(candidate.charAt(i) != '?'){
                    MappingPair mp = new MappingPair();
                    mp.encryptedLetter = cipherWord.charAt(i);
                    mp.decryptedLetter = candidate.charAt(i);
                }
                mappings.add(mp);
            }
            translator.pushMapping(mappings);
            // use partial translation table to translate entire message
            String potential = translator.getTranslation(cipherWord);
            if(isValidTranslation(potential))
                output.add(potential);
            else
                translator.popMapping();
        }
        
        // update number of encrypted letters in each word?
            // updateNumEncrypted(...);
        
        return;
    }
    
    private boolean isValidTranslation(String potential){
        String[] sentence = tokenizer.tokenize(potential);
        boolean validWord = false;
        
        for(String word : sentence){
            if(!word.matches(".*[?].*"))
                validWord = true;
            if(!d.isValidWord(word))
                return false;
        }
        return validWord;
    }
    
    
    
    
}