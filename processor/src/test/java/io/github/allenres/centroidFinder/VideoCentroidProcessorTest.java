package io.github.allenres.centroidFinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.jupiter.api.Test;

public class VideoCentroidProcessorTest {

    @Test
    public void testCsvIsValidFormat() throws Exception {

        // Arrange
        VideoCentroidProcessor processor = new VideoCentroidProcessor();
        String output = "sampleOutput/result.csv";

        processor.process(
                "sampleInput/test.mp4",
                output,
                0xFFFFFF,
                10
        );

        File file = new File(output);
        assertTrue(file.exists(), "CSV file was not created");

        boolean hasValidRows = false;

        // Act + Validate
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");

                // must have exactly 3 values
                assertEquals(3, parts.length, "Invalid CSV row: " + line);

                try {
                    Double.parseDouble(parts[0]); // timestamp
                    Double.parseDouble(parts[1]); // x
                    Double.parseDouble(parts[2]); // y
                } catch (NumberFormatException e) {
                    fail("Non-numeric value found in row: " + line);
                }

                hasValidRows = true;
            }
        }

        // Final assertion: file isn't empty
        assertTrue(hasValidRows, "CSV contains no valid rows");
    }
}
