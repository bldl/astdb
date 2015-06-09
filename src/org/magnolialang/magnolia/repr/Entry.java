package org.magnolialang.magnolia.repr;

/**
 * Every entry is on the form
 * 
 * entry : {
 * .. identity : <Identity>, //the identity of the node to which it belongs
 * .. key : <Key<?>>,
 * .. value : <?>
 **/

public class Entry<T> {

	private Key<T> KEY;
	private T VALUE;
	private Identity NODEID;


	public Entry(Key<T> key, T value) {
		if(key == null) {
			throw new IllegalArgumentException("key must be non-null");
		}
		if(value == null) {
			throw new IllegalArgumentException("value must be non-null");
		}

		KEY = key;
		VALUE = value;
	}


	/**
	 * Gets the key of an Entry
	 * 
	 * @return Key<T> the key of this entry
	 */
	public Key<T> getKey() {
		return KEY;
	}


	/**
	 * Gets the node to which this entry belongs
	 * 
	 * @return Identity the Identity of the node which this belongs to, or null
	 *         if the entry doesn't yet belong to a Node
	 */
	public Identity getNodeId() {
		return NODEID;
	}


	/**
	 * Gets the value stored in this entry
	 * 
	 * @return T the value stored in this entry
	 */
	public T getValue() {
		return VALUE;
	}


	/**
	 * Sets the NODEID of this Entry to be "IDENTITY". Throws a runtimeException
	 * if it already belongs to a Node!
	 * 
	 * @param IDENTITY
	 *            the Identity of the Node to which this Entry is associated.
	 */
	public void setNodeId(Identity IDENTITY) {
		if(NODEID != null) {
			throw new RuntimeException("Entry already belongs to another node!");
		}
		NODEID = IDENTITY;
	}
}
