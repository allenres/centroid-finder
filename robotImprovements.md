Error Handling in VideoCentroidApp
The app lacks validation for the input video path and output directory. You should:

Validate that the input video file exists before processing
Check that the output directory is writable
Handle cases where the JAR receives invalid arguments gracefully
Consider adding better error messages to stdout/stderr

LargestGroupFinder 
- JavaDocs
- turn this into an Interface

VideoCentroidProcessor Exception Handling
The process() method silently catches all exceptions with e.printStackTrace(). This means:

Errors are printed but the calling code doesn't know if processing succeeded
Consider throwing checked exceptions or returning a status code
Add proper logging instead of just printStackTrace

Missing JavaDoc on Video Processing Classes
VideoCentroidProcessor lacks comprehensive documentation. Add:

Method-level JavaDoc explaining the algorithm flow
Parameter descriptions
Exception documentation (even though they're caught)
Performance expectations or limitations

Test Coverage Gaps
VideoCentroidProcessor only has structural tests; consider mocking the video processing with fake frame data
LargestGroupFinder needs comprehensive tie-breaking tests

Input Validation Consistency
DfsBinaryGroupFinder validates extensively (null checks, rectangular checks, value checks)
DistanceImageBinarizer trusts inputs
VideoCentroidProcessor does minimal validation
Standardize validation approach across the codebase

Code Organization
Consider extracting the binarization + group finding pipeline into a reusable service class
The color parsing logic appears in multiple places (Java and Node)—consider a shared utility