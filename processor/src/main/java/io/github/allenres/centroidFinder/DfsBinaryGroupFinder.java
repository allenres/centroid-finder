package io.github.allenres.centroidFinder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class DfsBinaryGroupFinder implements BinaryGroupFinder {

    private static final int[][] MOVES = {
            { -1, 0 }, // up
            { 1, 0 }, // down
            { 0, -1 }, // left
            { 0, 1 } // right
    };

    /**
     * Finds connected pixel groups of 1s in an integer array representing a binary
     * image.
     * 
     * The input is a non-empty rectangular 2D array containing only 1s and 0s.
     * If the array or any of its subarrays are null, a NullPointerException
     * is thrown. If the array is otherwise invalid, an IllegalArgumentException
     * is thrown.
     *
     * Pixels are considered connected vertically and horizontally, NOT diagonally.
     * The top-left cell of the array (row:0, column:0) is considered to be
     * coordinate
     * (x:0, y:0). Y increases downward and X increases to the right. For example,
     * (row:4, column:7) corresponds to (x:7, y:4).
     *
     * The method returns a list of sorted groups. The group's size is the number
     * of pixels in the group. The centroid of the group
     * is computed as the average of each of the pixel locations across each
     * dimension.
     * For example, the x coordinate of the centroid is the sum of all the x
     * coordinates of the pixels in the group divided by the number of pixels in
     * that group.
     * Similarly, the y coordinate of the centroid is the sum of all the y
     * coordinates of the pixels in the group divided by the number of pixels in
     * that group.
     * The division should be done as INTEGER DIVISION.
     *
     * The groups are sorted in DESCENDING order according to Group's compareTo
     * method.
     * 
     * @param image a rectangular 2D array containing only 1s and 0s
     * @return the found groups of connected pixels in descending order
     */
    @Override
    public List<Group> findConnectedGroups(int[][] image) {

        validateImage(image);

        int rows = image.length;
        int cols = image[0].length;

        boolean[][] visited = new boolean[rows][cols];
        List<Group> groups = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if(image[r][c] != 1 && image[r][c] != 0) throw new IllegalArgumentException();

                if (image[r][c] == 1 && !visited[r][c]) {

                    Group group = exploreGroup(image, r, c, visited);
                    groups.add(group);
                }
            }
        }

        // descending order
        Collections.sort(groups, Collections.reverseOrder());

        return groups;
    }

    private Group exploreGroup(
            int[][] image,
            int startRow,
            int startCol,
            boolean[][] visited) {

        Deque<Coordinate> stack = new ArrayDeque<>();

        stack.push(new Coordinate(startCol, startRow));

        int size = 0;
        int sumX = 0;
        int sumY = 0;

        while (!stack.isEmpty()) {

            Coordinate current = stack.pop();

            int c = current.x();
            int r = current.y();

            // bounds check
            if (r < 0 || c < 0 ||
                    r >= image.length ||
                    c >= image[0].length) {
                continue;
            }

            // skip invalid cells
            if (visited[r][c] || image[r][c] == 0) {
                continue;
            }

            visited[r][c] = true;

            size++;
            sumX += c;
            sumY += r;

            for (int[] move : MOVES) {
                int nextRow = r + move[0];
                int nextCol = c + move[1];

                stack.push(new Coordinate(nextCol, nextRow));
            }
        }

        Coordinate centroid = new Coordinate(sumX / size, sumY / size);

        return new Group(size, centroid);
    }

    private void validateImage(int[][] image) {

        if (image == null || image.length == 0) {
            throw new NullPointerException("Image cannot be null or empty");
        }

        if (image[0] == null || image[0].length == 0) {
            throw new IllegalArgumentException("Image rows cannot be empty");
        }

        int cols = image[0].length;

        for (int r = 0; r < image.length; r++) {

            if (image[r] == null) {
                throw new NullPointerException("Row cannot be null");
            }

            if (image[r].length != cols) {
                throw new IllegalArgumentException(
                        "Image must be rectangular");
            }

            for (int c = 0; c < cols; c++) {

                if (image[r][c] != 0 && image[r][c] != 1) {
                    throw new IllegalArgumentException(
                            "Image must contain only 0s and 1s");
                }
            }
        }
    }
}