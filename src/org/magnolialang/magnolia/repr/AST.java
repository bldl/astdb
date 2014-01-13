package org.magnolialang.magnolia.repr;

public interface AST {

	ASTCursor getChild(Identity id, int i);


	Identity getChildId(Identity id, int i);


	<V> V getData(Identity id, Key<V> key);


	Kind getKind(Identity id);


	String getName(Identity id);


	ASTCursor getNode(Identity id);


	int getNumChildren(Identity id);


	boolean hasData(Identity id, Key<?> key);

}
