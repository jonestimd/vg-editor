// The MIT License (MIT)
//
// Copyright (c) 2018 Tim Jones
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
package io.github.jonestimd.vgeditor.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LruCache<K, V> {
    private final int size;
    private final Map<K, V> cache = new HashMap<>();
    private final List<K> usage;

    public LruCache(int size) {
        this.size = size;
        usage = new ArrayList<>(size);
    }

    public void put(K key, V value) {
        if (cache.size() == size) {
            cache.remove(usage.remove(size-1));
        }
        cache.put(key, value);
        usage.add(0, key);
    }

    public V get(K key) {
        V value = cache.get(key);
        if (value != null && usage.get(0) != key) {
            usage.remove(key);
            usage.add(0, key);
        }
        return value;
    }

    public V get(K key, Function<K, V> factory) {
        V value = get(key);
        if (value == null) {
            put(key, value = factory.apply(key));
        }
        return value;
    }
}
