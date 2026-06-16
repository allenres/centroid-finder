package io.github.allenres.centroidFinder;

public class CentroidAnalyzerApp {
    public static void main(String[] args) {
        // Check for minimum required arguments
        if (args.length < 1) {
            System.err.println("Error: Missing CSV path.");
            System.err.println("Usage: java -cp target/classes io.github.allenres.centroidFinder.CentroidAnalyzerApp <csvPath> [<minX> <maxX> <minY> <maxY>]");
            System.exit(1);
        }

        String csvPath = args[0];

        // Set up default region of interest boundaries
        double minX = 0;
        double maxX = 1920;
        double minY = 0;
        double maxY = 1080;

        // Override defaults if the user provided the 4 bounding coordinates
        if (args.length >= 5) {
            try {
                minX = Double.parseDouble(args[1]);
                maxX = Double.parseDouble(args[2]);
                minY = Double.parseDouble(args[3]);
                maxY = Double.parseDouble(args[4]);
            } catch (NumberFormatException e) {
                System.err.println("Error: Region coordinates must be numbers. Using defaults.");
            }
        }

        System.out.println("Analyzing data from: " + csvPath);
        System.out.printf("Region of Interest : X[%s, %s] Y[%s, %s]\n", minX, maxX, minY, maxY);
        System.out.println("-----------------------------------------");

        // Run analysis
        CentroidAnalyzer analyzer = new CentroidAnalyzer();
        analyzer.loadCsv(csvPath);

        double[] avgLoc = analyzer.getAverageLocation();
        int totalFrames = analyzer.getTotalFramesTracked();
        double totalDistance = analyzer.getTotalDistanceTraveled();
        
        // Use the dynamic boundaries here
        double timeInRegion = analyzer.getTimeSpentInArea(minX, maxX, minY, maxY);

        // Print Results
        System.out.println("Centroid Tracking Report");
        System.out.println();
        System.out.println("Total Frames Tracked: " + totalFrames);
        System.out.printf("Average Location    : X: %.2f, Y: %.2f\n", avgLoc[0], avgLoc[1]);
        System.out.printf("Total Path Distance : %.2f pixels\n", totalDistance);
        System.out.printf("Time spent in region: %.2f seconds\n", timeInRegion);
    }
}