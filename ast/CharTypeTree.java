package ast;

import visitor.ASTVisitor;

public class CharTypeTree extends AST {

  public CharTypeTree() {

  }

  @Override
  public Object accept(ASTVisitor v) {
    v.visitCharTypeTree(this);
    return null;
  }

}
