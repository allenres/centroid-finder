import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
        int col = image[0].length;
        checkRectangular(image);
        boolean[][] visited = new boolean[row][col];
        List<Group> totalGroups = new ArrayList<>();
        // row 3 col 4
        for(int r = 0; r < row; r++) {
            for(int c = 0; c < col; c++) {
                if(image[r][c] != 1 && image[r][c] != 0) throw new IllegalArgumentException();
                if(image[r][c] == 1 && !visited[r][c]) {
                    List<Coordinate> result = new ArrayList<>();
                    int sumX = 0;
                    int sumY = 0;
                    dfs(image, r, c, visited, result);
                    for(Coordinate crd : result) {
                        sumX += crd.x(); // 0 + 0 | 1 + 2 + 2 + 2 + 2  sum = 9
                        sumY += crd.y(); // 0 + 1 | 3 + 3 + 2 + 1 + 0 sum 9
                    }
                    Coordinate centroid = new Coordinate(sumX / result.size(), sumY / result.size()); // result: sumX = 1,  sumY = 1
                    Group g = new Group(result.size(), centroid);
                    totalGroups.add(g);
                }
            }
        }
        
        //  int[][] image = {
        //         {1, 1, 0, 0},
        //         {0, 0, 0, 1},
        //         {1, 1, 1, 1}
        // };

        return sortDesc(totalGroups);
        // [Group[size=5, centroid=Coordinate[x=1, y=1]], Group[size=2, centroid=Coordinate[x=0, y=0]]]
    }

        /*
            if group is less than or greater return otherwise if theyre equal
            compare their x if it is less than or greater return otherwise if these are equal
            compare their y return whether it is less or greater than or equal

            we need o(n^2) time complexity
            group(0) to group(1) we want this comparison

            this is is reliant on the fact that we use collections.sort() in the beginning 
            
            inner for loop
            check(this.compareTo(other)) > < = (1) (-1) (0) the other group 

                if our current is greater than our current[0]
                current  =  list.get(0)


                [1, 2, 3, 4, 5, 6, 7]

                1.compare(2) returns -1 swap

                [2, 1, 3, 4, 5, 6, 7]

                1.compare(1) continue

                [2, 1, 3, 4, 5, 6, 7]

                1.compare(3) returns -1 swap

                [2, 3, 1, 4, 5, 6, 7]

                ...
                [2, 3, 4, 5, 6, 7, 1]
                ...
                [3, 4, 5, 6, 7, 2, 1]
                ...
                [4, 5, 6, 7, 3, 2, 1]
                ...
                [5, 6, 7, 4, 3, 2, 1]
                ...
                [5, 6, 7, 4, 3, 2, 1]
                ...
                [6, 7, 5, 4, 3, 2, 1]
                ...
                [7, 6, 5, 4, 3, 2, 1]

        */
    
    public static List<Group> sortDesc(List<Group> group){
        Collections.sort(group);

        if (group.isEmpty()) return new ArrayList<>();
    
        for(int i = 0; i < group.size(); i++){
           
            Group current = group.get(0);
           
            for(int j = 0; j < group.size() - 1; j++){
                Group next = group.get(j + 1);
                int comp = current.compareTo(next);
            
                if(comp < 0){
                    group.set(j, next);
                    group.set(j + 1, current);
                }
            }
        }


        return group;
    }

    // rSize 3 cSize 4  newRow = 2 newCol = 0  result = [[1,3], [2, 3], [2, 2], [2, 1], [2, 0]]
    // movements down (+1, 0)
    public void dfs(int[][] image, int newRow, int newCol, boolean[][] visited, List<Coordinate> result) {
        if(newRow < 0 || newCol < 0 || newRow >= image.length || newCol >= image[0].length || image[newRow][newCol] == 0 || visited[newRow][newCol]) return;

        visited[newRow][newCol] = true;
        Coordinate current = new Coordinate(newCol, newRow);
        result.add(current);

        for(int[] move : moves) {
            dfs(image, newRow + move[0], newCol + move[1], visited, result);
        }
    }

    private void checkRectangular(int[][] image) {
        int col = image[0].length;
        for (int i = 0; i < image.length; i++) {
            if (image[i].length != col) {
                throw new IllegalArgumentException("Cannot be non-rectangular array");
            }
        }
    }
    
    public static void main(String[] args) {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        // int[][] image = {
        //         {1, 1, 0, 0},
        //         {0, 0, 0, 1},
        //         {1, 1, 1, 1}
        // };

        // int[][] image = {
        //         {1, 0, 1, 1},
        //         {0, 0, 0, 1},
        //         {1, 0, 0, 0}
        // };

        int[][] image = {
                {1, 0, 1},
                {0, 0, 0},
                {1, 0, 1}
        };
        // System.out.println(finder.findConnectedGroups(image));

        // sortDesc(finder.findConnectedGroups(image));
        ;

        System.out.println(finder.findConnectedGroups(image));
        // System.out.println(sortDesc(finder.findConnectedGroups(image)));
    }
}
