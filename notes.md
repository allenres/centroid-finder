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
 <br>

------------------------
<br>

# Other notes ( Brendan )

 ImageSummaryApp.java 
 - 3 inputs (image, hex string, Integer for threshold)
 - Euclidean color distance pixel marked white if < threshold otherwise black
 - converts binary array to buffered image returns a binarzied image to disk
 - finds connected group white pixels, bfs could be useful here
 - writes to csv file "groups.csv" format "size,x,y"

<br>

 ```` Interfaces ````

ImageGroupFinder
  - finds conneced groups in a image
  - returns a List of Groups in desc order
  - groups are sorted in desc order accrouding to groups compareTo method
  - ``` List<Group> findConnectedGroups(BufferedImage image)```

 ImageBinarizer
 - interface used to convert between RGB imgs and black and white imgs
 - A binary image is represented by a 2D array of integers, each pixel is black (0) or white (1)
 two methods:
 ```public int[][] toBinaryArray(BufferedImage image); ``` 
 - coverts image to 2D array of 0 or 1
 - takes in a BufferedImage in its parameter
 ``` public BufferedImage toBufferedImage(int[][] image); ```
 - takes in a binary 2D array converts black and white pixels, black represented as x000000 white xFFFFFF

BinaryGroupFinder
  -refer to java doc
  - input 2d array 0s, 1s,
  - pixels horizontally/vertically up down left right
  - The group's size is the number of pixels in the group
  - sum of (x or y) / number of pixels of the group
  - desc order accoruding to compareTo method based of the record (Group)

 ColorDistanceFinder
- color distance between two 24-bit integers in format 0xRRGGBB each pair of hexadecimals correspond to the rgb of color
- ``` public double distance(int colorA, int colorB); ```
- 24-bit hex RGB integer: params colorA, colorB
- return a double

 ```` Classes Implementing an interface  ````
BinarizingImageGroupFinder (implements ImageGroupFinder)
-  ``` public List<Group> findConnectedGroups(BufferedImage image) ```
- uses binarizer to convert BufferedImages into binary array
- then uses BinaryGroupFinder to loacte connected groups in desc order (refer to group record)
- return ```` List<Group> ```` 
DistanceImageBinarizer (implements ImageBinarizer) 
- uses ColorDistanceFinder distanceFinder, int threshold, int targetColor
- imlements two methods
- toBinaryArray, toBufferedImage
- essentially methods to convert back to black and white.

EuclideanColorDistance (implements ColorDistanceFinder)
- refer to the java doc
- ``` public double distance(int colorA, int colorB) ``` 
- Formula: sqrt((r1 - r2)^2 + (g1 - g2)^2 + (b1 - b2)^2)
- return a double

 DfsBinaryGroupFinder (implements BinaryGroupFinder)
 - method to implement ``` public List<Group> findConnectedGroups(int[][] image) ```
 - refer to the interface BinaryGroupFinder or javadoc

 ```` records ````
 Coordinate.java
  - represents the locaitons in an image or array
  - x = row, y = column
  - up (-1, 0)
  - down (1, 0)
  - left (0, -1)
  - right (0, 1)

Group.java
- The top-left cell of the array (row:0, column:0) is considered to be coordinate (x:0, y:0).
- Y increases downward and X increases to the right. For 
- example, (row:4, column:7)
corresponds to (x:7, y:4).

{0,0,0,0,0,0,0,0}
{0,0,0,0,0,0,0,0}
{0,0,0,0,0,0,0,0}
{0,0,0,0,0,0,0,0}
{0,0,0,0,0,0,0,X}
{0,0,0,0,0,0,0,0}
{0,0,0,0,0,0,0,0}
{0,0,0,0,X,0,0,0}

- centroid is the average of pixel cords in each dimension using integer division

- sum of (x or y) / number of pixels of the group
- contains compareTo method
- toCsv
- refer to docs for this on the Group Record.