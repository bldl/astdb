package org.magnolialang.magnolia.repr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.magnolialang.magnolia.repr.ASTCursor;
import org.magnolialang.magnolia.repr.Ast;
import org.magnolialang.magnolia.repr.Entry;
import org.magnolialang.magnolia.repr.EntryMap;
import org.magnolialang.magnolia.repr.Identity;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.Kind;
import org.magnolialang.magnolia.repr.Node;
import org.magnolialang.magnolia.repr.impl.MongoIdentityDomain.Domain;

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

	DBCollection nodes, entries, identities;
	final String NODE_KEY, ENTRY_KEY, AST_NAME, DOMAIN_KEY;

	protected static final Key<?> DATAKEY = new Key<Object>() {
	};
	protected static final Key<String> NAMEKEY = new Key<String>() {
	};
	protected static final Key<Kind> KINDKEY = new Key<Kind>() {
	};

	protected Domain domain;


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
	private <V> Entry<V> dbEntryToEntry(DBObject dbEntry) {
		return new Entry<V>((Key<V>) dbEntry.get("key"), (V) dbEntry.get("value"));
	}


	private Node dbNodeToNode(DBObject dbNode) {
		return new Node((String) dbNode.get("name"), (Identity) dbNode.get("identity"));
	}


	/**
	 * Deletes all entries corresponding to a Node with Identity "id"
	 * 
	 * @param id
	 *            the Identity to which the Entries belong
	 */
	private void deleteEntries(Identity id) {
		BasicDBObject dbEntry = new BasicDBObject().append("identity", id);
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
		Identity nodeId = (Identity) dbobject.get("identity");

		//find all children
		BasicDBObject childnodes = new BasicDBObject().append("parent", nodeId);
		DBCursor dbc = nodes.find(childnodes);
		while(dbc.hasNext()) {
			deleteNode(dbc.next()); // and then proceed to delete them
		}

		deleteEntries(nodeId); // delete entries corresponding to our node
		nodes.remove(dbobject); // before removing our node
	}


	@Override
	public void deleteNode(Identity nodeId) {
		deleteNode(getDBNode(nodeId));
	}


	private <V> BasicDBObject entryToDbEntry(Entry<V> entry) {
		BasicDBObject dbNode = new BasicDBObject();
		dbNode.append("identity", entry.getNodeId());
		dbNode.append("value", entry.getValue());
		dbNode.append("key", entry.getKey());
		return dbNode;
	}


	@Override
	public <V> boolean existsEntry(Identity id, Key<V> key) {
		BasicDBObject dbEntry = new BasicDBObject();
		dbEntry.append("identity", id);
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
		node.append("identity", id);
		DBCursor dbc = nodes.find(node);


		if(dbc == null || !dbc.hasNext()) {
			return false;
		}
		return true;
	}


//	@Override
//	public <V> V getData(Identity id, Key<V> key) {
//		return getEntry(id).get(key);
//	}


	@Override
	public ASTCursor getAstCursor(Identity id) {
		return new ASTCursor(this, id);
	}


//	@Override
//	protected EntryMap getEntry(Identity id) {
//		return (EntryMap) getDBNode(id).get("entry");
//	}


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
		childnode.append("parent", id);
		DBCursor dbc = nodes.find(childnode);

		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException();
		}

		if(dbc.count() <= i) {
			throw new IndexOutOfBoundsException("children are numbered from 0..n-1, so child number <" + i + "> is illegal");
		}

		int n = 0;
		for(DBObject obj : dbc) {
			if(n == i) {
				return (Identity) obj.get("identity");
			}
			n++;
		}
		throw new IndexOutOfBoundsException("something is wrong in getChildId(Identity id, int i)"); //should never happen, I have it as a precaution
	}


	private <V> DBObject getDBEntry(Identity id, Key<V> key) {
		BasicDBObject dbEntry = new BasicDBObject();
		dbEntry.append("identity", id);
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
		node.append("identity", id);
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
	public <V> Entry<V> getEntry(Identity id, Key<V> key) {
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


//
//	@Override
//	public Identity makeNode(String name, Identity parent) {
//		return (Identity) createNode(name, parent).get("identity");
//	}

//
//	@Override
//	public Identity makeNode(String name, Identity parent, EntryMap data) {
//		BasicDBObject node = createNode(name, parent);
//		setData(node, data);
//		return (Identity) node.get("identity");
//	}


	@Override
	public Identity getParentId(Identity id) {
		return (Identity) getDBNode(id).get("parent");
	}


//	protected <V> void setData(DBObject node, EntryMap data) {
//		nodes.update(node, new BasicDBObject().append("entry", data));
//	}


	@Override
	public List<Identity> getRoots() {
		BasicDBObject nodeWithParent = new BasicDBObject();
		nodeWithParent.append("parent", null);
		DBCursor dbc = nodes.find(nodeWithParent);

		List<Identity> roots = new ArrayList<Identity>(dbc.count());
		for(DBObject db : dbc) {
			roots.add((Identity) db.get("identity"));
		}
		return roots;
	}


	private BasicDBObject nodeToDbNode(Node node) {
		BasicDBObject dbNode = new BasicDBObject();
		dbNode.append("name", node.getName());
		dbNode.append("parent", node.getParent());
		dbNode.append("identity", node.getIDENTITY());
		return dbNode;
	}


	@Override
	public <T> void storeEntry(Entry<T> entry) {
		if(entry.getNodeId() != null) {
			entries.insert(entryToDbEntry(entry));
		}
		else {
			throw new RuntimeException("Entry cannot be stored without being associated to a node");
		}
	}


	@Override
	public void storeNode(Node node) {
		storeNode(node, false);
	}


	@Override
	public void storeNode(Node node, boolean storeEntries) {
		BasicDBObject dbNode = nodeToDbNode(node);
		nodes.insert(dbNode);
		if(storeEntries) {
			EntryMap entries = node.getEntryMap();
			for(Key<?> k : entries.getKeys()) {
				Entry<?> e = entries.getEntry(k);
				storeEntry(e);
			}
		}
	}


	@Override
	public void storeSubtree(Node node) {
		storeNode(node);
		for(Node child : node.getChildren()) {
			storeSubtree(child); //TODO eternal loops may occur
		}
	}

}
