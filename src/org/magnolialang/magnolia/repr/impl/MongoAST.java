package org.magnolialang.magnolia.repr.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

import org.magnolialang.magnolia.repr.ASTCursor;
import org.magnolialang.magnolia.repr.Ast;
import org.magnolialang.magnolia.repr.Entry;
import org.magnolialang.magnolia.repr.EntryMap;
import org.magnolialang.magnolia.repr.Identity;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.Kind;
import org.magnolialang.magnolia.repr.Node;
import org.magnolialang.magnolia.repr.impl.MongoIdentityDomain.IdentityDomain;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * - sjekk om effektivt, < dump et tre inn og sjekk
 * --- er dette mer effektivt enn vanlig måter å lagre trær på eller ikke?
 * --- eksperimenter med alternativer?
 * 
 */

public class MongoAST implements Ast {

	protected static final Key<?> DATAKEY = new Key<Object>() {
	};
	protected static final Key<String> NAMEKEY = new Key<String>() {
	};

	protected static final Key<Kind> KINDKEY = new Key<Kind>() {
	};
	DBCollection nodes, entries, identities;
	final String NODE_KEY, ENTRY_KEY, AST_NAME, DOMAIN_KEY;

	protected IdentityDomain domain;


	public MongoAST(String astName) {
		AST_NAME = astName;
		NODE_KEY = astName + "graph";
		ENTRY_KEY = astName + "entry";
		DOMAIN_KEY = astName + "domain";

		DB db = DatabaseFactory.getDb();
		nodes = db.getCollection(NODE_KEY);
		entries = db.getCollection(ENTRY_KEY);
		identities = db.getCollection(DOMAIN_KEY);
		domain = MongoIdentityDomain.getDomainInstance(DOMAIN_KEY);
	}


	@Override
	public void clearAst() {
		nodes.drop();
		entries.drop();
		identities.drop();
	}


	@Override
	public long countEntries() {
		return entries.count();
	}


	@Override
	public long countNodes() {
		return nodes.count();
	}


	@SuppressWarnings("unchecked")
	private <V extends Serializable> Entry<V> dbEntryToEntry(DBObject dbEntry) {
		Key<V> key = (Key<V>) dbEntry.get("key");
		V value = (V) dbEntry.get("value");
		Identity node_id = domain.toIdentity((int) dbEntry.get("node_id"));

		return new Entry<V>(key, value, node_id);
	}


	private Node dbNodeToNode(DBObject dbNode) {
		return new Node((String) dbNode.get("name"), domain.toIdentity((int) dbNode.get("identity")));
	}


	/**
	 * Deletes all entries corresponding to a Node with Identity "id"
	 * 
	 * @param id
	 *            the Identity to which the Entries belong
	 */
	private void deleteEntries(Identity id) {
		BasicDBObject dbEntry = new BasicDBObject().append("node_id", domain.toInt(id));
		DBCursor dbc = entries.find(dbEntry);

		while(dbc.hasNext()) {
			entries.remove(dbc.next());
		}
	}


	/**
	 * Deletes the Node "dbobject" from the nodes collection, and all its
	 * child-nodes and entries.
	 * 
	 * @param dbobject
	 *            the DBObject which we're deleting, together with all its
	 *            sub-entries and sub-nodes
	 */
	private void deleteNode(DBObject dbobject) {
		int nodeId = (int) dbobject.get("_id");

		//find all children
		BasicDBObject childnodes = new BasicDBObject().append("parent", nodeId);
		DBCursor dbc = nodes.find(childnodes);
		while(dbc.hasNext()) {
			deleteNode(dbc.next()); // and then proceed to delete them
		}

		deleteEntries(domain.toIdentity(nodeId)); // delete entries corresponding to our node
		nodes.remove(dbobject); // before removing our node
	}


	@Override
	public void deleteNode(Identity nodeId) {
		deleteNode(getDBNode(nodeId));
	}


	private <V extends Serializable> BasicDBObject entryToDbEntry(Entry<V> entry) {
		//TODO can duplicates happen? Need to test
		BasicDBObject dbNode = new BasicDBObject();
		dbNode.append("node_id", domain.toInt(entry.getNodeId()));
		dbNode.append("value", entry.getValue());
		dbNode.append("key", entry.getKey().toString());
		return dbNode;
	}


	@Override
	public <V> boolean existsEntry(Identity id, Key<V> key) {
		BasicDBObject dbEntry = new BasicDBObject();
		dbEntry.append("node_id", domain.toInt(id));
		dbEntry.append("key", key);
		DBCursor dbc = entries.find(dbEntry);


		if(dbc == null || !dbc.hasNext()) {
			return false;
		}
		return true;
	}


	@Override
	public boolean existsNode(Identity id) {
		BasicDBObject node = new BasicDBObject();
		node.append("_id", domain.toInt(id));
		DBCursor dbc = nodes.find(node);


		if(dbc == null || !dbc.hasNext()) {
			return false;
		}
		return true;
	}


	@Override
	public ASTCursor getAstCursor(Identity id) {
		return new ASTCursor(this, id);
	}


	@Override
	public String getAstName() {
		return AST_NAME;
	}


	@Override
	public ASTCursor getChild(Identity id, int i) {
		Identity childId = getChildId(id, i);
		return getAstCursor(childId);
	}


	@Override
	public Identity getChildId(Identity id, int i) {
		if(i < 0) {
			throw new IllegalArgumentException("children are numbered from 0..n-1, so child number <" + i + "> is illegal");
		}

		BasicDBObject childnode = new BasicDBObject();
		childnode.append("parent", domain.toInt(id));
		DBCursor dbc = nodes.find(childnode);

		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException();
		}

		if(dbc.count() <= i) {
			throw new IndexOutOfBoundsException("children are numbered from 0..n-1, so child number <" + i + "> is illegal");
		}

		int n = 0;
		for(DBObject obj : dbc) {
			if(n++ == i) {
				return domain.toIdentity((int) obj.get("_id"));
			}
		}
		throw new IndexOutOfBoundsException("something is wrong in getChildId(Identity id, int i)"); //should never happen, I have it as a precaution
	}


	private <V> DBObject getDBEntry(Identity id, Key<V> key) {
		BasicDBObject dbEntry = new BasicDBObject();
		dbEntry.append("_id", domain.toInt(id));
		dbEntry.append("key", key);
		DBCursor dbc = entries.find(dbEntry);


		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException();
		}

		if(dbc.count() > 1) {
			throw new RuntimeException("2 elements matched to same id!");
		}

		return dbc.next();
	}


//
//	@Override
//	public Kind getKind(Identity id) { //What is this supposed to do?
//		//return getEntry(id).get(KINDKEY)
//		return null;
//	}


	protected DBObject getDBNode(Identity id) {
		BasicDBObject node = new BasicDBObject();
		node.append("_id", domain.toInt(id));
		DBCursor dbc = nodes.find(node);

		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException();
		}

		if(dbc.count() > 1) {
			throw new RuntimeException("2 elements matched to same id!");
		}

		return dbc.next();
	}


	@Override
	public <V extends Serializable> Entry<V> getEntry(Identity id, Key<V> key) {
		return dbEntryToEntry(getDBEntry(id, key));
	}


	@Override
	public Node getNode(Identity id) {
		return dbNodeToNode(getDBNode(id));
	}


	@Override
	public int getNumChildren(Identity id) {
		BasicDBObject nodeWithParent = new BasicDBObject();
		nodeWithParent.append("parent", id);
		DBCursor dbc = nodes.find(nodeWithParent);

		if(dbc == null) {
			throw new NoSuchElementException("can't happen - check getNumChildren(Identity id) method");
		}

		return dbc.count();
	}


	@Override
	public Identity getParentId(Identity id) {
		return domain.toIdentity((int) getDBNode(id).get("parent"));
	}


	private List<Integer> getRootInts() {
		BasicDBObject nodeWithParent = new BasicDBObject();
		nodeWithParent.append("parent", -1);
		DBCursor dbc = nodes.find(nodeWithParent);

		List<Integer> roots = new ArrayList<Integer>(dbc.count());
		for(DBObject db : dbc) {
			int id = (int) db.get("_id");
			roots.add(id);
		}
		return roots;
	}


	@Override
	public List<Identity> getRoots() {
		BasicDBObject nodeWithParent = new BasicDBObject();
		nodeWithParent.append("parent", -1);
		DBCursor dbc = nodes.find(nodeWithParent);

		List<Identity> roots = new ArrayList<Identity>(dbc.count());
		for(DBObject db : dbc) {
			int id = (int) db.get("_id");
			roots.add(domain.toIdentity(id));
		}
		return roots;
	}


	@Override
	public boolean isChildOf(Node tree, Node node) {
		// check if the node is equal to the root
		if(node.equals(tree)) {
			return true;
		}

		// check if the node is part of the subtrees
		for(Node child : tree.getChildren()) {
			if(isChildOf(child, node)) {
				return true;
			}
		}

		// the node wasn't part of the tree, so it is not a child of it
		return false;
	}


	@Override
	public boolean isRootNode(Node node) {
		return node.getParent() == null;
	}


	@Override
	public boolean isSafeToSwapParents(Node firstTree, Node secondTree) {
		// it is safe to swap them if either one of them is a rootNode
		if(isRootNode(firstTree) || isRootNode(secondTree)) {
			return true;
		}

		// Checks if either of the trees are a child of the other
		if(isChildOf(firstTree, secondTree) || isChildOf(secondTree, firstTree)) {
			return false;
		}

		return true;
	}


	@Override
	public boolean isTree() {

		Map<Integer, Boolean> visited = new HashMap<>();
		Stack<Integer> stack = new Stack<>();

		stack.addAll(getRootInts());
		while(!stack.isEmpty()) {
			int node = stack.pop();

			// AST is not a tree if we can visit a node twice
			if(visited.containsKey(node)) {
				return false;
			}
			visited.put(node, true);

			// Add all children to stack, so that we visit them later
			DBCursor childNodes = nodes.find(new BasicDBObject("parent", node));
			while(childNodes.hasNext()) {
				DBObject dbNode = childNodes.next();
				stack.push((Integer) dbNode.get("_id"));
			}
		}

		return true;
	}


	private BasicDBObject nodeToDbNode(Node node) {
		BasicDBObject dbNode = new BasicDBObject();

		dbNode.append("_id", domain.toInt(node.getIDENTITY()));
		dbNode.append("name", node.getName());
		dbNode.append("parent", domain.toInt(node.getParent()));

		return dbNode;
	}


	@Override
	public <T extends Serializable> void storeEntry(Entry<T> entry) {
		if(entry.getNodeId() != null) {
			entries.insert(entryToDbEntry(entry));
		}
		else {
			throw new RuntimeException("Entry cannot be stored without being associated to a node");
		}
	}


	@Override
	public void storeNode(Node node) {
		storeNode(node, true);
	}


	@Override
	public void storeNode(Node node, boolean storeEntries) {
		BasicDBObject dbNode = nodeToDbNode(node);
		nodes.insert(dbNode);
		if(storeEntries) {
			EntryMap entries = node.getEntryMap();
			for(Key<? extends Serializable> k : entries.getKeys()) {
				Entry<?> e = entries.getEntry(k);
				storeEntry(e);
			}
		}
	}


	@Override
	public void storeSubtree(Node node) {
		storeSubtreeHelper(node);
	}


	private void storeSubtreeHelper(Node node) {
		storeNode(node);
		for(Node child : node.getChildren()) {
			storeSubtreeHelper(child); //TODO eternal loops may occur
		}
	}


	@Override
	public void swapParents(Node firstTree, Node secondTree) {
		Identity firstParent = firstTree.getParent();
		Identity secondParent = secondTree.getParent();

		// update the in-memory trees
		firstTree.setParent(secondParent);
		secondTree.setParent(firstParent);

		// update the db trees
		BasicDBObject firstDbNode = new BasicDBObject().append("_id", domain.toInt(firstTree.getIDENTITY()));
		BasicDBObject firstTreeDbChange = new BasicDBObject().append("parent", domain.toInt(secondParent));
		nodes.update(firstDbNode, firstTreeDbChange);

		BasicDBObject secondDbNode = new BasicDBObject().append("_id", domain.toInt(secondTree.getIDENTITY()));
		BasicDBObject secondTreeDbChange = new BasicDBObject().append("parent", domain.toInt(firstParent));
		nodes.update(secondDbNode, secondTreeDbChange);


	}
}
