package io.github.allenres.centroidFinder;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.List;

import org.bytedeco.javacv.*;

/*
For each frame:

Convert frame to BufferedImage
Find connected groups
Find largest group
Write timestamp + centroid to CSV

*/
public class VideoCentroidProcessor {

    public void process(String inputVideo, String outputCsv, int targetColor, int threshold) {

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideo);
             PrintWriter writer = new PrintWriter(outputCsv)) {

            Java2DFrameConverter biConverter = new Java2DFrameConverter();

            grabber.start();

            Frame frame;

            // -----------------------------
            // Create reusable objects ONCE
            // -----------------------------
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

                // Write the groups information to a CSV file.
                writer.println(
                        timestampSeconds + "," +
                        largestGroup.centroid().x() + "," +
                        largestGroup.centroid().y()
                );
            }

            grabber.stop();
            biConverter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}