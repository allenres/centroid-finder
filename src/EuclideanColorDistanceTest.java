import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class EuclideanColorDistanceTest {

    private final EuclideanColorDistance distanceFinder = new EuclideanColorDistance();

    @Test
    void testSameColorDistanceIsZero() {
        int color = 0x123456;
        assertEquals(0.0, distanceFinder.distance(color, color), 1e-9);
    }

    @Test
    void testBlackAndWhiteDistance() {
        int black = 0x000000;
        int white = 0xFFFFFF;

        double expected = Math.sqrt(255 * 255 * 3);
        assertEquals(expected, distanceFinder.distance(black, white), 1e-9);
    }

    @Test
    void testPrimaryColors() {
        int red = 0xFF0000;
        int green = 0x00FF00;
        int blue = 0x0000FF;

        double rg = Math.sqrt(255 * 255 + 255 * 255);
        double rb = Math.sqrt(255 * 255 + 255 * 255);
        double gb = Math.sqrt(255 * 255 + 255 * 255);

        assertEquals(rg, distanceFinder.distance(red, green), 1e-9);
        assertEquals(rb, distanceFinder.distance(red, blue), 1e-9);
        assertEquals(gb, distanceFinder.distance(green, blue), 1e-9);
    }

    @Test
    void testSingleChannelDifference() {
        int colorA = 0x000000;
        int colorB = 0x0000FF; // only blue differs

        assertEquals(255.0, distanceFinder.distance(colorA, colorB), 1e-9);
    }

    @Test
    void testSymmetry() {
        int colorA = 0xABCDEF;
        int colorB = 0x123456;

        double d1 = distanceFinder.distance(colorA, colorB);
        double d2 = distanceFinder.distance(colorB, colorA);

        assertEquals(d1, d2, 1e-9);
    }

    @Test
    void testSmallDifference() {
        int colorA = 0x010203;
        int colorB = 0x020304;

        double expected = Math.sqrt(1 * 1 + 1 * 1 + 1 * 1);
        assertEquals(expected, distanceFinder.distance(colorA, colorB), 1e-9);
    }

    @Test
    void testArbitraryValues() {
        int colorA = 0x112233;
        int colorB = 0x445566;

        int rDiff = 0x11 - 0x44;
        int gDiff = 0x22 - 0x55;
        int bDiff = 0x33 - 0x66;

        double expected = Math.sqrt(
                rDiff * rDiff +
                gDiff * gDiff +
                bDiff * bDiff
        );

        assertEquals(expected, distanceFinder.distance(colorA, colorB), 1e-9);
    }

    @Test
    void testGetRGBComponents_Black() {
        List<Integer> rgb = distanceFinder.getRGBComponents(0x000000);

        assertEquals(0, rgb.get(0)); // R
        assertEquals(0, rgb.get(1)); // G
        assertEquals(0, rgb.get(2)); // B
    }

    @Test
    void testGetRGBComponents_White() {
        List<Integer> rgb = distanceFinder.getRGBComponents(0xFFFFFF);

        assertEquals(255, rgb.get(0));
        assertEquals(255, rgb.get(1));
        assertEquals(255, rgb.get(2));
    }

    @Test
    void testGetRGBComponents_PrimaryColors() {
        List<Integer> red = distanceFinder.getRGBComponents(0xFF0000);
        List<Integer> green = distanceFinder.getRGBComponents(0x00FF00);
        List<Integer> blue = distanceFinder.getRGBComponents(0x0000FF);

        assertEquals(List.of(255, 0, 0), red);
        assertEquals(List.of(0, 255, 0), green);
        assertEquals(List.of(0, 0, 255), blue);
    }

    @Test
    void testGetRGBComponents_ArbitraryValue() {
        List<Integer> rgb = distanceFinder.getRGBComponents(0x123456);

        assertEquals(0x12, rgb.get(0)); // R = 18
        assertEquals(0x34, rgb.get(1)); // G = 52
        assertEquals(0x56, rgb.get(2)); // B = 86
    }

    @Test
    void testGetRGBComponents_Boundaries() {
        List<Integer> rgb = distanceFinder.getRGBComponents(0x00FF7F);

        assertEquals(0, rgb.get(0));     // R
        assertEquals(255, rgb.get(1));   // G
        assertEquals(127, rgb.get(2));   // B
    }

    @Test
    void testGetRGBComponents_ListSize() {
        List<Integer> rgb = distanceFinder.getRGBComponents(0xABCDEF);

        assertEquals(3, rgb.size(), "RGB list should always have exactly 3 elements");
    }
}