package com.anna.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class IndexedHashMap<K, V> extends LinkedHashMap<K, V> {

    public int getPositionOfValue(V value){
        int i=0;
        for(Map.Entry<K, V> entry:this.entrySet()){
            V obj = entry.getValue();
            if(obj.equals(value)){
                break;
            }
            i++;
        }
        return i;
    }

    public int getPositionOfKey(K key){
        int i=0;
        for(Map.Entry<K, V> entry:this.entrySet()){
            K obj = entry.getKey();
            if(obj.equals(key)){
                break;
            }
            i++;
        }
        return i;
    }
}
