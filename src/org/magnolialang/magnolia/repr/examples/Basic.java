package org.magnolialang.magnolia.repr.examples;


import org.magnolialang.magnolia.repr.Ast;
import org.magnolialang.magnolia.repr.Entry;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.Node;
import org.magnolialang.magnolia.repr.impl.MongoAST;

public class Basic {
	static Key<Integer> intkey = new Key<Integer>() {
	};


	public static void main(String[] args) {

		Ast astDB = new MongoAST("test");

		Node plusNode = 
				new Node("plusNode", 
						new Node("plusA").addEntry(new Entry<Integer>(intkey, 4)),
						new Node("plusB").addEntry(new Entry<Integer>(intkey, 7)));

		astDB.storeSubtree(plusNode);
	}
}
