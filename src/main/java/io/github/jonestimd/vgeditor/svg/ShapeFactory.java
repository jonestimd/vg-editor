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
package io.github.jonestimd.vgeditor.svg;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Optional;

import io.github.jonestimd.vgeditor.model.CircleModel;
import io.github.jonestimd.vgeditor.model.EllipseModel;
import io.github.jonestimd.vgeditor.model.GroupDefaults;
import io.github.jonestimd.vgeditor.model.ImageModel;
import io.github.jonestimd.vgeditor.model.LineModel;
import io.github.jonestimd.vgeditor.model.PathModel;
import io.github.jonestimd.vgeditor.model.PolygonModel;
import io.github.jonestimd.vgeditor.model.PolylineModel;
import io.github.jonestimd.vgeditor.model.RectangleModel;
import io.github.jonestimd.vgeditor.model.ShapeModel;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.xml.sax.Attributes;

/**
 * Factory class for creating shapes from SVG elements.
 */
public class ShapeFactory {
    public static final String DEFAULT_FONT_FAMILY = "Liberation Sans";
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

    protected String getString(String name, String defaultValue) {
        String value = attributes.getValue(name);
        if (value == null && group != null) value = ((GroupDefaults) group.getUserData()).getString(name);
        return value != null ? value : defaultValue;
    }

    public Line getLine() {
        return setStyle(new LineModel(group, getDouble("x1"), getDouble("y1"), getDouble("x2"), getDouble("y2"))).getShape();
    }

    public Circle getCircle() {
        return setStyle(new CircleModel(group, getDouble("cx"), getDouble("cy"), getDouble("r"))).getShape();
    }

    public Ellipse getEllipse() {
        return setStyle(new EllipseModel(group, getDouble("cx"), getDouble("cy"), getDouble("rx"), getDouble("ry"))).getShape();
    }

    public Rectangle getRect() {
        return setStyle(new RectangleModel(group, getDouble("x"), getDouble("y"), getDouble("width"), getDouble("height"))).getShape();
    }

    public Path getPath() {
        return setStyle(new PathModel(group, new PathParser().parse(attributes.getValue("d")))).getShape();
    }

    public Polygon getPolygon() {
        return setStyle(new PolygonModel(group, getPoints())).getShape();
    }

    public Polyline getPolyline() {
        return setStyle(new PolylineModel(group, getPoints())).getShape();
    }

    private double[] getPoints() {
        String[] xy = attributes.getValue("points").split("(,| +)");
        double[] points = new double[xy.length];
        for (int i = 0; i < xy.length; i++) {
            points[i] = Double.parseDouble(xy[i]);
        }
        return points;
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
            ImageModel model = new ImageModel(group, image);
            model.setX(getDouble("x"));
            model.setY(getDouble("y"));
            TransformParser.setTransform(model, attributes);
            return Optional.of(model.getImageView());
        }
        return Optional.empty();
    }

    public Text getText() {
        Text text = new Text();
        text.setX(getDouble("x"));
        text.setY(getDouble("y"));
        setStyle(text);
        String fontFamily = getString("font-family", DEFAULT_FONT_FAMILY);
        if (Font.getFontNames(fontFamily).size() == 0) fontFamily = DEFAULT_FONT_FAMILY;
        String fontWeight = getString("font-weight", DEFAULT_FONT_WEIGHT);
        Font font = Font.font(fontFamily, FontWeight.findByName(fontWeight), getFontSize());
        text.setFont(font);
        return text;
    }

    private double getFontSize() {
        String size = getString("font-size", null);
        if (size != null && size.endsWith("px")) return Double.parseDouble(size.substring(0, size.length()-2));
        return DEFAULT_FONT_SIZE;
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
        TransformParser.setTransform(shape, attributes);
        return shape;
    }

    private <T extends ShapeModel> T setStyle(T model) {
        if (this.group != null) {
            GroupDefaults userData = (GroupDefaults) this.group.getUserData();
            if (userData != null) {
                userData.setStroke(model);
                userData.setFill(model);
            }
        }
        AttributeParser.setPaint(attributes, "fill", model::setFill);
        AttributeParser.setPaint(attributes, "stroke", model::setStroke);
        TransformParser.setTransform(model, attributes);
        return model;
    }
}
