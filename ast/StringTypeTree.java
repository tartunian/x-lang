package ast;

import visitor.ASTVisitor;

public class StringTypeTree extends AST {

  public StringTypeTree() {

  }

  @Override
  public Object accept(ASTVisitor v) {
    return v.visitStringTypeTree( this );
  }
}
