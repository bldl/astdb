package org.magnolialang.magnolia.repr;

import org.magnolialang.magnolia.repr.impl.AnonIdentity;
import org.magnolialang.magnolia.repr.impl.NamedIdentity;

public class IdentityFactory {
	public static Identity makeId() {
		return new AnonIdentity();
	}


	public static Identity makeId(String name) {
		return new NamedIdentity(name);
	}
}
