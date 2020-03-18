package ast;

import lexer.Symbol;
import lexer.Token;
import visitor.*;

public class MultiplicationOperationTree extends AST {
    private Symbol symbol;

/**
 *  @param tok contains the Symbol that indicates the specific multiplying operator
*/
    public MultiplicationOperationTree(Token tok) {
        this.symbol = tok.getSymbol();
    }

    public Object accept(ASTVisitor v) {
        return v.visitMultiplicationOperationTree(this);
    }

    public Symbol getSymbol() {
        return symbol;
    }

}
