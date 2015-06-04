package org.magnolialang.magnolia.repr;

import java.util.HashMap;
import java.util.Map;

public class EntryMap {
	private Map<Key<?>, Object> data;


	public EntryMap() {
		data = new HashMap<Key<?>, Object>();
	}


	public EntryMap(Map<Key<?>, Object> data) {
		this.data = data;
	}


	public <T> T get(Key<T> key) {
		return (T) data.get(key);
	}


	public Map<Key<?>, Object> getData() {
		return data;
	}


	public <T> boolean hasData(Key<T> key) {
		return null != data.get(key);
	}


	public <T> EntryMap put(Key<T> key, T value) {
		data.put(key, value);
		return this;
	}
}
