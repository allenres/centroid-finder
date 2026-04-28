import java.util.List;

public class DfsBinaryGroupFinder implements BinaryGroupFinder {
    public static int[][] moves = {
        {-1, 0},
        {1, 0}, 
        {0, 1},
        {0, -1}
    };
   /**
    * Finds connected pixel groups of 1s in an integer array representing a binary image.
    * 
    * The input is a non-empty rectangular 2D array containing only 1s and 0s.
    * If the array or any of its subarrays are null, a NullPointerException
    * is thrown. If the array is otherwise invalid, an IllegalArgumentException
    * is thrown.
    *
    * Pixels are considered connected vertically and horizontally, NOT diagonally.
    * The top-left cell of the array (row:0, column:0) is considered to be coordinate
    * (x:0, y:0). Y increases downward and X increases to the right. For example,
    * (row:4, column:7) corresponds to (x:7, y:4).
    *
    * The method returns a list of sorted groups. The group's size is the number 
    * of pixels in the group. The centroid of the group
    * is computed as the average of each of the pixel locations across each dimension.
    * For example, the x coordinate of the centroid is the sum of all the x
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * Similarly, the y coordinate of the centroid is the sum of all the y
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * The division should be done as INTEGER DIVISION.
    *
    * The groups are sorted in DESCENDING order according to Group's compareTo method.
    * 
    * @param image a rectangular 2D array containing only 1s and 0s
    * @return the found groups of connected pixels in descending order
    */
    @Override
    public List<Group> findConnectedGroups(int[][] image) {
        int row = image.length;
        int col = image[row].length;
        boolean[][] visited = new boolean[row][col];

        for(int r = 0; r < row; r++) {
            for(int c = 0; c < col; c++) {
                if(image[r][c] == 1) {
                    dfs(image, r, c, visited);
                }
            }
        }
        return null;
    }
    public void dfs(int[][] image, int newRow, int newCol, boolean[][] visited) {
        if(newRow < 0 || newCol < 0 || newRow > image.length || newCol > image[0].length || image[newRow][newCol] == 0) return;

        visited[newRow][newCol] = true;

        for(int[] move : moves) {
            dfs(image, newRow + move[0], newCol + move[1], visited);
        }
    }
    
}
