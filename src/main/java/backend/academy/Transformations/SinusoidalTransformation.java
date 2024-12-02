package backend.academy.Transformations;

import backend.academy.Point;
import static java.lang.Math.sin;

public class SinusoidalTransformation implements Transformation {
    @Override
    public Point apply(Point point) {
        return new Point(sin(point.x()), sin(point.y()));
    }
}
