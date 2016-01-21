package org.magnolialang.magnolia.repr.examples;


import org.magnolialang.magnolia.repr.Ast;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.impl.MongoAST;
import org.magnolialang.magnolia.repr.impl.MongoIdentityDomain;
import org.magnolialang.magnolia.repr.impl.MongoIdentityDomain.IdentityDomain;

public class Basic {
	static Key<Integer> intkey = new Key<Integer>() {
	};
	static Key<String> stringkey = new Key<String>() {
	};


	public static void main(String[] args) {

		Ast astDB = new MongoAST("test");
//
//		Node plusNode = new Node("plusNode", new Node("plusA").addEntry(new Entry<Integer>(intkey, 4)), new Node("plusB").addEntry(new Entry<Integer>(intkey, 7)));
//
//		Node multiPropertyNode = new Node("multiPropertyNode", new EntryMap(new Entry<Integer>(intkey, 4), new Entry<String>(stringkey, "examplestring")), null);
//
//		Node multiPropertyNodeWithChildren = new Node("multiPropertyNode", new EntryMap(new Entry<Integer>(intkey, 4), new Entry<String>(stringkey, "examplestring")), null, new Node("childA").addEntry(new Entry<Integer>(intkey, 7)), new Node("childWithoutEntry"));

//		astDB.storeSubtree(plusNode);


		MongoIdentityDomain mid = new MongoIdentityDomain();
		IdentityDomain d = mid.getDomainInstance("hei");

	}
}
