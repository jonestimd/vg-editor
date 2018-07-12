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
package io.github.jonestimd.vgeditor;

import java.lang.reflect.Field;

import javafx.event.EventType;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import org.junit.Rule;

public abstract class JavafxTest {
    @Rule
    public JavaFxThreadingRule rule = new JavaFxThreadingRule();

    protected void setValue(Object target, String fieldName, Object value) throws Exception {
        getField(target, fieldName).set(target, value);
    }

    protected <T> T getValue(Object target, String fieldName, Class<T> valueClass) throws Exception {
        return valueClass.cast(getField(target, fieldName).get(target));
    }

    private Field getField(Object target, String fieldName) throws NoSuchFieldException {
        Class<?> fieldOwner = target.getClass();
        while (fieldOwner != null) {
            try {
                Field field = fieldOwner.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                fieldOwner = fieldOwner.getSuperclass();
            }
        }
        throw new NoSuchFieldException(target.getClass().getName() + "." + fieldName);
    }

    protected KeyEvent getKeyEvent(TextInputControl field, EventType<KeyEvent> eventType, String text) {
        return new KeyEvent(field, null, eventType, text, text, null, false, false, false, false);
    }
}
