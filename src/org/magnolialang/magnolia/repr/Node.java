package org.magnolialang.magnolia.repr;

import java.io.Serializable;

import org.magnolialang.magnolia.repr.impl.AnonIdentity;
import org.magnolialang.magnolia.repr.impl.NamedIdentity;

/**
 * Every node in the graph is on the form
 * 
 * node : {
 * .. identity : <Identity>,
 * .. name : <String>, // name of the node
 * .. parent : <Identity>, //identity of its parent
 * .. entries : <Entry>, //the entries of the Node
 * .. children : <List<Node>> // the child-nodes of the Node
 * }
 **/
public class Node implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5558098146365749731L;
	protected final Identity IDENTITY;
	protected final String name;
	protected Identity parent;
	protected EntryMap entrymap;


	public Node(String name) {
		IDENTITY = makeId(name);
		this.name = name;
		parent = null;
		entrymap = new EntryMap(this);
	}


	public Node(String name, EntryMap entries, Identity parent) {
		IDENTITY = makeId(name);
		this.name = name;
		this.parent = parent;

		entrymap = entries;
		entrymap.setMasterNode(this);
	}


	public Node(String name, Identity parent) {
		IDENTITY = makeId(name);
		this.name = name;
		this.parent = parent;
		entrymap = new EntryMap(this);
	}


	public Node(String name, Identity identity, Identity parentId) {
		this.IDENTITY = identity;
		this.name = name;
		parent = parentId;
		entrymap = new EntryMap(this);
	}


	public <V extends Serializable> Node addEntry(Entry<V> entry) {
		entry.setNodeId(IDENTITY);
		return entrymap.addEntry(entry);
	}


	public EntryMap getEntryMap() {
		return entrymap;
	}


	public Identity getIDENTITY() {
		return IDENTITY;
	}


	public String getName() {
		return name;
	}


	public Identity getParent() {
		return parent;
	}


	private Identity makeId(String name) {
		return name == null || name == "" ? new AnonIdentity() : new NamedIdentity(name); //might want to make id's in another way
	}


	/**
	 * Warning: unsafe operation, may cause cycles and invalid trees. Use with
	 * care
	 * 
	 * @param parent
	 *            the new parent of this node
	 */
	public void setParent(Identity identity) {
		this.parent = identity;
	}


	/**
	 * Warning: unsafe operation, may cause cycles and invalid trees. Use with
	 * care
	 * 
	 * @param parent
	 *            the new parent of this node
	 */
	public void setParent(Node parentNode) {
		if(parentNode == null) {
			return;
		}
		setParent(parentNode.IDENTITY);
	}
}
