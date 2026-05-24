package io.github.allenres.centroidFinder;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import javax.imageio.ImageIO;

/*
java -jar videoprocessor.jar inputPath outputCsv targetColor threshold
*/
public class VideoCentroidApp {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar videoprocessor.jar <input_path> <output_csv> <hex_target_color> <threshold>");
            return;
        }

        String inputVideoPath = args[0];
        String outputCsv = args[1];
        String hexTargetColor = args[2];
        int threshold = 0;
        try {
            threshold = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Threshold must be an integer.");
            return;
        }

        // Parse the target color from a hex string (format RRGGBB) into a 24-bit
        // integer (0xRRGGBB)
        int targetColor = 0;
        try {
            targetColor = Integer.parseInt(hexTargetColor, 16);
        } catch (NumberFormatException e) {
            System.err.println("Invalid hex target color. Please provide a color in RRGGBB format.");
            return;
        }

        VideoCentroidProcessor processor = new VideoCentroidProcessor();
        processor.process(inputVideoPath, outputCsv, targetColor, threshold);

    }
}
