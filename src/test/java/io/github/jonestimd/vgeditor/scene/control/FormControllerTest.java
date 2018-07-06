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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import io.github.jonestimd.vgeditor.JavafxTest;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.swing.assertions.Assertions.*;
import static org.mockito.Mockito.*;

public class FormControllerTest extends JavafxTest {
    private static final String FIELD_ID = "field1";
    private Pane root = mock(Pane.class);
    private FormController controller = new FormController();
    private TextField field;

    @Before
    public void loadForm() throws Exception {
        field = new TextField();
        field.setId(FIELD_ID);
        setControllerValue(controller, "root", root);
        when(root.getChildren()).thenReturn(FXCollections.observableArrayList(new Label("Field 1"), field));
        controller.initialize();
    }

    @Test
    public void getField() throws Exception {
        assertThat(controller.getField(FIELD_ID)).isSameAs(field);
    }

    @Test
    public void getValueReturnsDefault() throws Exception {
        double defaultValue = Math.random()*1000;

        assertThat(controller.getValue(FIELD_ID, defaultValue)).isEqualTo(defaultValue);
    }

    @Test
    public void getValueReturnsCurrentValue() throws Exception {
        double value = Math.random()*1000;
        controller.setValue(FIELD_ID, value);

        assertThat(controller.getValue(FIELD_ID, value)).isEqualTo(value);
    }

    @Test
    public void setValueUpdatesField() throws Exception {
        double value = Math.random()*1000;
        controller.setValue(FIELD_ID, value);

        assertThat(field.getText()).isEqualTo(Preferences.numberFormat().format(value));

        assertThat(controller.validFields()).containsExactly(FIELD_ID);
    }

    @Test
    public void setValueToNullClearsField() throws Exception {
        field.setText("12345");
        controller.setValue(FIELD_ID, null);

        assertThat(field.getText()).isEqualTo("");

        assertThat(controller.validFields()).isEmpty();
    }

    @Test
    public void validFieldsReturnsIdsOfNonemptyFields() throws Exception {
        assertThat(controller.validFields()).isEmpty();

        controller.setValue(FIELD_ID, 123d);

        assertThat(controller.validFields()).containsExactly(FIELD_ID);
    }

    @Test
    public void clearResetsFieldsAndValues() throws Exception {
        controller.setValue(FIELD_ID, 1234d);

        controller.clear();

        assertThat(controller.validFields()).isEmpty();
        assertThat(field.getText()).isEmpty();
    }

    @Test
    public void removeListener() throws Exception {
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        controller.addListener(listener);
        controller.setValue(FIELD_ID, 1234d);
        field.setText("5678");

        controller.removeListener(listener);
        controller.onKeyEvent(new KeyEvent(field, null, KeyEvent.KEY_PRESSED, "", "", null, false, false, false, false));

        verifyZeroInteractions(listener);
    }

    @Test
    public void onKeyEventNotifiesListeners() throws Exception {
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        controller.addListener(listener);
        controller.setValue(FIELD_ID, 1234d);
        field.setText("5678");

        controller.onKeyEvent(new KeyEvent(field, null, KeyEvent.KEY_PRESSED, "", "", null, false, false, false, false));

        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(captor.capture());
        assertThat(captor.getValue().getOldValue()).isEqualTo(1234d);
        assertThat(captor.getValue().getNewValue()).isEqualTo(5678d);
        assertThat(controller.validFields()).containsExactly(FIELD_ID);
        assertThat(controller.getValue(FIELD_ID, null)).isEqualTo(5678d);
    }

    @Test
    public void onKeyEventClearsValueForEmptyField() throws Exception {
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        controller.addListener(listener);
        controller.setValue(FIELD_ID, 1234d);
        field.setText("");

        controller.onKeyEvent(new KeyEvent(field, null, KeyEvent.KEY_PRESSED, "", "", null, false, false, false, false));

        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(captor.capture());
        assertThat(captor.getValue().getOldValue()).isEqualTo(1234d);
        assertThat(captor.getValue().getNewValue()).isEqualTo(null);
        assertThat(controller.validFields()).isEmpty();
        assertThat(controller.getValue(FIELD_ID, null)).isNull();
    }
}