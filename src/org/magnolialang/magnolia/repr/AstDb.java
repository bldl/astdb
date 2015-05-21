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


	ASTCursor getParent(Identity id);


	Identity getParentId(Identity id);


	Identity getRoot();


	boolean hasData(Identity id, Key<?> key);


	Identity makeNode(String name, Identity parent); //TODO maybe data in constr., maybe ID instead of astcursor


	<V> Identity makeNode(String name, Identity parent, Key<V> key, V data); //TODO maybe ID instead of astcursor


	void setChild(Identity parentId, int childIndex, Identity newChildId);


	<V> void setData(Identity id, Key<V> key, V data);
}
