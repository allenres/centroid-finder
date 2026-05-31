refactoring
- VideoTestApp no longer needed (java video library test file)
- make interface for LargestGroupFinder

improving error handling
- check for valid video or path in VideoCentroidApp

tests
- more tests for video centroid finder

writing documentation
- new video processor classes need JavaDocs
- comments on dfs explore group method

improving performance
- skip every 10 frames in video centroid processor, do we need to analyze every single frame?