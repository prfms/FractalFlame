package backend.academy.Transformations;

import backend.academy.Point;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class HandkerchiefTransformation implements Transformation {
    @Override
    public Point apply(Point point) {
        double x = point.x();
        double y = point.y();
        double r = sqrt(x * x + y * y);
        double theta = Math.atan2(y, x);

        return new Point(r * sin(theta + r), cos(theta - r));
    }
}
