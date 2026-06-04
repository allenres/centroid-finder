package io.github.allenres.centroidFinder;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VideoCentroidAppTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void shouldPrintUsageWhenTooFewArguments() {
        String[] args = {
                "input.mp4",
                "output.csv",
                "FF0000"
        };

        VideoCentroidApp.main(args);

        assertTrue(
                outContent.toString().contains("Usage: java -jar videoprocessor.jar"));
    }

    @Test
    void shouldRejectNonIntegerThreshold() throws Exception {
        File file = File.createTempFile("test", ".mp4");
        file.deleteOnExit();

        String[] args = {
                file.getAbsolutePath(),
                "output.csv",
                "FF0000",
                "abc"
        };

        VideoCentroidApp.main(args);

        assertTrue(
                errContent.toString().contains("Threshold must be an integer."),
                "Expected threshold validation error but got: " + errContent);
    }

    @Test
    void shouldRejectInvalidHexColor() throws Exception {
        File file = File.createTempFile("test", ".mp4");
        file.deleteOnExit();

        String[] args = {
                file.getAbsolutePath(),
                "output.csv",
                "NOTHEX",
                "50"
        };

        VideoCentroidApp.main(args);

        assertTrue(
                errContent.toString().contains("Invalid hex target color"),
                "Expected hex color validation error but got: " + errContent);
    }

    @Test
    void shouldRejectMissingVideoFile() {
        String[] args = {
                "does-not-exist.mp4",
                "output.csv",
                "FF0000",
                "50"
        };

        VideoCentroidApp.main(args);

        assertTrue(
                errContent.toString()
                        .contains("Input video file does not exist"));
    }

    @Test
    void shouldRejectDirectoryInsteadOfFile() throws Exception {
        File directory = Files.createTempDirectory("video-test").toFile();
        directory.deleteOnExit();

        String[] args = {
                directory.getAbsolutePath(),
                "output.csv",
                "FF0000",
                "50"
        };

        VideoCentroidApp.main(args);

        assertTrue(
                errContent.toString()
                        .contains("Input path is not a file"));
    }

    @Test
    void shouldRejectUnsupportedExtension() throws Exception {
        File file = File.createTempFile("test", ".txt");
        file.deleteOnExit();

        String[] args = {
                file.getAbsolutePath(),
                "output.csv",
                "FF0000",
                "50"
        };

        VideoCentroidApp.main(args);

        assertTrue(
                errContent.toString()
                        .contains("Unsupported video format"));
    }

    @Test
    void shouldRejectNegativeThreshold() throws Exception {
        File file = File.createTempFile("test", ".mp4");
        file.deleteOnExit();

        String[] args = {
                file.getAbsolutePath(),
                "output.csv",
                "FF0000",
                "-1"
        };

        VideoCentroidApp.main(args);

        assertTrue(
                errContent.toString()
                        .contains("Threshold must be non-negative"));
    }
}