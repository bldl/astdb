package org.magnolialang.magnolia.repr.impl;

import org.magnolialang.magnolia.repr.Identity;

public class AnonIdentity implements Identity {
	@Override
	public int compareTo(Identity other) {
		return Integer.compare(System.identityHashCode(this), System.identityHashCode(other));
	}


	@Override
	public boolean equals(Object other) {
		return this == other;
	}


	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

}
