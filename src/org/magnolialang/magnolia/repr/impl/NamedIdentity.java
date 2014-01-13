package org.magnolialang.magnolia.repr.impl;


public class NamedIdentity extends AnonIdentity {
	private final String name;


	public NamedIdentity(String name) {
		super();
		this.name = name;
	}


	@Override
	public String toString() {
		return name;
	}

}
