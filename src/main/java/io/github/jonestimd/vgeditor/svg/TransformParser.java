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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.xml.sax.Attributes;

public class TransformParser {
    private static final Pattern SVG_PATTERN = Pattern.compile("(matrix|translate|scale|rotate|skew[XY])\\(([+\\-,. 0-9]+)\\)");

    public static void setTransform(Node node, Attributes attributes) {
        String transform = attributes.getValue("transform");
        if (transform != null) {
            Collection<Transform> transforms = parse(transform);
            node.getTransforms().addAll(transforms);
        }
    }

    public static Collection<Transform> parse(String svgTransforms) {
        List<Transform> transforms = new ArrayList<>();
        Matcher matcher = SVG_PATTERN.matcher(svgTransforms);
        while (matcher.find()) {
            String name = matcher.group(1);
            List<Double> args = parseArgs(matcher.group(2));
            if ("translate".equals(name)) transforms.add(0, translate(args));
            else if ("scale".equals(name)) transforms.add(0, scale(args));
            else if ("rotate".equals(name)) transforms.add(0, rotate(args));
            else if ("skewX".equals(name)) transforms.add(0, skewX(args));
            else if ("skewY".equals(name)) transforms.add(0, skewY(args));
            else if ("matrix".equals(name)) transforms.add(0, matrix(args));
        }
        return transforms;
    }

    private static List<Double> parseArgs(String args) {
        return Arrays.stream(args.trim().split("(, *| +)")).map(Double::parseDouble).collect(Collectors.toList());
    }

    private static Translate translate(List<Double> args) {
        double x = args.get(0);
        double y = args.size() > 1 ? args.get(1) : 0d;
        return new Translate(x, y);
    }

    private static Scale scale(List<Double> args) {
        double x = args.get(0);
        double y = args.size() > 1 ? args.get(1) : x;
        return new Scale(x, y);
    }

    private static Rotate rotate(List<Double> args) {
        double angle = args.get(0);
        if (args.size() < 3) return new Rotate(angle);
        return new Rotate(angle, args.get(1), args.get(2));
    }

    private static Affine skewX(List<Double> args) {
        double angle = args.get(0);
        return new Affine(1, 0, Math.tan(angle * Math.PI / 180), 1, 0, 0);
    }

    private static Affine skewY(List<Double> args) {
        double angle = args.get(0);
        return new Affine(1, Math.tan(angle * Math.PI / 180), 0, 1, 0, 0);
    }

    private static Affine matrix(List<Double> args) {
        double mxx = (args.get(0));
        double mxy = (args.get(1));
        double myx = (args.get(2));
        double myy = (args.get(3));
        double tx = (args.get(4));
        double ty = (args.get(5));
        return new Affine(mxx, mxy, tx, myx, myy, ty);
    }
}
