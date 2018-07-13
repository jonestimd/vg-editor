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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * Controller for a {@code FXML} form containing only numeric inputs.
 */
public class FormController {
    @FXML
    private Pane root;

    private final Map<String, TextInputControl> fields = new HashMap<>();
    private final Map<String, String> values = new HashMap<>();
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void initialize() {
        root.getChildren().filtered(TextInputControl.class::isInstance).forEach(node -> fields.put(node.getId(), (TextInputControl) node));
    }

    public void addListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public TextInputControl getField(String fieldId) {
        return fields.get(fieldId);
    }

    public String getText(String fieldId) {
        return values.get(fieldId);
    }

    public void setText(String fieldId, String value) {
        fields.get(fieldId).setText(value == null ? "" : value);
        if (value == null) values.remove(fieldId);
        else values.put(fieldId, value);
    }

    public Double getValue(String fieldId, Double defaultValue) {
        return TextFields.parseDouble(values.getOrDefault(fieldId, "")).orElse(defaultValue);
    }

    public void setValue(String fieldId, Double value) {
        setText(fieldId, value == null ? null : Preferences.numberFormat().format(value));
    }

    public Collection<String> validFields() {
        return values.keySet();
    }

    public void clear() {
        values.clear();
        fields.values().forEach(field -> field.setText(""));
    }

    public void onKeyEvent(KeyEvent event) {
        TextInputControl source = (TextInputControl) event.getSource();
        String oldValue = values.get(source.getId());
        if (isValidValue(source, source.getText())) values.put(source.getId(), source.getText());
        else values.remove(source.getId());
        changeSupport.firePropertyChange(source.getId(), oldValue, values.get(source.getId()));
    }

    private boolean isValidValue(TextInputControl field, String value) {
        return value.trim().length() > 0 && !(field.getTextFormatter() instanceof NumericFormatter && !value.matches("-?\\d.*"));
    }
}
