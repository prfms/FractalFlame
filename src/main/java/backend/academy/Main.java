package backend.academy;

import backend.academy.Transformations.HeartTransformation;
import backend.academy.Transformations.SphericalTransformation;
import java.io.IOException;
import java.text.DecimalFormat;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
@SuppressWarnings("checkstyle:magicnumber")
public class Main {
    public static void main(String[] args) throws IOException {
        Config config = Config.loadConfig("src/main/resources/config.json");
        int height = config.getHeight();
        int width = config.getWidth();
        int iterationCount = config.getIterationCount();
        int affineCount = config.getAffineCount();
        int samples = config.getSamples();

        log.info("Running single-threaded version...");
        FractalImage fractalImageSingleThread = new FractalImage(
            height,
            width,
            iterationCount,
            affineCount,
            samples,
            new SphericalTransformation(),
            new HeartTransformation()
        );
        Pixel[][] imageSingle = fractalImageSingleThread.createSingleThread();
        long singleThreadTime = fractalImageSingleThread.spentTimeSingle();
        Renderer rendererSingle = new Renderer(imageSingle);
        rendererSingle.saveAndRenderFractal("fractal_single", ImageFormat.PNG, true);
        log.info("Single-thread version completed. Time spent: {} ms", singleThreadTime);

        log.info("Running multi-threaded version...");
        FractalImage fractalImageMultiThread = new FractalImage(
            height,
            width,
            iterationCount,
            affineCount,
            samples,
            new SphericalTransformation(),
            new HeartTransformation()
        );
        Pixel[][] imageMulti = fractalImageMultiThread.createMultiThread();
        long multiThreadTime = fractalImageMultiThread.spentTimeMulti();
        Renderer rendererMulti = new Renderer(imageMulti);
        rendererMulti.saveAndRenderFractal("fractal_multi", ImageFormat.PNG, true);
        log.info("Multi-thread version completed. Time spent: {} ms", multiThreadTime);

        double improvementPercentage = ((double) (singleThreadTime - multiThreadTime) / singleThreadTime) * 100;
        DecimalFormat df = new DecimalFormat("#.##");
        log.info("Performance improvement: {}%", df.format(improvementPercentage));
    }
}
