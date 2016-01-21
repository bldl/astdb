package org.magnolialang.magnolia.repr;

import java.io.Serializable;

/**
 * Every entry is on the form
 * 
 * entry : {
 * .. identity : <Identity>, //the identity of the node to which it belongs
 * .. key : <Key<?>>,
 * .. value : <?>
 **/

public class Entry<T extends Serializable> {

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


	public Entry(Key<T> key, T value, Identity node_id) {
		if(key == null) {
			throw new IllegalArgumentException("key must be non-null");
		}
		if(value == null) {
			throw new IllegalArgumentException("value must be non-null");
		}

		KEY = key;
		VALUE = value;

		if(node_id != null) {
			setNodeId(node_id);
		}
	}


	public Entry(Key<T> key, T value, Node node) {
		if(key == null) {
			throw new IllegalArgumentException("key must be non-null");
		}
		if(value == null) {
			throw new IllegalArgumentException("value must be non-null");
		}

		KEY = key;
		VALUE = value;

		if(node != null) {
			setNodeId(node.IDENTITY);
		}
	}


	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}

		Entry other = (Entry) obj;
		if(KEY == null) {
			if(other.KEY != null) {
				return false;
			}
		}
		else if(!KEY.equals(other.KEY)) {
			return false;
		}

		// TODO maybe add nodeid to equals? Not sure
//		if(NODEID == null) {
//			if(other.NODEID != null) {
//				return false;
//			}
//		}
//		else if(!NODEID.equals(other.NODEID)) {
//			return false;
//		}

		if(VALUE == null) {
			if(other.VALUE != null) {
				return false;
			}
		}
		else if(!VALUE.equals(other.VALUE)) {
			return false;
		}
		return true;
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((KEY == null) ? 0 : KEY.hashCode());
//		result = prime * result + ((NODEID == null) ? 0 : NODEID.hashCode()); //TODO only add if also added in equals
		result = prime * result + ((VALUE == null) ? 0 : VALUE.hashCode());
		return result;
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
