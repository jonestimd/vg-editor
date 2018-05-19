package io.github.jonestimd.svgeditor.svg;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;

import io.github.jonestimd.svgeditor.GroupDefaults;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.xml.sax.Attributes;

public class ShapeFactory {
    public static final String DEFAULT_FONT_FAMILY = "Dialog";
    public static final String DEFAULT_FONT_WEIGHT = "normal";
    public static final double DEFAULT_FONT_SIZE = 12d;

    protected final Attributes attributes;
    protected final Group group;

    public ShapeFactory(Attributes attributes, Group group) {
        this.attributes = attributes;
        this.group = group;
    }

    protected double getDouble(String name) {
        return Double.valueOf(attributes.getValue(name));
    }

    protected String getString(String name, Function<GroupDefaults, String> getter, String defaultValue) {
        String value = attributes.getValue(name);
        if (value == null && group != null) value = getter.apply((GroupDefaults) group.getUserData());
        return value != null ? value : defaultValue;
    }

    public Line getLine() {
        return setStyle(new Line(getDouble("x1"), getDouble("y1"), getDouble("x2"), getDouble("y2")));
    }

    public Circle getCircle() {
        return setStyle(new Circle(getDouble("cx"), getDouble("cy"), getDouble("r")));
    }

    public Rectangle getRect() {
        return setStyle(new Rectangle(getDouble("x"), getDouble("y"), getDouble("width"), getDouble("height")));
    }

    public SVGPath getPath() {
        SVGPath path = new SVGPath();
        path.setContent(attributes.getValue("d"));
        return setStyle(path);
    }

    public Optional<ImageView> getImage() {
        String href = attributes.getValue("http://www.w3.org/1999/xlink", "href");
        if (href != null) {
            Image image;
            if (href.startsWith("data:image/png;base64,")) {
                byte[] data = Base64.getMimeDecoder().decode(href.substring(22));
                image = new Image(new ByteArrayInputStream(data));
            }
            else image = new Image(href);
            ImageView imageView = new ImageView(image);
            imageView.setX(getDouble("x"));
            imageView.setY(getDouble("y"));
            TransformParser.setTransform(imageView, attributes);
            return Optional.of(imageView);
        }
        return Optional.empty();
    }

    public Text getText() {
        Text text = new Text();
        text.setX(getDouble("x"));
        text.setY(getDouble("y"));
        setStyle(text);

        String fontFamily = getString("font-family", GroupDefaults::getFontFamily, DEFAULT_FONT_FAMILY);
        String fontWeight = getString("font-weight", GroupDefaults::getFontWeight, DEFAULT_FONT_WEIGHT);

        Double fontSize = null;
        String fs = attributes.getValue("font-size");
        if (fs  != null && fs.endsWith("px")) fontSize = Double.parseDouble(fs.substring(0, fs.length()-2));
        else if (group != null) fontSize = ((GroupDefaults) group.getUserData()).getFontSize();
        if (fontSize == null) fontSize = DEFAULT_FONT_SIZE;

        Font font = Font.font(fontFamily, FontWeight.findByName(fontWeight), fontSize);
        text.setFont(font);
        return text;
    }

    private <T extends Shape> T setStyle(T shape) {
        if (this.group != null) {
            GroupDefaults userData = (GroupDefaults) this.group.getUserData();
            if (userData != null) {
                userData.setStroke(shape);
                userData.setFill(shape);
            }
        }
        AttributeParser.setPaint(attributes, "fill", shape::setFill);
        AttributeParser.setPaint(attributes, "stroke", shape::setStroke);
        return shape;
    }
}
