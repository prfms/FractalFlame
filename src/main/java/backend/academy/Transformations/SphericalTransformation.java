package backend.academy.Transformations;

import backend.academy.Point;

public class SphericalTransformation implements Transformation {

    @Override
    public Point apply(Point point) {
        double x = point.x();
        double y = point.y();
        double r = 1.0 / (x * x + y * y);

        return new Point(x * r, y * r);
    }
}
