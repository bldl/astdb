package org.magnolialang.magnolia.repr.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.magnolialang.magnolia.repr.Ast;
import org.magnolialang.magnolia.repr.Identity;
import org.magnolialang.magnolia.repr.Node;
import org.magnolialang.magnolia.repr.impl.DatabaseFactory;
import org.magnolialang.magnolia.repr.impl.MongoAST;

import com.mongodb.DB;

public class MongoAstTests {


	private static DB db;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = DatabaseFactory.getDb();
	}


	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DatabaseFactory.close();
	}


	private Ast ast;


	@Before
	public void setUp() throws Exception {
		ast = new MongoAST("testAst");
		if(ast.countEntries() > 0 || ast.countNodes() > 0) {
			ast.clearAst();
		}
	}


	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testDbIsInitialized() {
		assert (db != null) : "DB hasn't been initialized properly";
	}


	@Test
	public void testNodeCount() {
		Node n = new Node("testnode");
		Identity n_id = n.getIDENTITY();
		long nodeCount = ast.countNodes();

		ast.storeNode(n);
		assert (nodeCount + 1 == ast.countNodes()) : "Inserting a node should increase ast.countNodes() by 1";

		ast.deleteNode(n_id);
		assert (nodeCount == ast.countNodes()) : "Deleting a node should decrease ast.countNodes() by 1";
	}


	@Test
	public void testNodeExists() {
		Node n = new Node("testnode");
		Identity n_id = n.getIDENTITY();
		assert (!ast.existsNode(n_id)) : "Node should not exist in ast until it has been inserted";

		ast.storeNode(n);
		assert (ast.existsNode(n_id)) : "Node hasn't been inserted properly";
		ast.deleteNode(n_id);
		assert (!ast.existsNode(n_id)) : "Node should not exist in ast after being deleted";

		ast.storeNode(n);
		assert (ast.existsNode(n_id)) : "Node hasn't been inserted properly";
		ast.clearAst();
		assert (!ast.existsNode(n_id)) : "Node should not exist in ast after we clear it";
	}


	@Test
	public void testNoInitialDbData() {
		assert (ast.countEntries() == 0) : "no entries should exist in a freshly made AST";
		assert (ast.countNodes() == 0) : "no nodes should exist in a freshly made AST";
	}


	@Test
	public void testSwapParents() {
		/*
		 *  Makes a tree that looks like the following
		 * 			
		 * 				 (root)
		 * 				/	   \
		 * 	 	   (left)	  (right)
		 * 		 	/			|	 \
		 * 	 (leftChild) (rightChild)(rightChild2)
		 * 
		 * 
		 *  Calling swapParents(leftChild, rightChild) should give us
		 *  
		 * 				 (root)
		 * 				/	   \
		 * 	 	   (left)	  (right)
		 * 		 	/			|	 \
		 * 	 (rightChild) (leftChild)(rightChild2)
		 * 
		 * 
		 */

		Node leftChild = new Node("leftChild");
		Node left = new Node("left");
		Node rightChild = new Node("rightChild");
		Node rightChild2 = new Node("rightChild2");
		Node right = new Node("right");
		Node root = new Node("root");

		assert (!right.getIDENTITY().equals(left.getIDENTITY())) : "test impossible if right and left identities are equal";

		leftChild.setParent(left);
		rightChild.setParent(right);
		rightChild2.setParent(right);
		left.setParent(root);
		right.setParent(root);

		ast.storeNodes(leftChild, rightChild, rightChild2, left, right, root);

		ast.swapParents(leftChild, rightChild);
		assert (leftChild.getParent() == right.getIDENTITY()) : "leftChild did not swap parents with rightChild";
		assert (rightChild.getParent() == left.getIDENTITY()) : "rightChild did not swap parents with leftChild";
		assert (rightChild2.getParent() == right.getIDENTITY()) : "rightChild2 had its parent changed, which it should not";


		// Swapping parents twice should return the original parents
		ast.swapParents(leftChild, rightChild);
		assert (leftChild.getParent() == left.getIDENTITY()) : "leftChild doesn't have its original parent after 2 swaps";
		assert (rightChild.getParent() == right.getIDENTITY()) : "rightChild doesn't have its original parent after 2 swaps";
		assert (rightChild2.getParent() == right.getIDENTITY()) : "rightChild2 had its parent changed, which it should not";
	}

}
