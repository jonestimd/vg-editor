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
package io.github.jonestimd.vgeditor.shape.path;

import javafx.geometry.Point2D;
import javafx.scene.shape.ArcTo;

/**
 * @see <a href="https://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes">Conversion from endpoint to center parameterization</a>
 */
public class ArcToSegment extends PathSegment<ArcTo> {
    private static final double TWO_PI = Math.PI*2;

    /** midpoint of the arc */
    private final Point2D midpoint;
    /** center of the ellipse */
    private final Point2D center;
    /** x axis rotation in radians */
    private final double phi;
    /** sweep start angle in radians */
    private final double angleStart;
    /** sweep end angle in radians */
    private final double angleEnd;

    public ArcToSegment(Point2D start, ArcTo element) {
        super(start, element, new Point2D(element.getX(), element.getY()));
        phi = Math.toRadians(element.getXAxisRotation());
        Point2D p1p = rotate((start.getX()-end.getX())/2, (start.getY()-end.getY())/2, -phi);

        // Compute center
        double rx = Math.abs(element.getRadiusX()), ry = Math.abs(element.getRadiusY());
        double q = centerCoefficient(rx, ry, p1p.getX(), p1p.getY());
        double cxp = q*rx*p1p.getY()/ry, cyp = -q*ry*p1p.getX()/rx;
        center = rotate(cxp, cyp, phi).add((start.getX()+end.getX())/2, (start.getY()+end.getY())/2);

        final double ux = (p1p.getX()-cxp)/rx;
        final double uy = (p1p.getY()-cyp)/ry;
        final double vx = (-p1p.getX()-cxp)/rx;
        final double vy = (-p1p.getY()-cyp)/ry;
        // Compute the angle start
        double n = Math.sqrt(square(ux)+square(uy));
        double sign = ((uy < 0.0) ? -1.0 : 1.0);
        this.angleStart = sign*Math.acos(ux/n);

        // Compute the angle extent
        n = Math.sqrt((square(ux)+square(uy))*(square(vx)+square(vy)));
        double p = ux*vx+uy*vy;
        sign = ((ux*vy-uy*vx < 0.0) ? -1.0 : 1.0);
        double angleExtent = sign*Math.acos(p/n);
        if (element.isSweepFlag()) {
            if (angleExtent < 0) angleExtent += TWO_PI;
        }
        else if (angleExtent > 0) angleExtent -= TWO_PI;
        this.angleEnd = this.angleStart+angleExtent;

        double midAngle = (this.angleStart+this.angleEnd)/2;
        midpoint = rotate(rx*Math.cos(midAngle), ry*Math.sin(midAngle), phi).add(center);
    }

    /**
     * @param rx ellipse x radius
     * @param ry ellipse y radius
     * @param x1p start x relative to midpoint between start and end
     * @param y1p start y relative to midpoint between start and end
     */
    private double centerCoefficient(double rx, double ry, double x1p, double y1p) {
        double sign = ((element.isLargeArcFlag() == element.isSweepFlag()) ? -1.0 : 1.0);
        double rxSquared = square(rx), rySquared = square(ry);
        double x1pSquared = square(x1p), y1pSquared = square(y1p);
        return sign*Math.sqrt(Math.max(0, (rxSquared*rySquared-rxSquared*y1pSquared-rySquared*x1pSquared)/
                (rxSquared*y1pSquared+rySquared*x1pSquared)));
    }

    @Override
    public Point2D getMidpoint() {
        return midpoint;
    }

    @Override
    public double getDistanceSquared(Point2D point) {
        Point2D r = point.subtract(center);
        double angle = Math.atan(r.getY()/r.getX())-phi;
        if (r.getX() < 0) angle += Math.PI;
        if (isBetweenStartAndEnd(angle)) {
            double rx = element.getRadiusX()*Math.cos(angle);
            double ry = element.getRadiusY()*Math.sin(angle);
            return squaredDistance(rotate(rx, ry, phi), point.subtract(center));
        }
        return Double.MAX_VALUE;
    }

    private boolean isBetweenStartAndEnd(double angle) {
        double normalized = normalize(angle);
        return Math.signum(this.angleStart-normalized) == Math.signum(normalized-this.angleEnd);
    }

    private double normalize(double angle) {
        if (isCounterClockwise()) while (angle < angleStart) angle += TWO_PI;
        else while (angle > angleStart) angle -= TWO_PI;
        return angle;
    }

    private boolean isCounterClockwise() {
        return element.isSweepFlag();
    }

    private static double square(double value) {
        return value*value;
    }

    private static Point2D rotate(double x, double y, double angle) {
        double cos = Math.cos(angle), sin = Math.sin(angle);
        return new Point2D(x*cos-y*sin, x*sin+y*cos);
    }
}
