package ast;

import lexer.Symbol;
import lexer.Token;
import visitor.*;

public class RelationalOperationTree extends AST {
    private Symbol symbol;

/**
 *  @param tok contains the Symbol which indicates the specific relational operator
*/
    public RelationalOperationTree(Token tok) {
        this.symbol = tok.getSymbol();
    }

    public Object accept(ASTVisitor v) {
        return v.visitRelationalOperationTree(this);
    }

    public Symbol getSymbol() {
        return symbol;
    }

}

