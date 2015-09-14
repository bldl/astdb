package org.magnolialang.magnolia.repr;


/**
 * A unique identity that identifies a node, possibly across multiple trees.
 * 
 * Identitise are reference-equal.
 * 
 */
public interface Identity extends Comparable<Identity> {

	/**
	 * 
	 * Compare.
	 * 
	 * The order is arbitrary, but consistent.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Identity other);


	/**
	 * @param other
	 *            Other object
	 * @return True if the two identities are exactly the same.
	 */
	@Override
	public boolean equals(Object other);
}
