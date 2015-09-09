package org.magnolialang.magnolia.repr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EntryMap {
	private Map<Key<?>, Object> data;
	private Node masterNode;


	/**
	 * Make a new EntryMap belonging to a node with a list of entries
	 * 
	 * @param entries
	 *            The entries which this map is initialized with
	 */
	public EntryMap(Entry<?>... entries) {
		data = new HashMap<Key<?>, Object>();
		for(Entry<?> e : entries) {
			addEntry(e);
		}
	}


	/**
	 * Make a new EntryMap belonging to a node with a list of entries
	 * 
	 * @param node
	 *            The node which this map belongs to
	 * @param entries
	 *            The entries which this map is initialized with
	 */
	public EntryMap(Node node, Entry<?>... entries) {
		data = new HashMap<Key<?>, Object>();
		for(Entry<?> e : entries) {
			addEntry(e);
		}
		masterNode = node;
	}


	public <V> Node addEntry(Entry<V> entry) {
		data.put(entry.getKey(), entry.getValue());
		return masterNode;
	}


	/**
	 * Returns the Entry<V> associated with some Key<V>, or null if such an
	 * entry doesn't exist
	 * 
	 * @param key
	 *            The key which belongs to some entry
	 * @return entry
	 *         The Entry<V> corresponding to some key,
	 *         or null if no such entry exists
	 */
	public <V> Entry<V> getEntry(Key<V> key) {
		@SuppressWarnings("unchecked")
		V v = (V) data.get(key);
		return v != null ? new Entry<V>(key, v) : null;
	}


	public Set<Key<?>> getKeys() {
		return data.keySet();
	}


	public Node getMasterNode() {
		return masterNode;
	}


	public <T> boolean hasData(Key<T> key) {
		return null != data.get(key);
	}


	public void setMasterNode(Node newMasterNode) {
		masterNode = newMasterNode;
	}
}
