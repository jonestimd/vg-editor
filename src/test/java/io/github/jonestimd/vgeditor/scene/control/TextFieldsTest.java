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

import io.github.jonestimd.vgeditor.JavafxTest;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class TextFieldsTest extends JavafxTest {
    @Test
    public void parseDoubleParsesEventSourceFieldValue() throws Exception {
        TextField field = new TextField("6");

        assertThat(TextFields.parseDouble(getKeyEvent(field, KeyEvent.KEY_PRESSED, ""), 0d)).isEqualTo(6d);
    }

    @Test
    public void parseDoubleFallsBackToDefaultValue() throws Exception {
        TextField field = new TextField("");

        assertThat(TextFields.parseDouble(getKeyEvent(field, KeyEvent.KEY_PRESSED, ""), 0d)).isEqualTo(0d);
    }

    @Test
    public void parseDoubleReturnsNoneForNoDigits() throws Exception {
        assertThat(TextFields.parseDouble(new TextField("-")).isPresent()).isFalse();
        assertThat(TextFields.parseDouble(new TextField(".")).isPresent()).isFalse();
        assertThat(TextFields.parseDouble(new TextField("-.")).isPresent()).isFalse();
    }

    @Test
    public void parseDoubleReturnsValueForValidNumber() throws Exception {
        assertThat(TextFields.parseDouble(new TextField("0")).get()).isEqualTo(0d);
        assertThat(TextFields.parseDouble(new TextField(".0")).get()).isEqualTo(0d);
        assertThat(TextFields.parseDouble(new TextField("1")).get()).isEqualTo(1d);
        assertThat(TextFields.parseDouble(new TextField("1.5")).get()).isEqualTo(1.5d);
        assertThat(TextFields.parseDouble(new TextField("-0")).get()).isEqualTo(-0d);
        assertThat(TextFields.parseDouble(new TextField("-.5")).get()).isEqualTo(-0.5d);
        assertThat(TextFields.parseDouble(new TextField("-1")).get()).isEqualTo(-1d);
        assertThat(TextFields.parseDouble(new TextField("-1.")).get()).isEqualTo(-1d);
        assertThat(TextFields.parseDouble(new TextField("-1.5")).get()).isEqualTo(-1.5d);
    }
}