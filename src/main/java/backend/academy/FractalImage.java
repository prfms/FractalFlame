package backend.academy;

import backend.academy.Transformations.Transformation;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static backend.academy.AffineTransformation.createRandomAffineTransformation;

public class FractalImage {
    private final Pixel[][] data;
    private final int height;
    private final int width;
    private final int iterationCount;
    private final int affineCount;
    private final int pointsCount;
    private final double xMin;
    private final double xMax;
    private double yMin = -1;
    private double yMax = 1;
    private final static int skipIterations = -20;

    List<Transformation> transformations = new ArrayList<>();

    public FractalImage(
        int height,
        int width,
        int iterationCount,
        int affineCount,
        int pointsCount,
        Transformation... transformations
    ) {
        this.height = height;
        this.width = width;
        this.iterationCount = iterationCount;
        this.affineCount = affineCount;
        this.pointsCount = pointsCount;
        this.xMin = -(double) width / height;
        this.xMax = (double) width / height;
        data = new Pixel[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                data[i][j] = new Pixel();
            }
        }
        this.transformations.addAll(List.of(transformations));
    }

    private List<AffineTransformation> createAffineTransformations() {
        List<AffineTransformation> affineTransformations = new ArrayList<>();
        for (int i = 0; i < affineCount; i++) {
            AffineTransformation at = createRandomAffineTransformation();
            affineTransformations.add(at);
        }
        return affineTransformations;
    }

    public Pixel[][] create() {
        Random random = new Random();
        double x;
        double y;

        List<AffineTransformation> affineTransformations = createAffineTransformations();

        for (int i = 0; i < pointsCount; ++i) {
            x = random.nextDouble(xMin, xMax);
            y = random.nextDouble(yMin, yMax);

            for (int step = skipIterations; step < iterationCount; step++) {
                int trNum = random.nextInt(transformations.size());
                int affNum = random.nextInt(affineCount);

                AffineTransformation affine = affineTransformations.get(affNum);

                Color affineColor = affine.color();

                Point point = affine.apply(new Point(x, y));

                Transformation currTransformation = transformations.get(trNum);
                Point transformatedPoint = currTransformation.apply(point);

                x = transformatedPoint.x();
                y = transformatedPoint.y();

                if (step >= 0 && x <= xMax && y <= yMax && x >= xMin && y >= yMin) {
                    int x1 = width - (int) ((xMax - x) / (xMax - xMin) * width);
                    int y1 = height - (int) ((yMax - y) / (yMax - yMin) * height);

                    if (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) {
                        if (pixel(x1, y1).hitCount() == 0) {

                            pixel(x1, y1).r(affineColor.getRed());
                            pixel(x1, y1).g(affineColor.getGreen());
                            pixel(x1, y1).b(affineColor.getBlue());
                        } else {
                            pixel(x1, y1).r((pixel(x1, y1).r() + affineColor.getRed()) / 2);
                            pixel(x1, y1).g((pixel(x1, y1).g() + affineColor.getGreen()) / 2);
                            pixel(x1, y1).b((pixel(x1, y1).b() + affineColor.getBlue()) / 2);
                        }
                        pixel(x1, y1).incrementHitCount();
                    }
                }
            }
        }

        return data;
    }

    private Pixel pixel(int row, int col) {
            return data[row][col];
    }
}
