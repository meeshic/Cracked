import java.util.List;
import java.util.ArrayList;

/*class TokenizerImpl
{
public:
   TokenizerImpl(const std::string &separators);
   void tokenize(const std::string &s, std::vector<std::string> &tokens) const;
private:
// add your private data and methods here
};
*/

class TokenizerImpl {
    private String separators;
    private boolean formatted = false;
    
    // ( ) [ ] { { \ ^ $ | ? * + . < > - = !
    
    TokenizerImpl(String separators){
        this.separators = separators;
    }
    
    String[] tokenize(String s){
        if(!formatted){
            separators = format(separators);
            formatted = true;
        }
        
        s = s.trim();
        return removeWhiteSpace(s.split(separators));
    }
    
    private String[] removeWhiteSpace(String[] list){
        List<String> result = new ArrayList<String>(list.length);
        
        for(String s : list){
            if(s.trim().length() != 0)
                result.add(s.toLowerCase());
        }
        
        return result.toArray(new String[result.size()]);
    }
    
    private String format(String s){
        StringBuilder sb = new StringBuilder(s);
        
        int insertIndex = 0;
        for(int i=0; i<s.length(); i++){
            if(isMeta(s.charAt(i))){
                sb.insert(insertIndex, "\\");
                insertIndex++;;
            }
            insertIndex++;
        }
        
        sb.insert(0, "[");
        sb.insert(sb.length(), "]");
        
        return sb.toString();
    }
    
    // Checks for special meta characters that must be escaped
    private boolean isMeta(char c){
        return (c == '[' || c == '-' || c == '^' || c == ']');
    }
}

/*
Complexity:
    - Time:
        > StringBuilder insert(): O(N), N = length of string to shift
        > format(): O(N^2), but this function only runs once during the lifetime of object, assuming separators stay the same
        > String trim(): O(N-# begin and trailing white space) if exists white space at begin/end, otherwise O(1)(string stays the same)
        > String split(): consists of Pattern.compile(...).split(...) = O(N+K)
            >> Pattern compile(...): O(K), K = regex pattern length
            >> Pattern split(...): O(N), N = length of input string
*/
