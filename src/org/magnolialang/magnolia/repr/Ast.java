package org.magnolialang.magnolia.repr;

import java.util.List;


/**
 * Sitter og jobber med prosjektet nå, tror jeg har fu
 * Interface specifying the API for handling Abstract syntax trees.
 * 
 * 
 * @author patmon
 */
public interface Ast {
	//TODO problemer som skjer om man lager sykler, DAG, osv. 
	// PS: Lett å sjekke om det er et tre, for da har alle nodene kun en forelder
	//TODO skriv ut trær fra og med rot/en spesifikk node. BFS/DFS?


	//void addChild(Identity parentId, Identity newChildId);


	void deleteNode(Identity childId);


	ASTCursor getChild(Identity id, int i);


	Identity getChildId(Identity id, int i);


	<V> V getData(Identity id, Key<V> key);


	Kind getKind(Identity id);


	String getName(Identity id);


	ASTCursor getNode(Identity id);


	int getNumChildren(Identity id);


	ASTCursor getParent(Identity id);


	Identity getParentId(Identity id);


	List<Identity> getRoots();


	boolean hasData(Identity id, Key<?> key);


	Identity makeNode(String name, Identity parent);


	Identity makeNode(String name, Identity parent, EntryMap data);


	void setData(Identity id, EntryMap data);
}
