package org.magnolialang.magnolia.repr.impl;

import java.io.Serializable;

import org.magnolialang.magnolia.repr.Identity;

public class AnonIdentity implements Identity, Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4695600296642737607L;


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
