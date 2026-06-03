package io.github.allenres.centroidFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds the largest group from a list of connected pixel groups.
 *
 * The "largest" group is determined by the size of the group, where size
 * represents the number of pixels contained in that group.
 *
 * If multiple groups have the same maximum size, the last encountered group
 * in the list is selected due to the use of a greater-than-or-equal comparison.
 *
 * If the input list is empty, a default Group is returned with size equal to
 * 0 and a centroid of (-1, -1), indicating that no valid group was found.
 *
 * @param groups a list of connected pixel groups to evaluate
 * @return the largest Group based on size, or a default Group if the list is empty
 */
public class LargestGroupFinder {
    public Group findLargest(List<Group> groups) {
        if (groups.isEmpty()) return new Group(groups.size(), new Coordinate(-1, -1));
        Group largest = groups.get(0);
        for(Group g : groups) {
            if(g.size() >= largest.size()) {
                largest = g;
            }
        }
        return largest;
    }
}
