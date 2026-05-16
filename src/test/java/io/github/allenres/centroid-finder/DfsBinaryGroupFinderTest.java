import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DfsBinaryGroupFinderTest {

    private final DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();

    @Test
    void singlePixelGroup() {
        int[][] image = {
                {0, 0, 0},
                {0, 1, 0},
                {0, 0, 0}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(1, groups.size());
        Group g = groups.get(0);

        assertEquals(1, g.size());
        assertEquals(new Coordinate(1, 1), g.centroid());
    }

    @Test
    void singleLargeGroup() {
        int[][] image = {
                {1, 1},
                {1, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(1, groups.size());
        Group g = groups.get(0);

        assertEquals(4, g.size());
        // centroid: (0+1+0+1)/4 = 0, (0+0+1+1)/4 = 0
        assertEquals(new Coordinate(0, 0), g.centroid());
    }

    @Test
    void multipleSeparateGroups() {
        int[][] image = {
                {1, 0, 1},
                {0, 0, 0},
                {1, 0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(4, groups.size());

        for (Group g : groups) {
            assertEquals(1, g.size());
        }
    }

    @Test
    void diagonalNotConnected() {
        int[][] image = {
                {1, 0},
                {0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(2, groups.size());
    }

    @Test
    void horizontalAndVerticalConnectedOnly() {
        int[][] image = {
                {1, 1, 0},
                {0, 1, 0},
                {0, 0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        // one L-shaped group + one isolated
        assertEquals(2, groups.size());

        Group largest = groups.get(0);
        assertEquals(3, largest.size());
    }

    @Test
    void centroidIntegerDivision() {
        int[][] image = {
                {1, 0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(2, groups.size());

        // each is size 1 → centroid is exact
        assertTrue(groups.stream().anyMatch(g -> g.centroid().equals(new Coordinate(0, 0))));
        assertTrue(groups.stream().anyMatch(g -> g.centroid().equals(new Coordinate(2, 0))));
    }

    @Test
    void centroidRoundingDown() {
        int[][] image = {
                {1, 1, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        Group g = groups.get(0);

        assertEquals(3, g.size());
        // x = (0+1+2)/3 = 1, y = 0
        assertEquals(new Coordinate(1, 0), g.centroid());
    }

    @Test
    void groupsSortedDescending() {
        int[][] image = {
                {1, 1, 0, 0},
                {0, 0, 0, 1},
                {1, 1, 1, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        // sizes should be sorted descending
        for (int i = 0; i < groups.size() - 1; i++) {
            assertTrue(groups.get(i).compareTo(groups.get(i + 1)) >= 0,
                    "Groups are not sorted in descending order");
        }
    }

    @Test
    void unsortedInputStillProducesSortedOutput() {
        int[][] image = {
                {1, 0, 1, 1},
                {0, 0, 0, 1},
                {1, 0, 0, 0}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        // Expect sizes: 3, 1, 1 (descending)
        assertEquals(3, groups.get(0).size());

        for (int i = 1; i < groups.size(); i++) {
            assertTrue(groups.get(i).size() <= groups.get(i - 1).size());
        }
    }

    @Test
    void nullImageThrowsException() {
        assertThrows(NullPointerException.class, () ->
                finder.findConnectedGroups(null)
        );
    }

    @Test
    void nullRowThrowsException() {
        int[][] image = {
                {1, 0},
                null
        };

        assertThrows(NullPointerException.class, () ->
                finder.findConnectedGroups(image)
        );
    }

    @Test
    void nonRectangularArrayThrowsException() {
        int[][] image = {
                {1, 0},
                {1}
        };

        assertThrows(IllegalArgumentException.class, () ->
                finder.findConnectedGroups(image)
        );
    }

    @Test
    void invalidValuesThrowException() {
        int[][] image = {
                {1, 2},
                {0, 1}
        };

        assertThrows(IllegalArgumentException.class, () ->
                finder.findConnectedGroups(image)
        );
    }

    @Test
    void emptyGroupsWhenNoOnes() {
        int[][] image = {
                {0, 0},
                {0, 0}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        assertTrue(groups.isEmpty());
    }

    @Test
    void tieBreakingByCentroidXThenY() {
        int[][] image = {
                {1, 0, 1},
                {0, 0, 0},
                {1, 0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        // all size 1 → sorted by x then y DESC
        for (int i = 0; i < groups.size() - 1; i++) {
            Group a = groups.get(i);
            Group b = groups.get(i + 1);

            assertTrue(a.compareTo(b) >= 0);
        }
    }

    @Test
    void tieBreaking_CenterWithEdgePositions() {
        int[][] image = {
                {1, 0, 0, 0, 1},
                {0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0},
                {1, 0, 0, 0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        for (int i = 0; i < groups.size() - 1; i++) {
            Group a = groups.get(i);
            Group b = groups.get(i + 1);
            assertTrue(a.compareTo(b)>= 0);
        }
    }

    @Test
    void tieBreaking_CornerOfcubeOnes() {
        int[][] image = {
                {1, 1, 0, 1, 1},
                {1, 1, 0, 1, 1},
                {0, 0, 0, 0, 0},
                {1, 1, 0, 1, 1},
                {1, 1, 0, 1, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        for (int i = 0; i < groups.size() - 1; i++) {
            Group a = groups.get(i);
            Group b = groups.get(i + 1);
            assertTrue(a.compareTo(b)>= 0);
        }
    }

    @Test
    void singleIsland_snakeShape() {
        int[][] image = {
                {1, 1, 0, 0},
                {0, 1, 1, 0},
                {0, 0, 1, 1},
                {0, 0, 0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(1, groups.size());
    }

    @Test
    void singleIsland_XShapeSize() {
        int[][] image = {
                {1, 0, 0, 0, 1},
                {0, 1, 0, 1, 0},
                {0, 0, 1, 0, 0},
                {0, 1, 0, 1, 0},
                {1, 0, 0, 0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(9, groups.size());
    }

    @Test
    void singleIsland_XShape() {
        int[][] image = {
                {1, 0, 0, 0, 1},
                {0, 1, 0, 1, 0},
                {0, 0, 1, 0, 0},
                {0, 1, 0, 1, 0},
                {1, 0, 0, 0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        for (int i = 0; i < groups.size() - 1; i++) {
            Group a = groups.get(i);
            Group b = groups.get(i + 1);
            assertTrue(a.compareTo(b)>= 0);
        }
    }

    @Test
    void singleIsland_TriangleShape() {
        int[][] image = {
                {1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1},
                {0, 0, 1, 1, 1},
                {0, 0, 0, 1, 1},
                {1, 0, 0, 0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        for (int i = 0; i < groups.size() - 1; i++) {
            Group a = groups.get(i);
            Group b = groups.get(i + 1);
            assertTrue(a.compareTo(b)>= 0);
        }

        assertEquals(2, groups.size());
    }

    @Test
    void tieBreaking_sameSizeDifferentShapes() {
        int[][] image = {
                {1, 0, 1},
                {1, 0, 1},
                {0, 0, 0},
                {1, 1, 0}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        for (int i = 0; i < groups.size() - 1; i++) {
            Group a = groups.get(i);
            Group b = groups.get(i + 1);

            assertTrue(a.compareTo(b) >= 0);
        }

        assertEquals(3, groups.size());
    }

    @Test
    void tieBreaking_largeSquareSameSizes() {
        int[][] image = {
                {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        for (int i = 0; i < groups.size() - 1; i++) {
            Group a = groups.get(i);
            Group b = groups.get(i + 1);

            assertTrue(a.compareTo(b) >= 0);
        }

        assertEquals(20, groups.size());
    }

    @Test
    void tieBreaking_multipleSingleCellIslands_irregular() {
        int[][] image = {
                {0, 1, 0, 0, 1},
                {0, 0, 0, 0, 0},
                {1, 0, 0, 1, 0},
                {0, 0, 1, 0, 0}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        for (int i = 0; i < groups.size() - 1; i++) {
            Group a = groups.get(i);
            Group b = groups.get(i + 1);
            assertTrue(a.compareTo(b) >= 0);
        }

        assertEquals(5, groups.size());
    }
}