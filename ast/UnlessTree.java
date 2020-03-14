package ast;

import visitor.ASTVisitor;

public class UnlessTree extends AST {

  @Override
  public Object accept(ASTVisitor v) {
    return v.visitUnlessTree( this );
  }

}
