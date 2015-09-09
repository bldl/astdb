package org.magnolialang.magnolia.repr;

import java.util.List;


/**
 * Interface specifying the API for handling Abstract syntax trees.
 * 
 * 
 * @author patmon
 */
public interface Ast {
	//TODO problemer som skjer om man lager sykler, DAG, osv.
	// PS: Lett å sjekke om det er et tre, for da har alle nodene kun en forelder
	//TODO skriv ut trær fra og med rot/en spesifikk node. BFS/DFS?


	/**
	 * Clears all nodes and entries from this ast
	 */
	void clearAst();


	/**
	 * Counts the number of entries stored in the ast
	 * 
	 * @return number of entries stored in the ast
	 */
	long countEntries();


	/**
	 * Counts the number of nodes stored in the ast
	 * 
	 * @return number of nodes stored in the ast
	 */
	long countNodes();


	/**
	 * deletes the Node associated with an Identity. Also deletes all
	 * child-nodes and their entries!
	 * 
	 * @param nodeId
	 *            the Identity of the node we'll delete from the Ast
	 */
	void deleteNode(Identity nodeId);


	/**
	 * Checks if an Entry with data of type "V" corresponding to a Node with
	 * Identity "id" exists
	 * 
	 * @param id
	 *            the Identity of the Node corresponding to an Entry
	 * @param key
	 *            the Key<V> unlocking the data of the entry
	 * @return True if such an entry exists, false otherwise
	 */
	<V> boolean existsEntry(Identity id, Key<V> key);


	/**
	 * checks if there exists a node with Identity id
	 * 
	 * @param id
	 *            the Identity of the node we're checking existence of.
	 * @return True if such a node exists, false otherwise
	 */
	boolean existsNode(Identity id);


	/**
	 * Returns the AstCursor associated with some identity
	 * 
	 * @param id
	 *            Identity we're looking up
	 * @return ASTCursor belonging with some identity
	 */
	ASTCursor getAstCursor(Identity id);


	/**
	 * Gets the name of the AST
	 * 
	 * @return String astName
	 *         the name of the ast
	 */
	String getAstName();


	/**
	 * Returns the ASTCursor of child number 'i' from the AST
	 * 
	 * @param id
	 *            Identity of the node which we'll get a child from
	 * @param i
	 *            Integer corresponding to which child we'll get
	 * @return ASTCursor of the childNode we're getting
	 */
	ASTCursor getChild(Identity id, int i);


	/**
	 * Returns the Identity of child number 'i' from the AST.
	 * 
	 * @param id
	 *            the Identity of the node which we're finding children of
	 * @param i
	 *            Integer corresponding to which child we're looking up
	 * @return Identity the node corresponding to the child we're looking up
	 */
	Identity getChildId(Identity id, int i);


	/**
	 * Gets the entry corresponding to some node Identity 'id' and type 'V'
	 * 
	 * @param id
	 *            the Identity to which the entry belongs
	 * @param key
	 *            the Key<V> holding the type <V> of the Value contained in the
	 *            entry
	 * @return Entry<V> an entry with data of type V
	 */
	<V> Entry<V> getEntry(Identity id, Key<V> key);


//	/**
//	 * TODO what the hell does this do again? Removed until there is a clear need for it
//	 * 
//	 * @param id
//	 * @return
//	 */
//	Kind getKind(Identity id);
//

	/**
	 * Returns the node associated with an Identity or null if such a node
	 * doesn't exist
	 * 
	 * @param id
	 *            the Identity associated with the node
	 * @return the Node associated with the Identity, or null if such a node
	 *         doesn't exist
	 */
	Node getNode(Identity id);


	/**
	 * Gets the number of children of some node with Identity "id"
	 * 
	 * @param id
	 *            the Identity of the node we're finding children of
	 * @return Integer the number of children of some node with Identity "id"
	 */
	int getNumChildren(Identity id);


	/**
	 * Gets the Identity of the parent of the node with Identity "id"
	 * 
	 * @param id
	 *            the Identity of the node which we're looking for a parent of
	 * @return Identity of the parent of the node with Identity "id"
	 */
	Identity getParentId(Identity id);


	/**
	 * Returns a list of all the root-nodes.
	 * A node is a root-node if it doesn't have any parent
	 * 
	 * @return List<Identity> of root-nodes
	 */
	List<Identity> getRoots();


	/**
	 * Stores an entry in the AST. Entry must have a corresponding Node.
	 * 
	 * @param entry
	 *            the Entry to be stored
	 */
	<T> void storeEntry(Entry<T> entry);


	/**
	 * Stores a node in the AST. Does NOT store its entries!
	 * 
	 * @param node
	 *            the Node to be stored
	 */
	void storeNode(Node node);


	/**
	 * Stores a node in the AST, and its entries if storeEntires is true.
	 * 
	 * @param node
	 *            the Node to be stored
	 * @param storeEntries
	 *            boolean toggling whether to store the node entries or not
	 */
	void storeNode(Node node, boolean storeEntries);


	/**
	 * stores a node and all its child-nodes in the Ast
	 * 
	 * @param node
	 *            the Node which we'll store together with its children
	 */
	void storeSubtree(Node node);
}
