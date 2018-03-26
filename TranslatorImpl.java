import java.util.List;
import java.util.ArrayList;

//The TranslatorImpl class is responsible for translating an encoded message into 
// either a partially- or fully-decoded English version. 

/*
struct MappingPair
{
	char encryptedLetter;
	char decryptedLetter;
};

class Translator
{
public:
	Translator();
	~Translator();
	bool pushMapping(const std::vector<MappingPair> &mappings);
	bool popMapping();

	// Question marks fill untranslated chars in the output.
	std::string getTranslation(const std::string &input);
private:
	Translator(const Translator &other);
	Translator operator=(const Translator &other);

	TranslatorImpl *m_impl;
};
*/

class TranslatorImpl {
    private List<MyHash<Character, Character>> stack;
    private final char untranslated = '?';
    
    TranslatorImpl(){
        stack = new ArrayList<>();
        MyHash<Character, Character> transTable = new MyHash<Character, Character>();
        for(int i=0; i<=26; i++){
            char c = (char) (i+97);
            transTable.associate(c, untranslated);
        }
        stack.add(transTable);
    }
    
    // mappings: how to translate the specified group of letters from their encrypted version to their decrypted version.
    boolean pushMapping(List<MappingPair> mappings){
        // updating most recent mapping table
        MyHash<Character, Character> copy = new MyHash<Character, Character>();
        MyHash<Character, Character> top = stack.get(stack.size()-1);
        
        for(int i=0; i<=26; i++){
            char c = (char) (i+97);
            copy.associate(c, top.find(c));
        }
        
        for(MappingPair mp : mappings){
            char encrypted = Character.toLowerCase(mp.encryptedLetter);
            char decrypted = Character.toLowerCase(mp.decryptedLetter);
            
            // Bad input
            if(!Character.isLetter(encrypted) || !Character.isLetter(decrypted)) 
                return false;
            // Inconsistent mapping
            if(copy.find(encrypted) != untranslated)
                return false;
            
            copy.associate(encrypted, decrypted);
        }
        stack.add(copy);
        return true;
    }
    
    boolean popMapping(){ 
        if(stack.size() == 1) return false;
    
        stack.remove(stack.size()-1); 
        return true;
    }
    
    // Use mapping table to translate input String
    String getTranslation(String input){
        // keep case of original input message
        // non-letters are copied as is from original input message
        
        MyHash<Character, Character> transTable = stack.get(stack.size()-1);
        
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<input.length(); i++){
            char encrypted = input.charAt(i);
            if(Character.isLetter(encrypted)){
                char decrypted = transTable.find(Character.toLowerCase(encrypted));
                if(Character.isUpperCase(encrypted))
                    decrypted = Character.toUpperCase(decrypted);
                sb.append(decrypted);
            }
            else
                sb.append(input.charAt(i));
        }
        return sb.toString();
    }
}