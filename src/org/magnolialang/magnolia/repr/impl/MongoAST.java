package org.magnolialang.magnolia.repr.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.magnolialang.magnolia.repr.ASTCursor;
import org.magnolialang.magnolia.repr.Ast;
import org.magnolialang.magnolia.repr.Identity;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.Kind;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoAST implements Ast {


	static class Entry {
		Map<Key<?>, Object> data = new HashMap<Key<?>, Object>();


		<T> T get(Key<T> key) {
			return (T) data.get(key);
		}


		<T> void put(Key<T> key, T value) {
			data.put(key, value);
		}
	}

	/**
	 * Every node in the graph is on the form
	 * 
	 * node : {
	 * .. identity : <Identity>,
	 * .. name : <String>, // name of the node
	 * .. parent : <Identity>, //identity of its parent
	 * .. entry : <Entry> // the data contained in the node
	 * }
	 */
	DBCollection graph;
	final String GRAPH_KEY;

	protected static final Key<?> DATAKEY = new Key<Object>() {
	};
	protected static final Key<String> NAMEKEY = new Key<String>() {
	};
	protected static final Key<Kind> KINDKEY = new Key<Kind>() {
	};


	public MongoAST(String astName) {
		GRAPH_KEY = astName + "graph";
		graph = DatabaseFactory.getDb().getCollection(GRAPH_KEY);
	}


	protected void deleteNode(DBObject dbobject) {
		Identity nodeId = (Identity) dbobject.get("identity");

		//find all children
		BasicDBObject childnodes = new BasicDBObject().append("parent", nodeId);
		DBCursor dbc = graph.find(childnodes);
		while(dbc.hasNext()) {
			deleteNode(dbc.next()); // and then proceed to delete them
		}

		graph.remove(dbobject); // before removing our node
	}


	@Override
	public void deleteNode(Identity nodeId) {
		BasicDBObject thisNode = new BasicDBObject().append("identity", nodeId);
		DBCursor dbc = graph.find(thisNode);

		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException("Trying to delete something already deleted");
		}

		DBObject node = dbc.next();

		if(dbc.hasNext()) {
			throw new RuntimeException("Several nodes had the same identity!");
		}

		deleteNode(node);
	}


	@Override
	public ASTCursor getChild(Identity id, int i) {
		Identity childId = getChildId(id, i);
		return getNode(childId);
	}


	@Override
	public Identity getChildId(Identity id, int i) {
		if(i < 0) {
			throw new IllegalArgumentException("children are numbered from 0..n-1, so child number <" + i + "> is illegal");
		}

		BasicDBObject childnode = new BasicDBObject();
		childnode.append("parent", id);
		DBCursor dbc = graph.find(childnode);

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


	protected Entry getEntry(Identity id) {
		BasicDBObject node = new BasicDBObject();
		node.append("identity", id); //TODO globalize?
		DBCursor dbc = graph.find(node);


		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException();
		}

		if(dbc.count() > 1) {
			throw new RuntimeException("2 elements matched to same id!");
		}

		Entry entry = (Entry) dbc.next().get("entry");

		return entry;
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
	public ASTCursor getNode(Identity id) {
		return new ASTCursor(this, id);
	}


	@Override
	public int getNumChildren(Identity id) {
		BasicDBObject nodeWithParent = new BasicDBObject();
		nodeWithParent.append("parent", id);
		DBCursor dbc = graph.find(nodeWithParent);

		if(dbc == null) {
			throw new NoSuchElementException("can't happen - check getNumChildren(Identity id) method");
		}

		return dbc.count();
	}


	@Override
	public ASTCursor getParent(Identity id) {
		return getNode(getParentId(id));
	}


	@Override
	public Identity getParentId(Identity id) {
		BasicDBObject node = new BasicDBObject();
		node.append("identity", id);
		DBCursor dbc = graph.find(node);

		if(dbc == null) {
			throw new NoSuchElementException("can't happen - check getParent(Identity id) method");
		}
		if(dbc.count() == 0) {
			throw new NoSuchElementException("no match on identity " + id);
		}
		if(dbc.count() > 1) {
			throw new RuntimeException("several matches on identity " + id);
		}

		Identity parentId = (Identity) dbc.next().get("parent");
		return parentId;
	}


	@Override
	public List<Identity> getRoots() {
		BasicDBObject nodeWithParent = new BasicDBObject();
		nodeWithParent.append("parent", null);
		DBCursor dbc = graph.find(nodeWithParent);

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
		BasicDBObject node = new BasicDBObject();
		node.append("name", name);
		node.append("parent", parent);
		node.append("entry", null);

		Identity id = null; 		//TODO generate id
		node.append("identity", id);

		graph.insert(node);

		return id;
	}


	@Override
	public <V> void setData(Identity id, Key<V> key, V data) {
		BasicDBObject thisNode = new BasicDBObject().append("identity", id);
		DBCursor dbc = graph.find(thisNode);

		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException();
		}

		if(dbc.count() > 1) {
			throw new RuntimeException("2 elements matched to same id!");
		}

		DBObject graphnode = dbc.next();

		Entry entry = (Entry) graphnode.get("entry");
		entry.put(key, data);

		graph.update(graphnode, new BasicDBObject().append("entry", entry));	//TODO test behaves as expected
	}

}
