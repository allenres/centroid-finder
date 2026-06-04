package io.github.allenres.centroidFinder;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Entry point for the Video Centroid application.
 *
 * This class is responsible for parsing command-line arguments, validating
 * input formats, converting user-provided values into usable types, and
 * delegating processing to the VideoCentroidProcessor.
 *
 * The application expects arguments in the following order:
 *
 * input_path output_csv hex_target_color threshold
 *
 * Where:
 * - input_path is the path to the input video file
 * - output_csv is the file path where results will be written
 * - hex_target_color is a 6-digit hexadecimal RGB value (RRGGBB format)
 * - threshold is an integer representing the maximum allowed color distance
 *
 * The hex color string is parsed into a 24-bit integer (0xRRGGBB). If parsing
 * fails, the program terminates with an error message.
 *
 * The threshold must be a valid integer. If it is not, the program prints an
 * error message and exits.
 *
 * If insufficient arguments are provided, the program prints usage instructions
 * and exits without executing processing.
 *
 * After successful validation and parsing, this class creates a
 * VideoCentroidProcessor instance and invokes the processing pipeline.
 */
public class VideoCentroidApp {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println(
                    "Usage: java -jar videoprocessor.jar <input_path> <output_csv> <hex_target_color> <threshold>");
            return;
        }

        String inputVideoPath = args[0];
        String outputCsv = args[1];
        String hexTargetColor = args[2];

        // Validate video file
        File videoFile = new File(inputVideoPath);

        if (!videoFile.exists()) {
            System.err.println("Input video file does not exist: " + inputVideoPath);
            return;
        }

        if (!videoFile.isFile()) {
            System.err.println("Input path is not a file: " + inputVideoPath);
            return;
        }

        if (!videoFile.canRead()) {
            System.err.println("Input video file cannot be read: " + inputVideoPath);
            return;
        }

        String fileName = videoFile.getName().toLowerCase();
        if (!(fileName.endsWith(".mp4")
                || fileName.endsWith(".avi")
                || fileName.endsWith(".mov")
                || fileName.endsWith(".mkv"))) {
            System.err.println("Unsupported video format. Supported formats: mp4, avi, mov, mkv");
            return;
        }

        int threshold;
        try {
            threshold = Integer.parseInt(args[3]);
            if (threshold < 0) {
                System.err.println("Threshold must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Threshold must be an integer.");
            return;
        }

        // Parse the target color from a hex string (format RRGGBB) into a 24-bit
        // integer (0xRRGGBB)
        int targetColor;
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
