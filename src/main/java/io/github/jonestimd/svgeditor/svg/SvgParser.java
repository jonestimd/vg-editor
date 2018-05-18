package io.github.jonestimd.svgeditor.svg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import com.google.common.collect.ImmutableMap;
import io.github.jonestimd.svgeditor.GroupDefaults;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SvgParser {
    public static Collection<Node> parse(File file) throws IOException, ParserConfigurationException, SAXException {
        SvgSaxHandler handler = new SvgSaxHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.newSAXParser().parse(file, handler);
        return handler.nodes;
    }

    private static class SvgSaxHandler extends DefaultHandler {
        private Map<String, Consumer<Attributes>> tagHandlers = ImmutableMap.<String, Consumer<Attributes>>builder()
                .put("g", this::addGroup)
                .put("line", this::addLine)
                .put("circle", this::addCircle)
                .put("rect", this::addRect)
                .put("path", this::addPath)
                .put("image", this::addImage)
                .build();
        private List<Node> nodes = new ArrayList<>();
        private Group group = null;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (uri.equals("http://www.w3.org/2000/svg") && tagHandlers.containsKey(qName)) {
                tagHandlers.get(qName).accept(attributes);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (uri.equals("http://www.w3.org/2000/svg") && qName.equals("g")) {
                this.group = (Group) this.group.getParent();
            }
        }

        private void addGroup(Attributes attributes) {
            Group group = new Group();
            TransformParser.setTransform(group, attributes);
            group.setUserData(new GroupDefaults(group, attributes));
            if (this.group != null) this.group.getChildren().add(group);
            else nodes.add(group);
            this.group = group;
        }

        private void addNode(Node node) {
            if (group == null) nodes.add(node);
            else group.getChildren().add(node);
        }

        private void addLine(Attributes attributes) {
            double x1 = Double.parseDouble(attributes.getValue("x1"));
            double y1 = Double.parseDouble(attributes.getValue("y1"));
            double x2 = Double.parseDouble(attributes.getValue("x2"));
            double y2 = Double.parseDouble(attributes.getValue("y2"));
            addNode(setStyle(attributes, new Line(x1, y1, x2, y2)));
        }

        private void addCircle(Attributes attributes) {
            double cx = Double.parseDouble(attributes.getValue("cx"));
            double cy = Double.parseDouble(attributes.getValue("cy"));
            double r = Double.parseDouble(attributes.getValue("r"));
            addNode(setStyle(attributes, new Circle(cx, cy, r)));
        }

        private void addRect(Attributes attributes) {
            double x = Double.parseDouble(attributes.getValue("x"));
            double y = Double.parseDouble(attributes.getValue("y"));
            double width = Double.parseDouble(attributes.getValue("width"));
            double height = Double.parseDouble(attributes.getValue("height"));
            addNode(setStyle(attributes, new Rectangle(x, y, width, height)));
        }

        private void addPath(Attributes attributes) {
            SVGPath path = new SVGPath();
            path.setContent(attributes.getValue("d"));
            addNode(setStyle(attributes, path));
        }

        private void addImage(Attributes attributes) {
            String href = attributes.getValue("http://www.w3.org/1999/xlink", "href");
            if (href != null) {
                Image image;
                if (href.startsWith("data:image/png;base64,")) {
                    byte[] data = Base64.getMimeDecoder().decode(href.substring(22));
                    image = new Image(new ByteArrayInputStream(data));
                }
                else image = new Image(href);
                ImageView imageView = new ImageView(image);
                imageView.setX(Double.parseDouble(attributes.getValue("x")));
                imageView.setY(Double.parseDouble(attributes.getValue("y")));
                TransformParser.setTransform(imageView, attributes);
                addNode(imageView);
            }
        }

        private Shape setStyle(Attributes attributes, Shape shape) {
            if (this.group != null) {
                GroupDefaults userData = (GroupDefaults) this.group.getUserData();
                if (userData != null) {
                    userData.setStroke(shape);
                    userData.setFill(shape);
                }
            }
            String fill = attributes.getValue("fill");
            if (fill != null) {
                if (fill.equals("none")) shape.setFill(null);
                else shape.setFill(Paint.valueOf(fill));
            }
            String stroke = attributes.getValue("stroke");
            if (stroke != null) {
                if (stroke.equals("none")) shape.setStroke(null);
                else shape.setStroke(Paint.valueOf(stroke));
            }
            return shape;
        }
    }
}
