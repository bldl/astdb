package org.magnolialang.magnolia.repr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	final Identity IDENTITY;
	final String name;
	Identity parent;
	List<Node> children;
	EntryMap entrymap;


	public Node(String name, EntryMap entries, Node... childNodes) {
		IDENTITY = makeId(name);
		this.name = name;
		parent = null;

		if(childNodes != null) {
			children = new ArrayList<Node>();
		}

		for(Node child : childNodes) {
			child.parent = IDENTITY;
			children.add(child);
		}
		entrymap = entries;
		entrymap.setMasterNode(this);
	}


	public Node(String name, Identity parent, EntryMap entries, Node... childNodes) {
		IDENTITY = makeId(name);
		this.name = name;
		this.parent = parent;

		if(childNodes != null) {
			children = new ArrayList<Node>();
		}

		for(Node child : childNodes) {
			child.parent = IDENTITY;
			children.add(child);
		}
		entrymap = entries;
		entrymap.setMasterNode(this);
	}


	public Node(String name, Identity parent, Node... childNodes) {
		IDENTITY = makeId(name);
		this.name = name;
		this.parent = parent;

		if(childNodes != null) {
			children = new ArrayList<Node>();
		}

		for(Node child : childNodes) {
			child.parent = IDENTITY;
			children.add(child);
		}

		entrymap = new EntryMap(this);
	}


	public Node(String name, Node... childNodes) {
		IDENTITY = makeId(name);
		this.name = name;
		parent = null;

		if(childNodes != null) {
			children = new ArrayList<Node>();
		}

		for(Node child : childNodes) {
			child.parent = IDENTITY;
			children.add(child);
		}

		entrymap = new EntryMap(this);
	}


	public <V extends Serializable> Node addEntry(Entry<V> entry) {
		entry.setNodeId(IDENTITY);
		return entrymap.addEntry(entry);
	}


	public List<Node> getChildren() {
		return children;
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
	public void setParent(Identity parent) {
		this.parent = parent;
	}
}
