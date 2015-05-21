package org.magnolialang.magnolia.repr.impl;

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

	DBCollection entries;
	DBCollection graph;
	final String ENTRIES_KEY;
	final String GRAPH_KEY;

	protected static final Key<?> DATAKEY = new Key<Object>() {
	};
	protected static final Key<String> NAMEKEY = new Key<String>() {
	};
	protected static final Key<Kind> KINDKEY = new Key<Kind>() {
	};


	public MongoAST(String astName) {
		GRAPH_KEY = astName + "graph";
		ENTRIES_KEY = astName + "entries";
		entries = DatabaseFactory.getDb().getCollection(ENTRIES_KEY);
		graph = DatabaseFactory.getDb().getCollection(GRAPH_KEY);
	}


	@Override
	public void addChild(Identity parentId, Identity newChildId) {
		graph.update(new BasicDBObject().append("from", parentId), new BasicDBObject().append("$push", new BasicDBObject("to", newChildId)));
	}


	/**
	 * Deletes a node from some parent, removing it and all its children
	 * recursively.
	 */
	@Override
	public void deleteChild(Identity parentId, Identity childId) {
		graph.update(new BasicDBObject().append("from", parentId), new BasicDBObject("$pull", new BasicDBObject("to", childId)));
		deleteNode(childId); //recursively delete children
	}


	protected void deleteNode(Identity nodeId) {
		BasicDBObject nodeFinder = new BasicDBObject().append("from", nodeId);
		DBCursor dbc = graph.find(nodeFinder);

		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException("Trying to delete something already deleted");
		}

		DBObject node = dbc.next();
		List<Identity> to = (List<Identity>) node.get("to");
		graph.remove(node);
		for(Identity childId : to) {
			deleteNode(childId);
		}
	}


	@Override
	public ASTCursor getChild(Identity id, int i) {
		Identity childId = getChildId(id, i);
		return getNode(childId);
	}


	@Override
	public Identity getChildId(Identity id, int i) {
		if(i < 0) {
			throw new IllegalArgumentException();
		}

		BasicDBObject currentNode = new BasicDBObject();
		currentNode.append("from", id);	//TODO globalize?
		DBCursor dbc = graph.find(currentNode);

		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException();
		}

		List<Identity> to = (List<Identity>) dbc.next().get("to"); //TODO globalize?

		if(i >= to.size()) {
			throw new IndexOutOfBoundsException();
		}

		return to.get(i);
	}


	@Override
	public <V> V getData(Identity id, Key<V> key) {
		return getEntry(id).get(key);
	}


	protected Entry getEntry(Identity id) {

		BasicDBObject currentEntry = new BasicDBObject();
		currentEntry.append("from", id); //TODO globalize?
		DBCursor dbc = entries.find(currentEntry);


		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException();
		}

		Entry entry = (Entry) dbc.next().get("entry"); //TODO globalize?

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
		BasicDBObject currentNode = new BasicDBObject();
		currentNode.append("from", id);	//TODO globalize?
		DBCursor dbc = graph.find(currentNode);

		if(dbc == null || !dbc.hasNext()) {
			throw new NoSuchElementException();
		}

		List<Identity> children = (List<Identity>) dbc.next().get("to"); //TODO globalize?

		return children.size();
	}


	@Override
	public ASTCursor getParent(Identity id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Identity getParentId(Identity id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Identity getRoot() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean hasData(Identity id, Key<?> key) {
		return getData(id, key) != null;
	}


	@Override
	public Identity makeNode(String name, Identity parent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public <V> Identity makeNode(String name, Identity parent, Key<V> key, V data) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setChild(Identity parentId, int childIndex, Identity newChildId) {
		// TODO Auto-generated method stub

	}


	@Override
	public <V> void setData(Identity id, Key<V> key, V data) {
		// TODO Auto-generated method stub

	}
}
