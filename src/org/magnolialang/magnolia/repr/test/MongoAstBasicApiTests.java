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

public class MongoAstBasicApiTests {


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

}
