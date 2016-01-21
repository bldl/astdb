package org.magnolialang.magnolia.repr.test;

import org.junit.Test;
import org.magnolialang.magnolia.repr.Node;

public class NodeTests {


	@Test
	public void testDifferentNodesHaveDifferentIdentities() {
		Node right = new Node("right");
		Node left = new Node("left");

		assert (right.getIDENTITY().equals(right.getIDENTITY())) : "Identity should be reflexive";
		assert (!right.getIDENTITY().equals(left.getIDENTITY())) : "left and right should have different identities";
	}


	@Test
	public void testGetSetParent() {
		Node a = new Node("a");
		Node b = new Node("b", a.getIDENTITY()); // sets a to be the parent of b;
		Node c = new Node("c");

		assert (b.getParent().equals(a.getIDENTITY())) : "parent not set correctly in constructor";
		assert (c.getParent() == null) : "getParent should be 0 until a parent is set";

		b.setParent(c);
		assert (b.getParent().equals(c.getIDENTITY())) : "parent not updated correctly in setParent";
	}


	@Test
	public void testNodeName() {
		Node node = new Node("name");
		assert (node.getName().equals("name"));
	}

}
