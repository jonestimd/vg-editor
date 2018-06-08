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
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class IterableUtils {
    /**
     * Find the items in an {@link Iterable} for which a function's result is minimal.  If multiple items have the minimal
     * result then they are all returned.
     * @return the items for which the result of {@code getter} is the smallest
     */
    public static <T> List<T> minBy(Iterable<T> items, Function<T, Double> getter) {
        List<T> result = new ArrayList<>();
        Iterator<T> iterator = items.iterator();
        if (iterator.hasNext()) {
            T item = iterator.next();
            result.add(item);
            double minValue = getter.apply(item);
            while (iterator.hasNext()) {
                item = iterator.next();
                double value = getter.apply(item);
                if (value == minValue) result.add(item);
                else if (value < minValue) {
                    result.clear();
                    result.add(item);
                    minValue = value;
                }
            }
        }
        return result;
    }
}
