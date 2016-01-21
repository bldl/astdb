package org.magnolialang.magnolia.repr.test;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.magnolialang.magnolia.repr.Entry;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.Node;

public class EntryTests {

	@SuppressWarnings("serial")
	protected static final Key<String> skey = new Key<String>() {
	};

	@SuppressWarnings("serial")
	protected static final Key<Integer> ikey = new Key<Integer>() {
	};


	@Test
	public void testGetKey() {
		Entry<String> es = new Entry<>(skey, "string");
		Entry<Integer> ei = new Entry<>(ikey, 5);

		assert (es.getKey().equals(skey)) : "the key is not stored properly";
		assert (ei.getKey().equals(ikey)) : "the key is not stored properly";
	}


	@Test
	public void testGetSetNode() {
		Node a = new Node("a");
		Node b = new Node("b");


		Entry<String> es = new Entry<>(skey, "string", a);
		assert (es.getNodeId().equals(a.getIDENTITY())) : "Node incorrectly stored";

		try {
			es.setNodeId(b.getIDENTITY());
			fail("assigning a new node to an entry shouldn't be allowed");
		}
		catch(RuntimeException e) {
			// Everything ok, we want it to throw an exception
		}

		Entry<Integer> ei = new Entry<>(ikey, 5);
		assert (ei.getNodeId() == null) : "nodeId should be null if unassigned";

		ei.setNodeId(b.getIDENTITY());
		assert (ei.getNodeId().equals(b.getIDENTITY())) : "Entry didn't get update which node it belongs to";
	}


	@Test
	public void testGetValue() {
		Entry<String> es = new Entry<>(skey, "string");
		Entry<Integer> ei = new Entry<>(ikey, 5);

		assert (es.getValue().equals("string")) : "the value is not stored properly";
		assert (ei.getValue().equals(5)) : "the value is not stored properly";
	}

}
