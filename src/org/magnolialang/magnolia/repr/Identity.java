package org.magnolialang.magnolia.repr;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

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


	/**
	 * An identity domain provides a way to map identities to and from integers.
	 * 
	 */
	static class Domain {
		private final List<Identity> idents = new ArrayList<Identity>();
		private final Map<Identity, Integer> map = new IdentityHashMap<Identity, Integer>();


		public synchronized Identity toIdentity(int i) {
			return idents.get(i);
		}


		public synchronized Identity toIdentityOrNull(int i) {
			if(i >= 0 && i < idents.size()) {
				return toIdentity(i);
			}
			else {
				return null;
			}
		}


		public synchronized int toInt(Identity id) {
			Integer i = map.get(id);
			if(i == null) {
				i = idents.size();
				idents.add(id);
				map.put(id, i);
			}
			return i;
		}

	}
}