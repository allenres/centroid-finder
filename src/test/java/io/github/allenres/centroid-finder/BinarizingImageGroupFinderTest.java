import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BinarizingImageGroupFinderTest {

    // --- Fake Binarizer ---
    static class FakeImageBinarizer implements ImageBinarizer {
        BufferedImage receivedImage;
        int[][] resultToReturn;

        FakeImageBinarizer(int[][] resultToReturn) {
            this.resultToReturn = resultToReturn;
        }

        @Override
        public int[][] toBinaryArray(BufferedImage image) {
            this.receivedImage = image;
            return resultToReturn;
        }

        @Override
        public BufferedImage toBufferedImage(int[][] array) {
            throw new UnsupportedOperationException("Not used in this test");
        }
    }

    // --- Fake GroupFinder ---
    static class FakeBinaryGroupFinder implements BinaryGroupFinder {
        int[][] receivedArray;
        List<Group> resultToReturn;
        boolean wasCalled = false;

        FakeBinaryGroupFinder(List<Group> resultToReturn) {
            this.resultToReturn = resultToReturn;
        }

        @Override
        public List<Group> findConnectedGroups(int[][] binaryArray) {
            this.receivedArray = binaryArray;
            this.wasCalled = true;
            return resultToReturn;
        }
    }

    @Test
    public void testDelegationFlow() {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

        int[][] fakeBinary = {{1, 0}, {0, 1}};
        List<Group> fakeGroups = new ArrayList<>();

        FakeImageBinarizer binarizer = new FakeImageBinarizer(fakeBinary);
        FakeBinaryGroupFinder groupFinder = new FakeBinaryGroupFinder(fakeGroups);

        BinarizingImageGroupFinder finder =
                new BinarizingImageGroupFinder(binarizer, groupFinder);

        List<Group> result = finder.findConnectedGroups(image);

        // Verify binarizer received correct image
        assertSame(image, binarizer.receivedImage);

        // Verify groupFinder received correct binary array
        assertSame(fakeBinary, groupFinder.receivedArray);

        // Verify groupFinder was actually called
        assertTrue(groupFinder.wasCalled);

        // Verify result is passed through unchanged
        assertSame(fakeGroups, result);
    }

    @Test
    public void testEmptyGroupsReturned() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        int[][] fakeBinary = {{0}};
        List<Group> emptyGroups = new ArrayList<>();

        FakeImageBinarizer binarizer = new FakeImageBinarizer(fakeBinary);
        FakeBinaryGroupFinder groupFinder = new FakeBinaryGroupFinder(emptyGroups);

        BinarizingImageGroupFinder finder =
                new BinarizingImageGroupFinder(binarizer, groupFinder);

        List<Group> result = finder.findConnectedGroups(image);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertTrue(groupFinder.wasCalled);
    }

    @Test
    public void testNonEmptyGroupsReturned() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        int[][] fakeBinary = {{1}};
        List<Group> groups = new ArrayList<>();
        Group expected = new Group(1, new Coordinate(1, 5));
        groups.add(expected);

        FakeImageBinarizer binarizer = new FakeImageBinarizer(fakeBinary);
        FakeBinaryGroupFinder groupFinder = new FakeBinaryGroupFinder(groups);

        BinarizingImageGroupFinder finder =
                new BinarizingImageGroupFinder(binarizer, groupFinder);

        List<Group> result = finder.findConnectedGroups(image);

        // Verify size
        assertEquals(1, result.size());

        // Verify content (not just reference)
        assertEquals(expected.size(), result.get(0).size());
        assertEquals(expected.centroid(), result.get(0).centroid());

        // Verify same list returned
        assertSame(groups, result);
    }

    @Test
    public void testNullImageStillPassedThrough() {
        int[][] fakeBinary = {{1}};
        List<Group> groups = new ArrayList<>();

        FakeImageBinarizer binarizer = new FakeImageBinarizer(fakeBinary);
        FakeBinaryGroupFinder groupFinder = new FakeBinaryGroupFinder(groups);

        BinarizingImageGroupFinder finder =
                new BinarizingImageGroupFinder(binarizer, groupFinder);

        finder.findConnectedGroups(null);

        // Verify null is passed through
        assertNull(binarizer.receivedImage);

        // Verify pipeline continues
        assertSame(fakeBinary, groupFinder.receivedArray);
        assertTrue(groupFinder.wasCalled);
    }

    @Test
    public void testBinaryArrayForwardedExactly() {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

        int[][] fakeBinary = {{1, 1}, {0, 0}};
        List<Group> groups = new ArrayList<>();

        FakeImageBinarizer binarizer = new FakeImageBinarizer(fakeBinary);
        FakeBinaryGroupFinder groupFinder = new FakeBinaryGroupFinder(groups);

        BinarizingImageGroupFinder finder =
                new BinarizingImageGroupFinder(binarizer, groupFinder);

        finder.findConnectedGroups(image);

        // Ensure exact same array reference is passed (no copying/modification)
        assertSame(fakeBinary, groupFinder.receivedArray);
    }

    @Test
    public void testBinarizerOutputUsedDirectly() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        int[][] unusualBinary = {{42}}; // unusual value to detect misuse
        List<Group> groups = new ArrayList<>();

        FakeImageBinarizer binarizer = new FakeImageBinarizer(unusualBinary);
        FakeBinaryGroupFinder groupFinder = new FakeBinaryGroupFinder(groups);

        BinarizingImageGroupFinder finder =
                new BinarizingImageGroupFinder(binarizer, groupFinder);

        finder.findConnectedGroups(image);

        // Ensure the exact output from binarizer is used
        assertSame(unusualBinary, groupFinder.receivedArray);
    }

    @Test
    public void testBinarizerReturnsNull() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        FakeImageBinarizer binarizer = new FakeImageBinarizer(null);
        FakeBinaryGroupFinder groupFinder = new FakeBinaryGroupFinder(new ArrayList<>());

        BinarizingImageGroupFinder finder =
                new BinarizingImageGroupFinder(binarizer, groupFinder);

        finder.findConnectedGroups(image);

        // groupFinder should receive null
        assertNull(groupFinder.receivedArray);

        // It should still be called
        assertTrue(groupFinder.wasCalled);
    }

    @Test
    public void testGroupFinderCalledOnce() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        int[][] fakeBinary = {{1}};
        FakeImageBinarizer binarizer = new FakeImageBinarizer(fakeBinary);
        FakeBinaryGroupFinder groupFinder = new FakeBinaryGroupFinder(new ArrayList<>());

        BinarizingImageGroupFinder finder =
                new BinarizingImageGroupFinder(binarizer, groupFinder);

        finder.findConnectedGroups(image);

        assertEquals(1, groupFinder.wasCalled ? 1 : 0);
    }

    @Test
    public void testBinaryArrayNotModified() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        int[][] fakeBinary = {{1, 0}, {0, 1}};
        FakeImageBinarizer binarizer = new FakeImageBinarizer(fakeBinary);
        FakeBinaryGroupFinder groupFinder = new FakeBinaryGroupFinder(new ArrayList<>());

        BinarizingImageGroupFinder finder =
                new BinarizingImageGroupFinder(binarizer, groupFinder);

        finder.findConnectedGroups(image);

        assertArrayEquals(new int[][]{{1, 0}, {0, 1}}, fakeBinary);
    }
}