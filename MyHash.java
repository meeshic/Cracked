import java.lang.Math;

/*template <class mapFrom, class mapTo>
class MyHash
{
public:
	MyHash(const double maxLoad);
	~MyHash();
	void reset();
	void associate(const mapFrom &from, const mapTo &to);
	mapTo *find(const mapFrom &from) const;
	int getNumItems() const;
	double getLoad() const; 
private:
// add your private data and methods here
};
*/

// like a map
// You MUST not use any STL container classes to implement your MyHash class.


public class MyHash<K, V> {
    private double loadFactor = 0.5;
    private int size = 0;
    private int capacity = 100;
    private Entry[] table;
    
    private class Entry<K, V>{
        Entry<K, V> next = null;
        K key;
        V value;
        
        Entry(K key, V value){
            this.key = key;
            this.value = value;
        }
        
        K getKey(){ return this.key; }
        
        V getValue(){ return this.value; }
        
        V updateValue(V value){
            V temp = this.value;
            this.value = value;
            return temp;
        }
    }
    
    MyHash(){
        // set size to initial default size of 100 buckets
        table = new Entry[capacity];
    }
    
    MyHash(double loadFactor){
        table = new Entry[capacity];
        if(loadFactor > 2)
            this.loadFactor = 2.0;
        else if(loadFactor > 0)
            this.loadFactor = loadFactor;
    }
    
    void reset(){
        size = 0;
        capacity = 100;
        table = new Entry[capacity];
    }
    
    private void resize(){
        capacity *= 2;
        size = 0;
        
        Entry[] temp = new Entry[capacity];
        for(Entry<K,V> e : table){
            if(e != null){
                Entry<K,V> tableCurr = e;
                while(tableCurr != null){
                    size++;
                    K key = tableCurr.getKey();
                    V val = tableCurr.getValue();
                    int hash = Math.abs(key.hashCode()) % capacity;
                    
                    if(temp[hash] != null){
                        Entry<K,V> tempCurr = temp[hash];
                        while(tempCurr.next != null){
                            tempCurr = tempCurr.next;
                        }
                        tempCurr.next = new Entry<K,V>(key, val);
                    }
                    else
                        temp[hash] = new Entry<K,V>(key, val);
                    
                    tableCurr = tableCurr.next;
                }
            }
        }
        
        table = temp;
    }
    
    
    void associate(K key, V value){
        int hash = Math.abs(key.hashCode()) % capacity;
        
        // Collision or update
        if(table[hash] != null){
            Entry<K,V> curr = table[hash];
            Entry<K,V> prev = null;
            while(curr != null && !(curr.getKey().equals(key))){
                prev = curr;
                curr = curr.next;
            }
            
            // update
            if(curr != null)
                curr.updateValue(value);
            // chaining
            else{
                prev.next = new Entry<K,V>(key, value);
                size++;
            }
        }
        else{
            table[hash] = new Entry<K,V>(key, value);
            size++;
        }
        
        if(size == capacity*loadFactor)
            resize();
    }
    
    V find(K from){
        int hash = Math.abs(from.hashCode() % capacity);
        
        Entry<K,V> curr = table[hash];
        
        while(curr != null && !(curr.getKey().equals(from))){
            curr = curr.next;
        }
        
        return (curr == null) ? null : curr.getValue();
    }
    
    int getNumItems(){ return size; }
    
    double getLoad(){ return loadFactor; }
}