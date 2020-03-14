package ast;

import visitor.ASTVisitor;

public class SwitchBlockTree extends AST {

  @Override
  public Object accept(ASTVisitor v) {
    return v.visitSwitchBlockTree( this );
  }
}
