package io.github.jonestimd.svgeditor.svg;

import java.util.Optional;
import java.util.function.Consumer;

import javafx.scene.paint.Paint;
import org.xml.sax.Attributes;

public class AttributeParser {
    public static Optional<Paint> getPaint(Attributes attributes, String name) {
        return Optional.ofNullable(attributes.getValue(name)).map(Paint::valueOf);
    }

    /**
     * Set or clear a paint attribute on a shape.  If the attribute value is {@code none} then {@code setter}
     * will be called with {@code null}.
     * @param attributes SVG node attributes
     * @param name name of the attribute specifying the paint
     * @param setter Shape paint attribute setter
     */
    public static void setPaint(Attributes attributes, String name, Consumer<Paint> setter) {
        String fill = attributes.getValue(name);
        if (fill != null) {
            if (fill.equals("none")) setter.accept(null);
            else setter.accept(Paint.valueOf(fill));
        }
    }
}
