package backend.academy.Transformations;

import backend.academy.Point;

public class LinearTransformation implements Transformation {
    @Override
    public Point apply(Point point) {
        return new Point(point.x(), point.y());
    }
}
