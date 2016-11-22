package com.anna.util;

import android.os.Build;
import android.util.Log;

import com.anna.BuildConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class IndexedHashMap<K, V> extends LinkedHashMap<K, V> {

    public int getPositionOfKey(K key) {
        int i = 0;
        if (BuildConfig.DEBUG) {
            Log.d("Schlüssel", key.toString());
        }
        for (Map.Entry<K, V> entry : this.entrySet()) {
            K obj = entry.getKey();
            if (BuildConfig.DEBUG) {
                Log.d("Objekt", obj.toString());
            }
            if (obj.equals(key)) {
                break;
            }
            i++;
        }
        return i;
    }

    public V getValueAt(int position) {
        int i = 0;
        for (Map.Entry<K, V> entry : this.entrySet()) {
            if (i == position) {
                return entry.getValue();
            }
            i++;
        }
        return null;
    }
}
