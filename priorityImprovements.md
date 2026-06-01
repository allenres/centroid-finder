# refactoring code (top 2)

1. **Adjust csv naming output**: The output for the csv is improper

2. **Video centroid app error handling**: Adjust the video centroid app with error handling

# adding tests (top 2)

1. **Unit tests for centroid calculation in DfsBinaryGroupFinder**: Test the core algorithm with various group shapes (single pixel, line, square, irregular), edge pixels, and boundary conditions. Verify that integer division for centroid coordinates is calculated correctly.

2. **Unit tests for image binarization in DistanceImageBinarizer**: Test with different thresholds, target colors, and image sizes. Include tests for pixels at the threshold boundary, fully white/black images, and edge cases like threshold = 0 or very large values.

# improving error handling (top 2)

1. **Add logging and error context in VideoCentroidProcessor and ImageSummaryApp**: Replace generic `e.printStackTrace()` with structured logging that includes operation context (which file, which frame/stage failed), enabling root cause diagnosis in production. Log both successful operations and failures.

2. **Implement input validation and path sanitization in videoService.js**: Validate filename parameters to prevent path traversal attacks; validate targetColor and threshold are within expected ranges before spawning Java processes. Return specific 400 errors for invalid input rather than 500 errors.

# writing documentation (top 2)

1. **Add JSDoc/inline documentation to videoController.js endpoints**: Document expected query parameter formats (hex color format RRGGBB, threshold range), response structures, possible error codes, and provide examples. This clarifies what clients should send and what they'll receive.

2. **Create developer setup guide**: Document how to build the Java JAR, what dependencies are required, how to configure environment variables (.env template), and steps to run both the server and test the API. Include example requests using the Postman collection.