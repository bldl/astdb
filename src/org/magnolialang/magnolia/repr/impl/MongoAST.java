package org.magnolialang.magnolia.repr.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.magnolialang.magnolia.repr.ASTCursor;
import org.magnolialang.magnolia.repr.AstDb;
import org.magnolialang.magnolia.repr.Identity;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.Kind;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class MongoAST implements AstDb {


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

	protected static final Key<?> DATAKEY = new Key<Object>() {
	};
	protected static final Key<String> NAMEKEY = new Key<String>() {
	};
	protected static final Key<Kind> KINDKEY = new Key<Kind>() {
	};


	public MongoAST() {
		entries = DatabaseFactory.getDb().getCollection("entries");
		graph = DatabaseFactory.getDb().getCollection("graph");
	}


	@Override
	public void addChild(Identity parentId, Identity newChildId) {
		// TODO Auto-generated method stub

	}


	@Override
	public void deleteChild(Identity parentId, int childIndex) {
		// TODO Auto-generated method stub

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
	public Identity getRoot() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean hasData(Identity id, Key<?> key) {
		return getData(id, key) != null;
	}


	@Override
	public Identity makeNode(String name, Identity... children) {
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
