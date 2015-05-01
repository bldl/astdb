package org.magnolialang.magnolia.repr.examples;


import org.magnolialang.magnolia.repr.AstDb;
import org.magnolialang.magnolia.repr.Identity;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.impl.MongoAST;

public class Basic {
	static Key<Integer> intkey = new Key<Integer>() {
	};


	public static void main(String[] args) {

		AstDb astDB = new MongoAST();

		Identity id1 = astDB.makeNode("plusA");
		astDB.setData(id1, intkey, 4);

		Identity id2 = astDB.makeNode("plusB");
		astDB.setData(id1, intkey, 7);

		Identity id3 = astDB.makeNode("plusNode", id1, id2);
	}
}
