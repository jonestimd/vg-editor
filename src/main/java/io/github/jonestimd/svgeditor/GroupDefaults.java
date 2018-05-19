package io.github.jonestimd.svgeditor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.github.jonestimd.svgeditor.svg.AttributeParser;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import org.xml.sax.Attributes;

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
