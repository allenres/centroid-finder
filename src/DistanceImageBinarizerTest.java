import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.BufferedImage;

public class DistanceImageBinarizerTest {
    // tests for toBinaryArray
    @Test
    void singlePixel_exactMatch() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0x000000);

        ImageBinarizer binarizer = new DistanceImageBinarizer(
                new EuclideanColorDistance(), 0x000000, 10
        );

        int[][] result = binarizer.toBinaryArray(image);

        int[][] expected = {
                {1}
        };

        assertArrayEquals(expected, result);
    }

    @Test
    void singlePixel_farFromTarget() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0xFFFFFF);

        ImageBinarizer binarizer = new DistanceImageBinarizer(
                new EuclideanColorDistance(), 0x000000, 10
        );

        int[][] result = binarizer.toBinaryArray(image);

        int[][] expected = {
                {0}
        };

        assertArrayEquals(expected, result);
    }
    
    @Test
    void rectangularImage_dimensionCheck() {
        BufferedImage image = new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB);

        // Row 0
        image.setRGB(0, 0, 0x000000);
        image.setRGB(1, 0, 0xFFFFFF);
        image.setRGB(2, 0, 0x000000);

        // Row 1
        image.setRGB(0, 1, 0xFFFFFF);
        image.setRGB(1, 1, 0x000000);
        image.setRGB(2, 1, 0xFFFFFF);

        ImageBinarizer binarizer = new DistanceImageBinarizer(
                new EuclideanColorDistance(), 0x000000, 10
        );

        int[][] result = binarizer.toBinaryArray(image);

        int[][] expected = {
                {1, 0, 1},
                {0, 1, 0}
        };

        assertArrayEquals(expected, result);
    }

    @Test
    void uniformImage_allPixelsSame() {
        BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                image.setRGB(x, y, 0x000000);
            }
        }

        ImageBinarizer binarizer = new DistanceImageBinarizer(
                new EuclideanColorDistance(), 0x000000, 10
        );

        int[][] result = binarizer.toBinaryArray(image);

        int[][] expected = {
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        };

        assertArrayEquals(expected, result);
    }

    @Test
    void twoByTwo_mixedPixels() {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

        image.setRGB(0, 0, 0x000000); // close
        image.setRGB(1, 0, 0xFFFFFF); // far
        image.setRGB(0, 1, 0x010101); // close-ish
        image.setRGB(1, 1, 0xF0F0F0); // far

        ImageBinarizer binarizer = new DistanceImageBinarizer(
                new EuclideanColorDistance(), 0x000000, 10
        );

        int[][] result = binarizer.toBinaryArray(image);

        int[][] expected = {
                {1, 0},
                {1, 0}
        };

        assertArrayEquals(expected, result);
    }
    // tests for toBufferedImage
}
