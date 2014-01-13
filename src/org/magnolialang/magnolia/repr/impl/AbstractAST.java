package org.magnolialang.magnolia.repr.impl;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.magnolialang.magnolia.repr.AST;
import org.magnolialang.magnolia.repr.ASTCursor;
import org.magnolialang.magnolia.repr.Identity;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.Kind;

public class AbstractAST implements AST {
	Map<Identity, Entry> map = new IdentityHashMap<Identity, Entry>();
	Map<Identity, List<Identity>> tree = new IdentityHashMap<Identity, List<Identity>>();
	protected static final Key<?> DATAKEY = new Key<Object>() {
	};
	protected static final Key<String> NAMEKEY = new Key<String>() {
	};
	protected static final Key<Kind> KINDKEY = new Key<Kind>() {
	};


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
		List<Identity> list = tree.get(id);
		if(list == null) {
			throw new NoSuchElementException();
		}
		if(i >= list.size()) {
			throw new IndexOutOfBoundsException();
		}
		return list.get(i);
	}


	@Override
	public <V> V getData(Identity id, Key<V> key) {
		return getEntry(id).get(key);
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
		List<Identity> list = tree.get(id);
		if(list == null) {
			throw new NoSuchElementException();
		}
		return list.size();
	}


	@Override
	public boolean hasData(Identity id, Key<?> key) {
		return getData(id, key) != null;
	}


	protected Entry getEntry(Identity id) {
		Entry entry = map.get(id);
		if(entry == null) {
			throw new NoSuchElementException();
		}
		return entry;
	}


	static class Entry {
		Map<Key<?>, Object> data = new HashMap<Key<?>, Object>();


		<T> T get(Key<T> key) {
			return (T) data.get(key);
		}


		<T> void put(Key<T> key, T value) {
			data.put(key, value);
		}
	}
}
