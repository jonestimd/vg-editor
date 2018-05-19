package io.github.jonestimd.svgeditor;

import java.util.function.Function;

import io.github.jonestimd.svgeditor.svg.AttributeParser;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import org.xml.sax.Attributes;

public class GroupDefaults {
    private final Group owner;
    private Paint stroke;
    private Paint fill;
    private String fontFamily;
    private String fontWeight;
    private Double fontSize;

    public GroupDefaults(Group owner, Attributes attributes) {
        this.owner = owner;
        this.stroke = AttributeParser.getPaint(attributes, "stroke");
        this.fill = AttributeParser.getPaint(attributes, "fill");
        fontFamily = attributes.getValue("font-family");
        fontWeight = attributes.getValue("font-weight");
        fontSize = AttributeParser.getFontSize(attributes);
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

    public void setStroke(Shape shape) {
        Paint stroke = getValue(groupDefaults -> groupDefaults.stroke);
        if (stroke != null) shape.setStroke(stroke);
    }

    public void setFill(Shape shape) {
        Paint fill = getValue(groupDefaults -> groupDefaults.fill);
        if (fill != null) shape.setFill(fill);
    }

    public String getFontFamily() {
        return getValue(groupDefaults -> groupDefaults.fontFamily);
    }

    public String getFontWeight() {
        return getValue(groupDefaults -> groupDefaults.fontWeight);
    }

    public Double getFontSize() {
        return getValue(groupDefaults -> groupDefaults.fontSize);
    }
}
