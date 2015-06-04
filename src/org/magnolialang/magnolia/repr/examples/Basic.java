package org.magnolialang.magnolia.repr.examples;


import org.magnolialang.magnolia.repr.Ast;
import org.magnolialang.magnolia.repr.EntryMap;
import org.magnolialang.magnolia.repr.Identity;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.impl.MongoAST;

public class Basic {
	static Key<Integer> intkey = new Key<Integer>() {
	};


	public static void main(String[] args) {

		Ast astDB = new MongoAST("test");

		Identity id3 = astDB.makeNode("plusNode", null); //it becomes the root

		Identity id1 = astDB.makeNode("plusA", id3);
		astDB.setData(id1, new EntryMap().put(intkey, 4));

		Identity id2 = astDB.makeNode("plusB", id3, new EntryMap().put(intkey, 4));
	}
}
