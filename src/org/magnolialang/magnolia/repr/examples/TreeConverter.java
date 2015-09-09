package org.magnolialang.magnolia.repr.examples;

import nuthatch.library.ActionFactory;
import nuthatch.library.Walk;
import nuthatch.tree.TreeCursor;
import nuthatch.walker.Walker;

public class TreeConverter {

	public TreeConverter() {

	}


	public static <Value, Type, C extends TreeCursor<Value, Type>, W extends Walker<Value, Type, W>> Walk<W> conversionWalk() {
		ActionFactory<Value, Type, C, W> af = (ActionFactory<Value, Type, C, W>) ActionFactory.actionFactory;


		return af.walk(null);
	}

}
