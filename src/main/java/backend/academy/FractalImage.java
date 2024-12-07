package backend.academy;

import backend.academy.Transformations.Transformation;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import lombok.Getter;
import static backend.academy.AffineTransformation.createRandomAffineTransformation;

public class FractalImage {
    private final Pixel[][] data;
    private final int height;
    private final int width;
    private final int iterationCount;
    private final int affineCount;
    private final int samples;
    private final int symmetry;
    private final double xMin;
    private final double xMax;
    private double yMin = -1;
    private double yMax = 1;
    private static final int SKIP_ITERATIONS = -20;
    @Getter
    private long spentTimeSingle;
    @Getter
    private long spentTimeMulti;

    private final List<Transformation> transformations;

    public FractalImage(
        int height,
        int width,
        int iterationCount,
        int affineCount,
        int samples,
        int symmetry,
        Transformation... transformations
    ) {
        this.height = height;
        this.width = width;
        this.iterationCount = iterationCount;
        this.affineCount = affineCount;
        this.samples = samples;
        this.xMin = -(double) width / height;
        this.xMax = (double) width / height;
        this.symmetry = symmetry;
        data = new Pixel[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                data[i][j] = new Pixel();
            }
        }
        this.transformations = List.of(transformations);
    }

    private List<AffineTransformation> createAffineTransformations() {
        List<AffineTransformation> affineTransformations = new ArrayList<>();
        for (int i = 0; i < affineCount; i++) {
            AffineTransformation at = createRandomAffineTransformation();
            affineTransformations.add(at);
        }
        return affineTransformations;
    }

    private void processPoint(List<AffineTransformation> affineTransformations, Random random) {
        double x = random.nextDouble(xMin, xMax);
        double y = random.nextDouble(yMin, yMax);

        for (int step = SKIP_ITERATIONS; step < iterationCount; step++) {
            int trNum = random.nextInt(transformations.size());
            int affNum = random.nextInt(affineCount);

            AffineTransformation affine = affineTransformations.get(affNum);
            Color affineColor = affine.color();

            Point point = affine.apply(new Point(x, y));
            Transformation currTransformation = transformations.get(trNum);
            Point transformedPoint = currTransformation.apply(point);

            x = transformedPoint.x();
            y = transformedPoint.y();

            double theta = 0.0;
            for (int s = 0; s < symmetry; ++s) {
                theta += ((2 * Math.PI) / symmetry);
                double rotatedX = x * Math.cos(theta) - y * Math.sin(theta);
                double rotatedY = x * Math.sin(theta) + y * Math.cos(theta);

                if (step >= 0 && rotatedX <= xMax && rotatedY <= yMax && rotatedX >= xMin && rotatedY >= yMin) {
                    int x1 = width - (int) ((xMax - rotatedX) / (xMax - xMin) * width);
                    int y1 = height - (int) ((yMax - rotatedY) / (yMax - yMin) * height);

                    if (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) {
                        synchronized (pixel(x1, y1)) {
                            Pixel pixel = pixel(x1, y1);
                            if (pixel.hitCount() == 0) {
                                pixel.r(affineColor.getRed());
                                pixel.g(affineColor.getGreen());
                                pixel.b(affineColor.getBlue());
                            } else {
                                pixel.r((pixel.r() + affineColor.getRed()) / 2);
                                pixel.g((pixel.g() + affineColor.getGreen()) / 2);
                                pixel.b((pixel.b() + affineColor.getBlue()) / 2);
                            }
                            pixel.incrementHitCount();
                        }
                    }
                }
            }
        }
    }

    public Pixel[][] createSingleThread() {
        long startTime = System.currentTimeMillis();
        Random random = new Random();
        List<AffineTransformation> affineTransformations = createAffineTransformations();

        for (int i = 0; i < samples; ++i) {
            processPoint(affineTransformations, random);
        }

        long endTime = System.currentTimeMillis();
        this.spentTimeSingle = endTime - startTime;

        return data;
    }

    public Pixel[][] createMultiThread() {
        long startTime = System.currentTimeMillis();
        Random random = new Random();
        List<AffineTransformation> affineTransformations = createAffineTransformations();

        try (var service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            for (int i = 0; i < samples; ++i) {
                service.submit(() -> processPoint(affineTransformations, random));
            }
        }

        long endTime = System.currentTimeMillis();
        this.spentTimeMulti = endTime - startTime;

        return data;
    }

    private Pixel pixel(int row, int col) {
        return data[row][col];
    }
}
