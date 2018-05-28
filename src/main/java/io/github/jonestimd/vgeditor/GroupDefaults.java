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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.github.jonestimd.vgeditor.svg.AttributeParser;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import org.xml.sax.Attributes;

/**
 * Default values for shapes in a {@link Group}.  Stored on the {@link Group} as the user data property.
 */
public class GroupDefaults {
    private final Group owner;
    private final Map<String, String> attributes = new HashMap<>();
    private Paint stroke;
    private Paint fill;

    public GroupDefaults(Group owner, Attributes attributes) {
        this.owner = owner;
        for (int i = 0; i < attributes.getLength(); i++) {
            this.attributes.put(attributes.getLocalName(i), attributes.getValue(i));
        }
        this.stroke = AttributeParser.getPaint(attributes, "stroke").orElse(null);
        this.fill = AttributeParser.getPaint(attributes, "fill").orElse(null);
    }

    private <T> T getValue(Function<GroupDefaults, T> getter) {
        Parent node = this.owner;
        T value = getter.apply(this);
        while (value == null && node != null) {
            node = node.getParent();
            value = getter.apply((GroupDefaults) node.getUserData());
        }
        return value;
    }

    public String getString(String name) {
        return getValue(groupDefaults -> groupDefaults.attributes.get(name));
    }

    public void setStroke(Shape shape) {
        Paint stroke = getValue(groupDefaults -> groupDefaults.stroke);
        if (stroke != null) shape.setStroke(stroke);
    }

    public void setFill(Shape shape) {
        Paint fill = getValue(groupDefaults -> groupDefaults.fill);
        if (fill != null) shape.setFill(fill);
    }
}
