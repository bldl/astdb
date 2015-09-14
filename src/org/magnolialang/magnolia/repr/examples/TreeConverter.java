package org.magnolialang.magnolia.repr.examples;

import static nuthatch.library.JoinPoints.down;
import static nuthatch.library.JoinPoints.up;

import java.util.Stack;

import nuthatch.examples.ExampleStat;
import nuthatch.examples.xmpllang.Type;
import nuthatch.examples.xmpllang.XmplNode;
import nuthatch.examples.xmpllang.full.XmplCursor;
import nuthatch.examples.xmpllang.full.XmplWalker;
import nuthatch.library.ActionFactory;
import nuthatch.library.BaseAction;
import nuthatch.library.Walk;
import nuthatch.tree.TreeCursor;
import nuthatch.walker.Walker;

import org.magnolialang.magnolia.repr.Ast;
import org.magnolialang.magnolia.repr.Entry;
import org.magnolialang.magnolia.repr.Identity;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.Node;
import org.magnolialang.magnolia.repr.impl.MongoAST;

public class TreeConverter<V, T, C extends TreeCursor<V, T>, W extends Walker<V, T, W>> {

	public TreeConverter() {

	}


	public Walk<W> conversionWalk() {
		ActionFactory<V, T, C, W> af = (ActionFactory<V, T, C, W>) ActionFactory.actionFactory;
		final Ast astDB = new MongoAST("test");
		final Key<V> dataKey = new Key<V>() {
		};
		final Key<T> typeKey = new Key<T>() {
		};

		return af.walk(new BaseAction<W>() {
			private Stack<Identity> parents = new Stack<>();


			@Override
			public int step(W walker) {
				if(down(walker)) {
					String name = walker.getName();
					T type = walker.getType();
					V value = walker.getData();

					System.out.println("Adding node " + name + " type " + type + " value " + value);
					Node node = new Node(name);
					if(value != null) {
						node.addEntry(new Entry<V>(dataKey, value));
					}
					if(type != null) {
						node.addEntry(new Entry<T>(typeKey, type));
					}

					parents.push(node.getIDENTITY());
					astDB.storeNode(node);
				}

				if(up(walker)) {
					Identity identity = parents.pop();
				}
				return PROCEED;
			}
		});
	}


	public static void main(String[] args) {
		TreeConverter<XmplNode, Type, XmplCursor, XmplWalker> converter = new TreeConverter<XmplNode, Type, XmplCursor, XmplWalker>();
		Walk<XmplWalker> conversionWalk = converter.conversionWalk();

		XmplWalker walker = new XmplWalker(ExampleStat.stat2, conversionWalk);

		walker.start();

//		ExampleTree.TREE;
	}
}
