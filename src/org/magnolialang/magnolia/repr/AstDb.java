package org.magnolialang.magnolia.repr;

public interface AstDb {

	//TODO AstDB skal håndtere en haug av AST'er, 
	//TODO lag eget interface for AST, refakturer inn alt som er relevant
	//TODO problemer som skjer om man lager sykler, DAG, osv. 
	// PS: Lett å sjekke om det er et tre, for da har alle nodene kun en forelder
	//TODO skriv ut trær fra og med rot/en spesifikk node. BFS/DFS?

	void addChild(Identity parentId, Identity newChildId);


	void deleteChild(Identity parentId, int childIndex);


	ASTCursor getChild(Identity id, int i);


	Identity getChildId(Identity id, int i);


	<V> V getData(Identity id, Key<V> key);


	Kind getKind(Identity id);


	String getName(Identity id);


	ASTCursor getNode(Identity id);


	int getNumChildren(Identity id);


	Identity getRoot(); //TODO how many trees do we have? Every child of root is the start of a tree


	boolean hasData(Identity id, Key<?> key);


	Identity makeNode(String name, Identity... children); //TODO maybe data in constr., maybe ID instead of astcursor


	void setChild(Identity parentId, int childIndex, Identity newChildId);


	<V> void setData(Identity id, Key<V> key, V data);
}
