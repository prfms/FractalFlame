package backend.academy;

import java.awt.Color;
import java.util.Random;
import static java.lang.Math.pow;

public record AffineTransformation(
    double a,
    double b,
    double c,
    double d,
    double e,
    double f,
    Color color
) {
    private static final int MAX_RGB = 255;

    public static AffineTransformation createRandomAffineTransformation() {
        double a = 1.0;
        double b = 1.0;
        double d = 1.0;
        double e = 1.0;
        Random random = new Random();
        while (!(pow(a, 2) + pow(d, 2) < 1
            && pow(b, 2) + pow(e, 2) < 1
            && pow(a, 2) + pow(b, 2) + pow(d, 2) + pow(e, 2) < 1 + pow((a * e - b * d), 2))) {
            a = random.nextDouble() * 2 - 1; // [-1,1]
            b = random.nextDouble() * 2 - 1;
            d = random.nextDouble() * 2 - 1;
            e = random.nextDouble() * 2 - 1;
        }
        double c = random.nextDouble() * 2 - 1;
        double f = random.nextDouble() * 2 - 1;
        Color color = new Color(
            random.nextInt(0, MAX_RGB + 1),
            random.nextInt(0, MAX_RGB + 1),
            random.nextInt(0, MAX_RGB + 1)
        );

        return new AffineTransformation(a, b, c, d, e, f, color);
    }

    public Point apply(Point point) {
        double x = point.x() * this.a + point.y() * this.b + this.c;
        double y = point.x() * this.d + point.y() * this.e + this.f;

        return new Point(x, y);
    }
}
