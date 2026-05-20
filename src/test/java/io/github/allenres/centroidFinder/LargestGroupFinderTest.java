package io.github.allenres.centroidFinder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class LargestGroupFinderTest {

    private final LargestGroupFinder largestGroupFinder = new LargestGroupFinder();

    // @Test
    // void testEmptyListNull() {
    //     List<Group> list = new ArrayList<>();
    //     assertEquals(null, largestGroupFinder.findLargest(list));
    // }

    @Test
    void testLargestSingleGroup() {
        List<Group> list = new ArrayList<>();
        Group grp1 = new Group(10, new Coordinate(1, 3));
        list.add(grp1);
        assertEquals(grp1, largestGroupFinder.findLargest(list));
    }

    @Test
    void testLargestMultipleGroups() {
        List<Group> list = new ArrayList<>();
        Group grp1 = new Group(4, new Coordinate(1, 3));
        Group grp2 = new Group(1, new Coordinate(4, 3));
        Group grp3 = new Group(10, new Coordinate(1, 5));
        list.add(grp1);
        list.add(grp2);
        list.add(grp3);
        assertEquals(grp3, largestGroupFinder.findLargest(list));
    }

    @Test
    void testLargestMultipleGroupsTied() {
        List<Group> list = new ArrayList<>();
        Group grp1 = new Group(10, new Coordinate(1, 3));
        Group grp2 = new Group(1, new Coordinate(4, 3));
        Group grp3 = new Group(10, new Coordinate(1, 5));
        list.add(grp1);
        list.add(grp2);
        list.add(grp3);
        assertEquals(grp3, largestGroupFinder.findLargest(list));
    }
    
    @Test
    void testLargestMultipleGroupsTiedSame() {
        List<Group> list = new ArrayList<>();
        Group grp1 = new Group(10, new Coordinate(1, 3));
        Group grp2 = new Group(1, new Coordinate(4, 3));
        Group grp3 = new Group(10, new Coordinate(1, 5));
        Group grp4 = new Group(10, new Coordinate(1, 5));
        list.add(grp1);
        list.add(grp2);
        list.add(grp3);
        list.add(grp4);
        assertEquals(grp3, largestGroupFinder.findLargest(list));
    }

    @Test
    void testLargestMultipleGroupsVeryLargeGroup() {
        List<Group> list = new ArrayList<>();
        Group grp1 = new Group(100000, new Coordinate(1, 3));
        Group grp2 = new Group(1, new Coordinate(4, 3));
        Group grp3 = new Group(10, new Coordinate(1, 5));
        Group grp4 = new Group(10, new Coordinate(1, 5));
        list.add(grp1);
        list.add(grp2);
        list.add(grp3);
        list.add(grp4);
        assertEquals(grp1, largestGroupFinder.findLargest(list));
    }

}