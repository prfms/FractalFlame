package backend.academy;

import java.io.IOException;
import backend.academy.Transformations.HandkerchiefTransformation;
import backend.academy.Transformations.LinearTransformation;
import backend.academy.Transformations.SwirlTransformation;
import lombok.experimental.UtilityClass;


@UtilityClass
public class Main {
    public static void main(String[] args) throws IOException {
        FractalImage fractal = new FractalImage(1080, 1920, 100_000, 7, 100, new SwirlTransformation());
        Pixel[][] image = fractal.create();
        Renderer renderer = new Renderer(image);
        renderer.saveAndRenderFractal("fractal", ImageFormat.JPEG, true);
    }

}
