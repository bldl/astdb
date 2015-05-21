package org.magnolialang.magnolia.repr;

import nuthatch.tree.TreeCursor;
import nuthatch.tree.TreeHandle;
import nuthatch.tree.impl.AbstractTreeCursor;

import org.magnolialang.magnolia.repr.ASTCursor.ASTHandle;

public class ASTCursor extends AbstractTreeCursor<Identity, Kind, ASTHandle> {
	static class ASTHandle {
		public final Ast ast;
		public final Identity id;


		public ASTHandle(Ast ast, Identity id) {
			super();
			this.ast = ast;
			this.id = id;
		}
	}


	/*	protected ASTCursor(AbstractTreeCursor<Value, Kind, ASTHandle> src, ASTHandle replacement) {
		super(src, replacement);
		// TODO Auto-generated constructor stub
	}

	*/
	protected ASTCursor(AbstractTreeCursor<Identity, Kind, ASTHandle> src, boolean fullTree) {
		super(src, fullTree);
		// TODO Auto-generated constructor stub
	}


	public ASTCursor(Ast ast, Identity id) {
		this(new ASTHandle(ast, id));
	}


	protected ASTCursor(ASTHandle tree) {
		super(tree);
	}


	@Override
	public ASTCursor copy() {
		return new ASTCursor(this, true);
	}


	@Override
	public ASTCursor copyAndReplaceSubtree(TreeCursor<Identity, Kind> replacement) { //TODO hvorfor er dette unupported?
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
	protected ASTHandle getChild(int i) {
		ASTHandle node = getCurrent();
		return new ASTHandle(node.ast, node.ast.getChildId(node.id, i));
	}


	@Override
	public Identity getData() {
		return getCurrent().id;
	}


	public <V> V getData(Key<V> key) {
		return getCurrent().ast.getData(getCurrent().id, key);
	}


	@Override
	public String getName() {
		return getCurrent().ast.getName(getCurrent().id);
	}


	@Override
	public Kind getType() {
		return getCurrent().ast.getKind(getCurrent().id);
	}


	@Override
	public boolean hasData() {
		return true;
	}


	public boolean hasData(Key<?> key) {
		return getCurrent().ast.hasData(getCurrent().id, key);
	}


	@Override
	public boolean hasName() {
		return true;
	}


	@Override
	protected ASTHandle replaceChild(ASTHandle node, ASTHandle child, int i) {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean subtreeEquals(TreeHandle<Identity, Kind> other) {
		if(other instanceof ASTCursor) {
			ASTHandle otherHandle = ((ASTCursor) other).getCurrent();
			if(getCurrent().ast == otherHandle.ast && getCurrent().id == otherHandle.id) {
				return true;
			}
		}
		throw new UnsupportedOperationException(); //TODO hvorfor unsupported istedenfor "false"?

	}
}
