package io.github.jonestimd.svgeditor;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import org.xml.sax.Attributes;

public class GroupDefaults {
    private final Group owner;
    private Paint stroke;
    private Paint fill;

    public GroupDefaults(Group owner, Attributes attributes) {
        this.owner = owner;
        String stroke = attributes.getValue("stroke");
        if (stroke != null) {
            this.stroke = Paint.valueOf(stroke);
        }
        String fill = attributes.getValue("fill");
        if (fill != null) {
            this.fill = Paint.valueOf(fill);
        }
    }

    public void setStroke(Shape shape) {
        Parent parent = this.owner.getParent();
        Paint stroke = this.stroke;
        while (stroke == null && parent != null) {
            stroke = ((GroupDefaults) parent.getUserData()).stroke;
            parent = parent.getParent();
        }
        if (stroke != null) shape.setStroke(stroke);
    }

    public void setFill(Shape shape) {
        Parent parent = this.owner.getParent();
        Paint fill = this.fill;
        while (fill == null && parent != null) {
            fill = ((GroupDefaults) parent.getUserData()).fill;
            parent = parent.getParent();
        }
        if (fill != null) shape.setFill(fill);
    }
}
