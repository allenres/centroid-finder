package io.github.allenres.centroidFinder;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.List;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;

/*
For each frame:

Convert frame to BufferedImage
Find connected groups
Find largest group
Write timestamp + centroid to CSV
 
*/
public class VideoCentroidProcessor {
    public void process(String inputVideo, String outputCsv, int targetColor, int threshold) {

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideo)) {

            grabber.start();

            long timestampMicroseconds = grabber.getTimestamp();
            double timestampSeconds = timestampMicroseconds / 1000000.0;

            Java2DFrameConverter biConverter = new Java2DFrameConverter();

            Frame frame;

            while ((frame = grabber.grabImage()) != null) {

                // convert frame to BufferedImage
                BufferedImage image = biConverter.convert(frame);

                // Create the DistanceImageBinarizer with a EuclideanColorDistance instance.
                ColorDistanceFinder distanceFinder = new EuclideanColorDistance();
                ImageBinarizer binarizer = new DistanceImageBinarizer(distanceFinder, targetColor, threshold);

                // Binarize the frame.
                int[][] binaryArray = binarizer.toBinaryArray(image);
                BufferedImage binaryImage = binarizer.toBufferedImage(binaryArray);

                // Create an ImageGroupFinder using a BinarizingImageGroupFinder with a
                // DFS-based BinaryGroupFinder.
                ImageGroupFinder groupFinder = new BinarizingImageGroupFinder(binarizer, new DfsBinaryGroupFinder());

                // Find connected groups in the frame.
                // The BinarizingImageGroupFinder is expected to internally binarize the image,
                // then locate connected groups of white pixels.
                List<Group> groups = groupFinder.findConnectedGroups(binaryImage);

                // find largest group
                LargestGroupFinder largestGroupFinder = new LargestGroupFinder();
                Group largestGroup = largestGroupFinder.findLargest(groups);

                // Write the groups information to a CSV file.
                try (PrintWriter writer = new PrintWriter(outputCsv)) {
                    writer.println(timestampSeconds + largestGroup.centroid().toString());
                } catch (Exception e) {
                    System.err.println("Error writing " + outputCsv + ".csv");
                    e.printStackTrace();
                }
            }

            grabber.stop();
            biConverter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
