# Centroid Finder
 
## What is centroid finder? 
Centroid finder takes a video input file and for each frame of the video locates the largest connected group of pixels matching a target color and writes the centroid positions of those groups to a CSV file.

## How does it work?
- It uses the JavaCV library to extract each frame of the video, and the frame read using FFmpegFrameGrabber. 
- Each frame is binarized with a Euclidean color distance algorithm, using the threshold parameter.
- Pixels within the provided threshold distance of the target color are marked as foreground (white) pixels.
- Groups of white pixels are identified using a DFS algorithm, and the largest group is selected to have its centroid cooridinates written to the output csv, alongside the frame timestamp in seconds. It is in the following format: timestampSeconds, centroidX, centroidY

## Backend Server
The centroid finder server helps to serve a front-end user interface.

You can read our documentation for the salamander api in the file "salamander-api-postman-documentation.md"

To use our front end please visit: https://github.com/allenres/salamander-tracker


## Required Software
We recommened using Chocolatey package manager for easy installation on Windows.
- Node
- FFmpeg (choco install ffmpeg)
- Maven (choco install maven)
- Java


## Setup Instructions
- In the processor directory, run
-   mvn install
- In the server directory, run
-   npm install
Finally, configure your dot ENV 
- PORT=3000, VIDEOS_DIR, RESULTS_DIR, STATUS_DIR, JAR_PATH

## How do I run it?
To run the program alone, execute:
- java -jar target/centroid-finder-1.0-SNAPSHOT.jar input_file_path output_csv_path hex_color threshold
- If you intend to use this program with a front-end, run the Express server:
- node server/index.js
