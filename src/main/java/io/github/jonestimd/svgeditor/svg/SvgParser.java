package io.github.jonestimd.svgeditor.svg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
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
                .put("svg", this::addGroup)
                .put("g", this::addGroup)
                .put("line", this::addLine)
                .put("circle", this::addCircle)
                .put("rect", this::addRect)
                .put("path", this::addPath)
                .put("image", this::addImage)
                .build();
        private List<Node> nodes = new ArrayList<>();
        private Group group = null;
        private Text text = null;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (uri.equals("http://www.w3.org/2000/svg")) {
                if (tagHandlers.containsKey(qName)) {
                    tagHandlers.get(qName).accept(attributes);
                }
                else if ("text".equals(qName)) {
                    text = new ShapeFactory(attributes, group).getText();
                    addNode(text);
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (uri.equals("http://www.w3.org/2000/svg")) {
                if (qName.equals("g")) {
                    this.group = (Group) this.group.getParent();
                }
                else if ("text".equals(qName)) {
                    this.text = null;
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (this.text != null) {
                StringBuilder buffer = new StringBuilder(text.getText());
                text.setText(buffer.append(new String(ch, start, length)).toString());
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
            addNode(new ShapeFactory(attributes, group).getLine());
        }

        private void addCircle(Attributes attributes) {
            addNode(new ShapeFactory(attributes, group).getCircle());
        }

        private void addRect(Attributes attributes) {
            addNode(new ShapeFactory(attributes, group).getRect());
        }

        private void addPath(Attributes attributes) {
            addNode(new ShapeFactory(attributes, group).getPath());
        }

        private void addImage(Attributes attributes) {
            new ShapeFactory(attributes, group).getImage().ifPresent(this::addNode);
        }

        private void setStyle(Attributes attributes, Shape shape) {
            if (this.group != null) {
                GroupDefaults userData = (GroupDefaults) this.group.getUserData();
                if (userData != null) {
                    userData.setStroke(shape);
                    userData.setFill(shape);
                }
            }
            AttributeParser.setPaint(attributes, "fill", shape::setFill);
            AttributeParser.setPaint(attributes, "stroke", shape::setStroke);
        }
    }
}
