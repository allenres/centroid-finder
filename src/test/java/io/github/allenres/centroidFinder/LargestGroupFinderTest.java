package io.github.allenres.centroidFinder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class LargestGroupFinderTest {

    private final LargestGroupFinder largestGroupFinder = new LargestGroupFinder();

    @Test
    void testEmptyListNull() {
        List<Group> list = new ArrayList<>();
        assertEquals(null, largestGroupFinder.findLargest(list));
    }

    @Test
    void testLargestSingleGroup() {
        List<Group> list = new ArrayList<>();
        Group grp1 = new Group(10, new Coordinate(1, 3));
        list.add(grp1);
        assertEquals(grp1, largestGroupFinder.findLargest(list));
    }


}