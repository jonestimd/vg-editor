package io.github.jonestimd.svgeditor.svg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import javafx.geometry.Point2D;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;

import static java.lang.Character.*;

/**
 * Parses the {@code d} attribute of an SVG path.
 * <strong>Note:</strong> This class is not thread safe.
 */
public class PathParser {
    private static final Pattern SVG_PATTERN = Pattern.compile("([mMlLhHvVcCqQsStTaAzZ])([+\\-,. 0-9]+)?");
    private static final Map<String, Handler> HANDLER_MAP = ImmutableMap.<String, Handler>builder()
            .put("m", PathParser::moveTo)
            .put("l", PathParser::lineTo)
            .put("h", PathParser::horizontalTo)
            .put("v", PathParser::verticalTo)
            .put("c", PathParser::cubicCurveTo)
            .put("s", PathParser::smoothCubicCurveTo)
            .put("q", PathParser::quadCurveTo)
            .put("t", PathParser::smoothQuadCurveTo)
            .put("a", PathParser::arcTo)
            .put("z", (parser, absolute, args) -> parser.path.getElements().add(new ClosePath())).build();

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
            Handler handler = HANDLER_MAP.get(command.toLowerCase());
            if (handler != null) handler.accept(this, isUpperCase(command.charAt(0)), args);
        }
        return path;
    }

    private void setLastPos(PathElement element, double x, double y) {
        setLastPos(element, x, y, x, y);
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

    private void arcTo(boolean absolute, List<Double> args) {
        for (int i = 0; i+6 < args.size(); i += 7) {
            arcTo(absolute, args.get(i), args.get(i+1), args.get(i+2), args.get(i+3) != 0, args.get(i+4) != 0, args.get(i+5), args.get(i+6));
        }
    }

    private void arcTo(boolean absolute, double rx, double ry, double xAngle, boolean largeArc, boolean sweep, double x, double y) {
        ArcTo arcTo = new ArcTo(rx, ry, xAngle, x, y, largeArc, sweep);
        arcTo.setAbsolute(absolute);
        setLastPos(arcTo, x, y);
    }

    private static List<Double> parseArgs(String args) {
        return args != null ? Arrays.stream(args.trim().split("(, *| +)")).map(Double::parseDouble).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

    private interface Handler {
        void accept(PathParser parser, boolean absolute, List<Double> args);
    }
}
