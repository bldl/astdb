package org.magnolialang.magnolia.repr.impl;

import java.io.Serializable;


public class NamedIdentity extends AnonIdentity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5381803116431576768L;
	private final String name;


	public NamedIdentity(String name) {
		super();
		this.name = name;
	}


	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		return name;
	}

}
