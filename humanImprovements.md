# adding tests
    - VideoCentroidProcessor and LargestGroupFinder
    - VideoCentroidProcessor there is no consistent way to test, so our test file mainly checks structure and output.
    - more tests for video centroid finder

# refactoring code
    - Logic in heavier files can be moved into another file.
    - Adjust csv output logic so results are stored in a testing csv
    - Adjusting the csv file out put because the naming for the file is a bit weird.
    - VideoTestApp no longer needed (java video library test file)
    - make interface for LargestGroupFinder

# improving error handling
    - check for valid video or path in VideoCentroidApp

# writing documentation
    - new video processor classes need JavaDocs
    - comments on dfs explore group method

# improving performance
    - skip every 10 frames in video centroid processor, do we need to analyze every single frame?
