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
package io.github.jonestimd.vgeditor.scene.control;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/** Fix for null class loader when using <fx:include> with resources attribute */
public class ResourceBundleWrapper extends ResourceBundle {
    private final ResourceBundle bundle;

    ResourceBundleWrapper(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    protected Object handleGetObject(String key) {
        return bundle.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return bundle.getKeys();
    }

    @Override
    public Locale getLocale() {
        return bundle.getLocale();
    }

    @Override
    public boolean containsKey(String key) {
        return bundle.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return bundle.keySet();
    }
}
