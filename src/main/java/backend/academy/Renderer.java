package backend.academy;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.extern.log4j.Log4j2;
import static java.lang.Math.pow;

@Log4j2
public class Renderer {
    private final Pixel[][] data;
    private final int width;
    private final int height;
    private static final int MAX_RGB = 255;
    private static final double GAMMA = 2.2;

    public Renderer(Pixel[][] data) {
        this.data = data;
        this.width = data.length;
        this.height = data[0].length;
    }

    public void correction() {
        double max = 0.0;
        double gamma = GAMMA;
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                if (pixel(row, col).hitCount() != 0) {
                    pixel(row, col).normal(Math.log10(pixel(row, col).hitCount()));
                    if (pixel(row, col).normal() > max) {
                        max = pixel(row, col).normal();
                    }
                }
            }
        }

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                if (max != 0.0) {
                    pixel(row, col).normal(pixel(row, col).normal() / max);
                }
                pixel(row, col).r((int) (pixel(row, col).r() * pow(pixel(row, col).normal(), (1.0 / gamma))));
                pixel(row, col).g((int) (pixel(row, col).g() * pow(pixel(row, col).normal(), (1.0 / gamma))));
                pixel(row, col).b((int) (pixel(row, col).b() * pow(pixel(row, col).normal(), (1.0 / gamma))));
            }
        }
    }

    public void saveAndRenderFractal(String filename, ImageFormat format,  boolean addGammaCorrection) {
        if (addGammaCorrection) {
            correction();
        }
        String fullFilename = filename + '.' + format.toString().toLowerCase();
        BufferedImage image = getBufferedImage();

        File outputFile = new File(fullFilename);
        try {
            ImageIO.write(image, String.valueOf(format), outputFile);
            log.info("Fractal image saved to: {}", fullFilename);
        } catch (IOException e) {
            log.error("Failed to save fractal image: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private BufferedImage getBufferedImage() {
        BufferedImage image = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Pixel pixel = data[x][y];
                if (pixel != null) {
                    int r = Math.min(MAX_RGB, pixel.r());
                    int g = Math.min(MAX_RGB, pixel.g());
                    int b = Math.min(MAX_RGB, pixel.b());

                    Color color = new Color(r, g, b);
                    image.setRGB(x, y, color.getRGB());
                } else {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return image;
    }

    private Pixel pixel(int row, int col) {
        return data[row][col];
    }
}
