package org.magnolialang.magnolia.repr;

import java.io.Serializable;

import nuthatch.tree.TreeCursor;
import nuthatch.tree.TreeHandle;
import nuthatch.tree.impl.AbstractTreeCursor;

import org.magnolialang.magnolia.repr.ASTCursor.ASTHandle;

public class ASTCursor extends AbstractTreeCursor<Identity, Kind, ASTHandle> {
	public ASTCursor(Ast ast, Identity id) {
		this(new ASTHandle(ast, id));
	}


	protected ASTCursor(AbstractTreeCursor<Identity, Kind, ASTHandle> src, boolean fullTree) {
		super(src, fullTree);
	}


	protected ASTCursor(ASTHandle tree) {
		super(tree);
	}


	@Override
	public ASTCursor copy() {
		return new ASTCursor(this, true);
	}


	@Override
	public ASTCursor copyAndReplaceSubtree(TreeCursor<Identity, Kind> replacement) {
		throw new UnsupportedOperationException();
	}


	@Override
	public ASTCursor copySubtree() {
		return new ASTCursor(this, false);
	}


	@Override
	public int getArity() {
		return getCurrent().ast.getNumChildren(getCurrent().id);
	}


	@Override
	public Identity getData() {
		return getCurrent().id;
	}


	public <V extends Serializable> V getData(Key<V> key) {
		ASTHandle current = getCurrent();
		return current.ast.getEntry(current.id, key).getValue();
	}


	@Override
	public String getName() {
		return getCurrent().ast.getNode(getCurrent().id).getName();
	}


	@Override
	public Kind getType() {
		throw new UnsupportedOperationException();
		// return getCurrent().ast.getKind(getCurrent().id);
	}


	@Override
	public boolean hasData() {
		return true;
	}


	public boolean hasData(Key<? extends Serializable> key) {
		Entry<?> e = getCurrent().ast.getEntry(getCurrent().id, key);
		return e != null && e.getValue() != null;
	}


	@Override
	public boolean hasName() {
		return true;
	}


	@Override
	public boolean subtreeEquals(TreeHandle<Identity, Kind> other) {
		if(other instanceof ASTCursor) {
			ASTHandle otherHandle = ((ASTCursor) other).getCurrent();
			return (getCurrent().ast == otherHandle.ast && getCurrent().id == otherHandle.id);
		}
		throw new UnsupportedOperationException();

	}


	@Override
	protected ASTHandle getChild(int i) {
		ASTHandle node = getCurrent();
		return new ASTHandle(node.ast, node.ast.getChildId(node.id, i));
	}


	@Override
	protected ASTHandle replaceChild(ASTHandle node, ASTHandle child, int i) {
		throw new UnsupportedOperationException();
	}


	static class ASTHandle {
		public final Ast ast;
		public final Identity id;


		public ASTHandle(Ast ast, Identity id) {
			super();
			this.ast = ast;
			this.id = id;
		}
	}
}
