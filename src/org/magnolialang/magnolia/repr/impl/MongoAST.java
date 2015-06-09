package org.magnolialang.magnolia.repr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import oldcode.EntryMap;

import org.magnolialang.magnolia.repr.ASTCursor;
import org.magnolialang.magnolia.repr.Ast;
import org.magnolialang.magnolia.repr.Entry;
import org.magnolialang.magnolia.repr.Identity;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.Kind;
import org.magnolialang.magnolia.repr.Node;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * TODO
 * - ferdiggjør EntryMap/Entry refaktorering og lag en collection for Entry = {
 * nodeId, key, data }
 * 
 * 
 * - sjekk om effektivt, < dump et tre inn og sjekk
 * --- er dette mer effektivt enn vanlig måter å lagre trær på eller ikke?
 * --- eksperimenter med alternativer?
 * 
 */

public class MongoAST implements Ast {

	DBCollection nodes, entries;
	final String NODE_KEY, ENTRY_KEY;

	protected static final Key<?> DATAKEY = new Key<Object>() {
	};
	protected static final Key<String> NAMEKEY = new Key<String>() {
	};
	protected static final Key<Kind> KINDKEY = new Key<Kind>() {
	};


	public MongoAST(String astName) {
		NODE_KEY = astName + "graph";
		ENTRY_KEY = astName + "entry";
		nodes = DatabaseFactory.getDb().getCollection(NODE_KEY);
		entries = DatabaseFactory.getDb().getCollection(ENTRY_KEY);
	}


	protected void deleteNode(DBObject dbobject) {
		Identity nodeId = (Identity) dbobject.get("identity");

		//find all children
		BasicDBObject childnodes = new BasicDBObject().append("parent", nodeId);
		DBCursor dbc = nodes.find(childnodes);
		while(dbc.hasNext()) {
			deleteNode(dbc.next()); // and then proceed to delete them
		}

		nodes.remove(dbobject); // before removing our node
	}


	@Override
	public void deleteNode(Identity nodeId) {
		deleteNode(getDBNode(nodeId));
	}


	private BasicDBObject entryToDbEntry(Entry entry) {
		BasicDBObject dbNode = new BasicDBObject();
		dbNode.append("identity", entry.getNodeId());
		dbNode.append("value", entry.getValue());
		dbNode.append("key", entry.getKey());
		return dbNode;
	}


	@Override
	public <V> boolean existsEntry(Identity id, Key<V> key) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean existsNode(Identity id) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public ASTCursor getAstCursor(Identity id) {
		return new ASTCursor(this, id);
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


	@Override
	public <V> V getData(Identity id, Key<V> key) {
		return getEntry(id).get(key);
	}


	protected DBObject getDBNode(Identity id) {
		BasicDBObject node = new BasicDBObject();
		node.append("identity", id); //TODO globalize?
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
	protected EntryMap getEntry(Identity id) {
		return (EntryMap) getDBNode(id).get("entry");
	}


	@Override
	public <V> Entry<V> getEntry(Identity id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public <V> Entry<V> getEntry(Identity id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Kind getKind(Identity id) {
		return getEntry(id).get(KINDKEY);
	}


	@Override
	public String getName(Identity id) {
		return getEntry(id).get(NAMEKEY);
	}


	@Override
	public Node getNode(Identity id) {
		// TODO Auto-generated method stub
		return null;
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
	public ASTCursor getParentAstCursor(Identity id) {
		return getAstCursor(getParentId(id));
	}


	@Override
	public Identity getParentId(Identity id) {
		return (Identity) getDBNode(id).get("parent");
	}


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


	@Override
	public boolean hasData(Identity id, Key<?> key) {
		return getData(id, key) != null;
	}


	@Override
	public Identity makeNode(String name, Identity parent) {
		return (Identity) createNode(name, parent).get("identity");
	}


	@Override
	public Identity makeNode(String name, Identity parent, EntryMap data) {
		BasicDBObject node = createNode(name, parent);
		setData(node, data);
		return (Identity) node.get("identity");
	}


	private BasicDBObject nodeToDbNode(Node node) {
		BasicDBObject dbNode = new BasicDBObject();
		dbNode.append("name", node.getName());
		dbNode.append("parent", node.getParent());
		dbNode.append("identity", node.getIDENTITY());
		return dbNode;
	}


	protected <V> void setData(DBObject node, EntryMap data) {
		nodes.update(node, new BasicDBObject().append("entry", data));	//TODO test behaves as expected
	}


	@Override
	public <T> Identity storeEntry(Entry<T> entry) {
		entries.insert(entryToDbEntry(entry)); //TODO duplicate check etc.
		return entry.getNodeId();
	}


	@Override
	public Identity storeNode(Node node) {
		BasicDBObject dbNode = nodeToDbNode(node); //TODO duplicate check etc.
		nodes.insert(dbNode);
		return node.getIDENTITY();
	}


	@Override
	public void storeSubtree(Node node) {
		// TODO Auto-generated method stub

	}

}
