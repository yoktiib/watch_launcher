package com.pomohouse.launcher.fragment.avatar;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Admin on 11/18/2016 AD.
 */

public class AvatarLinkedMap<K, V> extends LinkedHashMap<K, V> {

    V getValue(int i) {
        Entry<K, V> entry = this.getEntry(i);
        if (entry == null) return null;
        return entry.getValue();
    }

    private Entry<K, V> getEntry(int i) {
        Set<Entry<K, V>> entries = entrySet();
        int j = 0;
        for (Entry<K, V> entry : entries)
            if (j++ == i) return entry;
        return null;
    }

    K getKey(int i) {
        Entry<K, V> entry = this.getEntry(i);
        if (entry == null) return null;
        return entry.getKey();
    }
}

