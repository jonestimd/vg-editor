package io.github.jonestimd.svgeditor.svg;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.paint.Paint;
import org.xml.sax.Attributes;

public class AttributeParser {
    private static <T> T parse(Attributes attributes, String name, Function<String, T> converter) {
        String value = attributes.getValue(name);
        return value == null ? null : converter.apply(value);
    }

    public static Paint getPaint(Attributes attributes, String name) {
        return parse(attributes, name, Paint::valueOf);
    }

    public static void setPaint(Attributes attributes, String name, Consumer<Paint> setter) {
        String fill = attributes.getValue(name);
        if (fill != null) {
            if (fill.equals("none")) setter.accept(null);
            else setter.accept(Paint.valueOf(fill));
        }
    }

    public static Double getFontSize(Attributes attributes) {
        String fs = attributes.getValue("font-size");
        return fs != null && fs.endsWith("px") ? Double.parseDouble(fs.substring(0, fs.length()-2)) : null;
    }
}
