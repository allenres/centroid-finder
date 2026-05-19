package io.github.allenres.centroidFinder;

import java.util.ArrayList;
import java.util.List;

/*
    Return the largest group.
    Return null if empty.
*/
public class LargestGroupFinder {
    public Group findLargest(List<Group> groups) {
        if (groups.isEmpty()) return null;
        Group largest = groups.get(0);
        for(Group g : groups) {
            if(g.size() >= largest.size()) {
                largest = g;
            }
        }
        return largest;
    }
}
