package io.github.allenres.centroidFinder;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.List;

import org.bytedeco.javacv.*;

/**
 * Processes a video file frame-by-frame to locate the largest connected group
 * of pixels matching a target color and writes the centroid positions of those
 * groups to a CSV file.
 *
 * The input video is read using FFmpegFrameGrabber. Each frame is converted
 * into a BufferedImage and binarized using a DistanceImageBinarizer with a
 * Euclidean color distance algorithm. Pixels within the provided threshold
 * distance of the target color are marked as foreground pixels.
 *
 * Connected foreground pixel groups are identified using a DFS-based
 * BinaryGroupFinder. For each frame containing at least one group, the largest
 * group is selected and its centroid coordinates are written to the output CSV
 * file alongside the frame timestamp in seconds.
 *
 * Each output CSV row has the format:
 *
 * timestampSeconds, centroidX, centroidY
 *
 * Frames with no image data or no detected groups are skipped.
 *
 * Resources such as the frame grabber and CSV writer are automatically closed
 * after processing completes.
 *
 * @param inputVideo the path to the input video file to process
 * @param outputCsv the path to the CSV file where centroid data will be written
 * @param targetColor the target RGB color encoded as an integer
 * @param threshold the maximum Euclidean color distance allowed when matching
 *                  pixels to the target color
 */
public class VideoCentroidProcessor {

    public void process(String inputVideo, String outputCsv, int targetColor, int threshold) {

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideo);
             PrintWriter writer = new PrintWriter(outputCsv)) {

            Java2DFrameConverter biConverter = new Java2DFrameConverter();

            grabber.start();

            Frame frame;

            ColorDistanceFinder distanceFinder = new EuclideanColorDistance();
            ImageBinarizer binarizer =
                    new DistanceImageBinarizer(distanceFinder, targetColor, threshold);

            BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder();
            LargestGroupFinder largestGroupFinder = new LargestGroupFinder();

            while ((frame = grabber.grabImage()) != null) {

                if (frame.image == null) continue;

                // get time stamp
                long timestampMicroseconds = grabber.getTimestamp();
                double timestampSeconds = timestampMicroseconds / 1000000.0;

                // Convert frame to BufferedImage
                BufferedImage image = biConverter.convert(frame);

                // Binarize the frame.
                int[][] binaryArray = binarizer.toBinaryArray(image);

                // Find connected groups in the frame.
                // The DFS-based BinaryGroupFinder is used directly on the binary image.
                List<Group> groups = groupFinder.findConnectedGroups(binaryArray);

                if (groups.isEmpty()) continue;

                // find largest group
                Group largestGroup = largestGroupFinder.findLargest(groups);

                CentroidData data = new CentroidData(timestampSeconds, largestGroup.centroid().x(), largestGroup.centroid().y());
                // Write the groups information to a CSV file.
                writer.println(
                        data.timestamp() + "," +
                        data.x() + "," +
                        data.y()
                );
            }

            grabber.stop();
            biConverter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}