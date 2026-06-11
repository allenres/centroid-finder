# Centroid Finder
 
## What is centroid finder? 
Centroid finder takes a video input file and for each frame of the video locates the largest connected group of pixels matching a target color and writes the centroid positions of those groups to a CSV file.

## How does it work?
- It uses the JavaCV library to extract each frame of the video, and the frame read using FFmpegFrameGrabber. 
- Each frame is binarized with a Euclidean color distance algorithm, using the threshold parameter.
- Pixels within the provided threshold distance of the target color are marked as foreground (white) pixels.
- Groups of white pixels are identified using a DFS algorithm, and the largest group is selected to have its centroid cooridinates written to the output csv, alongside the frame timestamp in seconds. It is in the following format: timestampSeconds, centroidX, centroidY

## Setup Instructions

