package io.github.allenres.centroidFinder;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;

public class VideoTestApp {

    public static void main(String[] args) {

        String videoFile = "sampleInput/test.mp4";

        try (FFmpegFrameGrabber grabber =
                     new FFmpegFrameGrabber(videoFile)) {

            grabber.start();

            System.out.println("Video width: " + grabber.getImageWidth());
            System.out.println("Video height: " + grabber.getImageHeight());
            System.out.println("Frame count: " + grabber.getLengthInFrames());
            System.out.println("Frame rate: " + grabber.getFrameRate());

            OpenCVFrameConverter.ToMat converter =
                    new OpenCVFrameConverter.ToMat();

            Frame frame;
            int frameNumber = 0;

            while ((frame = grabber.grabImage()) != null) {

                Mat image = converter.convert(frame);

                System.out.println(
                        "Read frame #" + frameNumber +
                        " size = " +
                        image.cols() + "x" + image.rows());

                frameNumber++;
            }

            grabber.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}