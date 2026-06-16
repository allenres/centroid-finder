package io.github.allenres.centroidFinder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CentroidAnalyzer {

    private final List<CentroidData> dataPoints = new ArrayList<>();

    /**
     * Loads centroid data from the generated CSV file.
     * Assumes format: timestampSeconds,centroidX,centroidY
     */
    public void loadCsv(String csvPath) {
        dataPoints.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split(",");
                if (parts.length == 3) {
                    double timestamp = Double.parseDouble(parts[0].trim());
                    int x = Integer.parseInt(parts[1].trim());
                    int y = Integer.parseInt(parts[2].trim());
                    dataPoints.add(new CentroidData(timestamp, x, y));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    /**
     * Calculates the average (mean) X and Y coordinate.
     */
    public double[] getAverageLocation() {
        if (dataPoints.isEmpty())
            return new double[] { 0, 0 };

        double sumX = 0;
        double sumY = 0;
        for (CentroidData point : dataPoints) {
            sumX += point.x();
            sumY += point.y();
        }
        return new double[] { sumX / dataPoints.size(), sumY / dataPoints.size() };
    }

    /**
     * Calculates time spent within a defined rectangular boundary.
     * Since frame drops happen, we estimate time based on the gaps between frames.
     */
    public double getTimeSpentInArea(double minX, double maxX, double minY, double maxY) {
        if (dataPoints.size() < 2)
            return 0.0;

        double totalTime = 0.0;

        for (int i = 0; i < dataPoints.size() - 1; i++) {
            CentroidData current = dataPoints.get(i);
            CentroidData next = dataPoints.get(i + 1);

            // Check if the centroid falls inside the boundary
            if (current.x() >= minX && current.x() <= maxX &&
                    current.y() >= minY && current.y() <= maxY) {

                // Add the duration between this frame and the next frame
                totalTime += (next.timestamp() - current.timestamp());
            }
        }
        return totalTime;
    }

    /**
     * Calculates total path distance moved by the centroid.
     */
    public double getTotalDistanceTraveled() {
        double totalDistance = 0.0;
        for (int i = 0; i < dataPoints.size() - 1; i++) {
            CentroidData p1 = dataPoints.get(i);
            CentroidData p2 = dataPoints.get(i + 1);

            // Euclidean distance formula: sqrt((x2-x1)^2 + (y2-y1)^2)
            totalDistance += Math.sqrt(Math.pow(p2.x() - p1.x(), 2) + Math.pow(p2.y() - p1.y(), 2));
        }
        return totalDistance;
    }

    public int getTotalFramesTracked() {
        return dataPoints.size();
    }
}