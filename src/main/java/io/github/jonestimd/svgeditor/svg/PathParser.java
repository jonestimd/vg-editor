package io.github.jonestimd.svgeditor.svg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.geometry.Point2D;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;

/**
 * Parses the {@code d} attribute of an SVG path.
 * <strong>Note:</strong> This class is not thread safe.
 */
public class PathParser {
    private static final Pattern SVG_PATTERN = Pattern.compile("([mMlLhHvVcCqQsStTaAzZ])([+\\-,. 0-9]+)?");
    private double lastX, lastY, prevCX, prevCY;
    private PathElement lastElement;
    private Path path;

    public Path parse(String content) {
        path = new Path();
        lastElement = null;
        Matcher matcher = SVG_PATTERN.matcher(content);
        while (matcher.find()) {
            String command = matcher.group(1);
            List<Double> args = parseArgs(matcher.group(2));
            if ("M".equals(command)) moveTo(true, args);
            else if ("m".equals(command)) moveTo(false, args);
            else if ("L".equals(command)) lineTo(true, args);
            else if ("l".equals(command)) lineTo(false, args);
            else if ("H".equals(command)) horizontalTo(true, args);
            else if ("h".equals(command)) horizontalTo(false, args);
            else if ("V".equals(command)) verticalTo(true, args);
            else if ("v".equals(command)) verticalTo(false, args);
            else if ("C".equals(command)) cubicCurveTo(true, args);
            else if ("c".equals(command)) cubicCurveTo(false, args);
            else if ("S".equals(command)) smoothCubicCurveTo(true, args);
            else if ("s".equals(command)) smoothCubicCurveTo(false, args);
            else if ("Q".equals(command)) quadCurveTo(true, args);
            else if ("q".equals(command)) quadCurveTo(false, args);
            else if ("T".equals(command)) smoothQuadCurveTo(true, args);
            else if ("t".equals(command)) smoothQuadCurveTo(false, args);
            else if ("Z".equalsIgnoreCase(command)) path.getElements().add(new ClosePath());
        }
        return path;
    }

    private void setLastPos(PathElement element, double x, double y) {
        setLastPos(element, x, y, 0, 0);
    }

    private void setLastPos(PathElement element, double x, double y, double cx, double cy) {
        if (element.isAbsolute()) {
            lastX = x;
            lastY = y;
            prevCX = cx;
            prevCY = cy;
        }
        else {
            prevCX = lastX+cx;
            prevCY = lastY+cy;
            lastX += x;
            lastY += y;
        }
        path.getElements().add(element);
        lastElement = element;
    }

    private void moveTo(boolean absolute, List<Double> args) {
        for (int i = 0; i+1 < args.size(); i += 2) {
            moveTo(absolute, args.get(i), args.get(i+1));
        }
    }

    private void moveTo(boolean absolute, double x, double y) {
        MoveTo moveTo = new MoveTo(x, y);
        moveTo.setAbsolute(absolute);
        setLastPos(moveTo, x, y);
    }

    private void lineTo(boolean absolute, List<Double> args) {
        for (int i = 0; i+1 < args.size(); i += 2) {
            lineTo(absolute, args.get(i), args.get(i+1));
        }
    }

    private void lineTo(boolean absolute, double x, double y) {
        LineTo lineTo = new LineTo(x, y);
        lineTo.setAbsolute(absolute);
        setLastPos(lineTo, x, y);
    }

    private void horizontalTo(boolean absolute, List<Double> args) {
        for (Double arg : args) {
            lineTo(absolute, Arrays.asList(arg, absolute ? lastY : 0));
        }
    }

    private void verticalTo(boolean absolute, List<Double> args) {
        for (Double arg : args) {
            lineTo(absolute, Arrays.asList(absolute ? lastX : 0, arg));
        }
    }

    private void cubicCurveTo(boolean absolute, List<Double> args) {
        for (int i = 0; i+5 < args.size(); i += 6) {
            cubicCurveTo(absolute, args.get(i), args.get(i+1), args.get(i+2), args.get(i+3), args.get(i+4), args.get(i+5));
        }
    }

    private void cubicCurveTo(boolean absolute, double cx1, double cy1, double cx2, double cy2, double x, double y) {
        CubicCurveTo curveTo = new CubicCurveTo(cx1, cy1, cx2, cy2, x, y);
        curveTo.setAbsolute(absolute);
        setLastPos(curveTo, x, y, cx2, cy2);
    }

    private void smoothCubicCurveTo(boolean absolute, List<Double> args) {
        for (int i = 0; i+3 < args.size(); i += 4) {
            if (!(lastElement instanceof CubicCurveTo)) prevCX = prevCY = 0;
            Point2D control = getControlPoint(absolute);
            cubicCurveTo(absolute, control.getX(), control.getY(), args.get(i), args.get(i+1), args.get(i+2), args.get(i+3));
        }
    }

    private void quadCurveTo(boolean absolute, List<Double> args) {
        for (int i = 0; i+3 < args.size(); i += 4) {
            quadCurveTo(absolute, args.get(i), args.get(i+1), args.get(i+2), args.get(i+3));
        }
    }

    private void quadCurveTo(boolean absolute, double cx, double cy, double x, double y) {
        QuadCurveTo curveTo = new QuadCurveTo(cx, cy, x, y);
        curveTo.setAbsolute(absolute);
        setLastPos(curveTo, x, y, cx, cy);
    }

    private void smoothQuadCurveTo(boolean absolute, List<Double> args) {
        for (int i = 0; i+1 < args.size(); i += 2) {
            if (!(lastElement instanceof QuadCurveTo)) prevCX = prevCY = 0;
            Point2D control = getControlPoint(absolute);
            quadCurveTo(absolute, control.getX(), control.getY(), args.get(i), args.get(i+1));
        }
    }

    private Point2D getControlPoint(boolean absolute) {
        double cx = 2*lastX-prevCX;
        double cy = 2*lastY-prevCY;
        if (!absolute) {
            cx -= lastX;
            cy -= lastY;
        }
        return new Point2D(cx, cy);
    }

    private static List<Double> parseArgs(String args) {
        return args != null ? Arrays.stream(args.trim().split("(, *| +)")).map(Double::parseDouble).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }
}
