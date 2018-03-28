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
        System.out.println("start decoding");
        this.cipher = cipher;
        // start with an empty mapping
        translator = new TranslatorImpl();
        // tokenize encoded message into separate words
        tokenizer = new TokenizerImpl(" ,.!");
        tokens = tokenizer.tokenize(cipher);
        System.out.println("print tokens:");
        
        for(String s : tokens)
            System.out.println(s);
        System.out.println("==============");
        
        
        seen = new boolean[tokens.length];
        
        decodeMessage(seen, 0, output);
        
        return (output.size() != 0);
    }  
    
    private int findNumEncrypted(String s){
        String translated = translator.getTranslation(s);
        int num = 0;
        //System.out.println("num encrypted string:" + translated);
        
        for(int i=0; i<translated.length(); i++){
            if(translated.charAt(i) == '?')
                num++;
        }
        return num;
    }
    
    private int findNextWord(boolean[] seen){
        // find word that has not been chosen yet AND has most encrypted letters
        int maxLength = 0;
        int index = -1;
        for(int i=0; i<tokens.length; i++){
            int numEncrypted = findNumEncrypted(tokens[i]);
            //System.out.println(tokens[i] + ":" + numEncrypted);
            if(!seen[i] && numEncrypted > maxLength){
                maxLength = numEncrypted;
                index = i;
            }
        }
        //System.out.println("index of most encrypted:" + index);
        return index;
    }    
        
    private void printSeen(boolean[] seen){
        for(int i=0; i<seen.length; i++){
            if(seen[i])
                System.out.println("seen " + tokens[i]);
            else
                System.out.println("not seen " + tokens[i]);
        }
        System.out.println();
    }
    
    private void decodeMessage(boolean[] seen, int valid, List<String> output){
        int index = findNextWord(seen);
        String cipherWord = tokens[index];
        System.out.println("--------------");
        System.out.println();
        System.out.println("picked next word:" + cipherWord);
        seen[index] = true;
        
        System.out.println("SEEN LIST");
        printSeen(seen);
        
        // translate chosen word using current mapping table
        String partialDecode = translator.getTranslation(cipherWord);
        System.out.println("current translation:" + partialDecode);
        
        // create a list of all valid English words that match
        List<String> matches = new ArrayList<String>(); 
        
        // no matches, then current mapping is wrong
        if(!d.findPotentialCandidates(cipherWord, partialDecode, matches)){
            seen[index] = false;
            translator.popMapping();
            System.out.println("no matches in dictionary");
            return;
        }

        int currValid = valid+1;
        System.out.println("valid words:" + currValid);
        
        for(String candidate : matches){
            System.out.println("candidate:" + candidate);
            List<MappingPair> mappings = new ArrayList<MappingPair>();
            for(int i=0; i<candidate.length(); i++){
                if(Character.isLetter(candidate.charAt(i))){
                    MappingPair mp = new MappingPair();
                    mp.encryptedLetter = cipherWord.charAt(i);
                    mp.decryptedLetter = candidate.charAt(i);
                    mappings.add(mp);
                }
            }
            
            // what if empty mappings?
            if(translator.pushMapping(mappings)){
                // use partial translation table to translate entire message
                String potential = translator.getTranslation(cipher);
                System.out.println("cipher translation:" + potential);
                int numValid = findValidWords(potential);
                System.out.println("num valid words in translation:" + numValid);
                System.out.println("need " + currValid + " valid words");
                // full valid translation found
                System.out.println(tokens.length);
                if(numValid == tokens.length){
                    output.add(potential);
                    System.out.println("********************************************");
                    System.out.println("FOUND VALID TRANSLATION:" + potential);
                    System.out.println("********************************************");
                }
                else if(numValid >= currValid)
                    decodeMessage(seen, numValid, output); 
                else{
                    System.out.println("did not work out");
                }
                translator.popMapping();
            }
            System.out.println("onto next candidate for " + cipherWord);
            System.out.println("~~~~~~~~~~~~~");
        }
       
        System.out.println("````````````````````````");
        System.out.println("finished checking word:" + tokens[index]);
        System.out.println("````````````````````````");
        seen[index] = false;
        return;
    }

    private int findValidWords(String potential){
        String[] sentence = tokenizer.tokenize(potential);
        int validWords = 0;
        
        for(String word : sentence){
            if(!word.matches(".*[?].*")){
                if(!d.isValidWord(word))
                    return -1;
                validWords++;
            }
        }
        return validWords;
    }   
}