````MAIN````
ImageSummaryApp
 - it accepts 3 args (image path, target hex color, threshold)
 - hex color is parsed into a 24 bit integer
 - binarize: Euclidean distance to the target color eacch pixel (white if its distance is less than threshold, else mark black)
 - find islands of white pixels in the binary image
    - compute the size and the centroid
 - write to a csv with one row per group in format "size, x, y"

```IMPLEMENTATIONS```
DistanceImageBinarizer
- implements ImageBinarizer
- uses color distance to determine whether each pixel should be black or white
- distance between pixel color and target color if (< threshold, pixel is white)

BinaryGroupFinder
- finds islands of 1s (white) in a 2d array
- retruns a list of sorted 
- size = number of pixels
- centroid (x) = sum of all x coordinates of the pixels / number of pixels 
- centroid (y) = same as above, but y coordinates
- groups are sorted in descending order

BinarizingImageGroupFinder
- implements ImageGroupFinder
- binarizes a given image, then finds connected groups of white pixels using BinaryGroupFinder
- uses ImageBinarizes to convert an RGB image into a binary 2d array
- BinaryGroupFinder is applied to the binary array to find the connected groups of white pixels

EuclideanColorDistance
 - implements colordistance finder
 - colors are represented as 24-bit integer
 - calculation: sqrt((r1 - r2)^2 + (g1 - g2)^2 + (b1 - b2)^2)

```` INTERFACE ````
ImageGroupFinder
 - interface

ImageBinarizer
- interface
- method for converting buffedimage where 1 = white and 0 = black, returns 2d array
- method for converting 2d array into a bufferedimage, where 0 = x000000 and 1 = xFFFFFF, returns BufferedImage

ColorDistanceFinder
 - interface for computing the distance between two colors

````RECORD ````
Group
  - a record
  - contains compareTo function
  - has method to return a string containg for the csv row in the required format

Coordinate
 - a record containing the location (x, y)
 - y increases downard, x increases to the right
 - (row:4, column:7) corresponds to (x:7, y:4)
 - 0, 0 = top left