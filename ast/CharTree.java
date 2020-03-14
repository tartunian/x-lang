package ast;

import lexer.Symbol;
import lexer.Token;
import visitor.ASTVisitor;

public class CharTree extends AST {

  private Symbol symbol;

  public CharTree( Token token ) {
    this.symbol = token.getSymbol();
  }

  public Object accept(ASTVisitor v) {
    return v.visitCharTree(this);
  }

  public Symbol getSymbol() {
    return symbol;
  }

}
