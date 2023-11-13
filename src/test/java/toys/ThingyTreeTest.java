package toys;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThingyTreeTest {

	@Test
	public void test() {
		ThingyTree<Integer> tree = new ThingyTree<>();
		tree.add(new Thingy<Integer>(5));
		tree.add(new Thingy<Integer>(3));
		tree.add(new Thingy<Integer>(7));
		tree.add(new Thingy<Integer>(2));
		tree.add(new Thingy<Integer>(4));
		tree.add(new Thingy<Integer>(1));
		tree.dump(System.out);
		tree.remove(new Thingy<Integer>(3));
		System.out.println();
		tree.dump(System.out);
		System.out.println();
	}

	@Test
	public void testListLikeInsertion() {
		ThingyTree<Integer> tree = new ThingyTree<>();
		tree.add(new Thingy<Integer>(1));
		tree.add(new Thingy<Integer>(2));
		tree.add(new Thingy<Integer>(3));
		tree.add(new Thingy<Integer>(4));
		tree.add(new Thingy<Integer>(5));
		tree.add(new Thingy<Integer>(6));
		tree.add(new Thingy<Integer>(7));
		tree.add(new Thingy<Integer>(8));
		tree.add(new Thingy<Integer>(9));
		tree.dump(System.out);
		System.out.println();
		
	}

	@Test
	public void testAnotherListLikeInsertion() {
		ThingyTree<Integer> tree = new ThingyTree<>();
		tree.add(new Thingy<Integer>(9));
		tree.add(new Thingy<Integer>(8));
		tree.add(new Thingy<Integer>(7));
		tree.add(new Thingy<Integer>(6));
		tree.add(new Thingy<Integer>(5));
		tree.add(new Thingy<Integer>(4));
		tree.add(new Thingy<Integer>(3));
		tree.add(new Thingy<Integer>(2));
		tree.add(new Thingy<Integer>(1));
		tree.dump(System.out);
		System.out.println();
	}

	@Test
	public void testTreeFlattening() {
		ThingyTree<Integer> tree = new ThingyTree<>();
		tree.add(new Thingy<Integer>(9));
		tree.add(new Thingy<Integer>(8));
		tree.add(new Thingy<Integer>(7));
		tree.add(new Thingy<Integer>(6));
		tree.add(new Thingy<Integer>(5));
		tree.add(new Thingy<Integer>(4));
		tree.add(new Thingy<Integer>(3));
		tree.add(new Thingy<Integer>(2));
		tree.add(new Thingy<Integer>(1));
		List<Thingy<Integer>> list = tree.flatten();
		Assert.assertEquals(9, list.size());
		Assert.assertEquals(6, list.get(0).getThingy().intValue());
		Assert.assertEquals(4, list.get(1).getThingy().intValue());
		Assert.assertEquals(2, list.get(2).getThingy().intValue());
		Assert.assertEquals(1, list.get(3).getThingy().intValue());
		Assert.assertEquals(3, list.get(4).getThingy().intValue());
		Assert.assertEquals(5, list.get(5).getThingy().intValue());
		Assert.assertEquals(8, list.get(6).getThingy().intValue());
		Assert.assertEquals(7, list.get(7).getThingy().intValue());
		Assert.assertEquals(9, list.get(8).getThingy().intValue());
	}

	@Test
	public void testIterator() {
		ThingyTree<Integer> tree = new ThingyTree<>();
		tree.add(new Thingy<Integer>(1));
		tree.add(new Thingy<Integer>(2));
		tree.add(new Thingy<Integer>(3));
		tree.add(new Thingy<Integer>(4));
		tree.add(new Thingy<Integer>(5));
		tree.add(new Thingy<Integer>(6));
		tree.add(new Thingy<Integer>(7));
		tree.add(new Thingy<Integer>(8));
		tree.add(new Thingy<Integer>(9));
		// LHR Iterator
		Iterator<Integer> itt = tree.iterator();
		List<Integer> list = new ArrayList<>();
		while(itt.hasNext()) {
			list.add(itt.next());
		}
		Assert.assertEquals(9, list.size());
		Assert.assertEquals(1, list.get(0).intValue());
		Assert.assertEquals(3, list.get(1).intValue());
		Assert.assertEquals(2, list.get(2).intValue());
		Assert.assertEquals(5, list.get(3).intValue());
		Assert.assertEquals(7, list.get(4).intValue());
		Assert.assertEquals(9, list.get(5).intValue());
		Assert.assertEquals(8, list.get(6).intValue());
		Assert.assertEquals(6, list.get(7).intValue());
		Assert.assertEquals(4, list.get(8).intValue());
	}

	@Test
    public void testSerialization() {
        ThingyTree<ExtInteger> tree = new ThingyTree<>();
        tree.add(new Thingy<ExtInteger>(new ExtInteger(1)));
        tree.add(new Thingy<ExtInteger>(new ExtInteger(2)));
        tree.add(new Thingy<ExtInteger>(new ExtInteger(3)));
        tree.add(new Thingy<ExtInteger>(new ExtInteger(4)));
        tree.add(new Thingy<ExtInteger>(new ExtInteger(5)));
        tree.add(new Thingy<ExtInteger>(new ExtInteger(6)));
        tree.add(new Thingy<ExtInteger>(new ExtInteger(7)));
        tree.add(new Thingy<ExtInteger>(new ExtInteger(8)));
        tree.add(new Thingy<ExtInteger>(new ExtInteger(9)));
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
            tree.writeExternal(objectOutput);
            objectOutput.flush();
            ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
            ThingyTree<ExtInteger> deserializedTree = new ThingyTree<>();
            deserializedTree.readExternal(objectInput);
            List<Thingy<ExtInteger>> list = deserializedTree.lhrFlatten();
            Assert.assertEquals(9, list.size());
            Assert.assertEquals(1, list.get(0).getThingy().intValue());
            Assert.assertEquals(3, list.get(1).getThingy().intValue());
            Assert.assertEquals(2, list.get(2).getThingy().intValue());
            Assert.assertEquals(5, list.get(3).getThingy().intValue());
            Assert.assertEquals(7, list.get(4).getThingy().intValue());
            Assert.assertEquals(9, list.get(5).getThingy().intValue());
            Assert.assertEquals(8, list.get(6).getThingy().intValue());
            Assert.assertEquals(6, list.get(7).getThingy().intValue());
            Assert.assertEquals(4, list.get(8).getThingy().intValue());
        }
        catch (IOException | ClassNotFoundException e) {
            Assert.fail("Failed to serialize/deserialize: " + e.getMessage());
        }
    }
}
