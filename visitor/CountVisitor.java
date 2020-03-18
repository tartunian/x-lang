package visitor;

import ast.AST;

/**
 *
 * @author Lowell Milliken
 */
public class CountVisitor extends ASTVisitor {

  private int [] nCount = new int[ 100 ];
  private int depth = 0;
  private int maxDepth = 0;

  private void count( AST t ) {
    nCount[ depth ]++;

    if( depth > maxDepth ) {
      maxDepth = depth;
    }

    depth++;
    visitChildren( t );
    depth--;
  }

  public int[] getCount() {
    int [] count = new int[ maxDepth + 1 ];

    for( int i = 0; i <= maxDepth; i++ ) {
      count[ i ] = nCount[ i ];
    }

    return count;
  }

  public void printCount() {
    for( int i = 0; i <= maxDepth; i++ ) {
      System.out.println( "Depth: " + i + " Nodes: " + nCount[ i ] );
    }
  }

  @Override
  public Object visitProgramTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitBlockTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitFunctionDeclarationTree(AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitCallTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitDeclarationTree(AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitIntTypeTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitBoolTypeTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitStringTypeTree(AST t) {
    count( t );
    return null;
  }

  @Override
  public Object visitCharTypeTree(AST t) {
    count( t );
    return null;
  }

  @Override
  public Object visitStringTree(AST t) {
    count( t );
    return null;
  }

  @Override
  public Object visitCharTree(AST t) {
    count( t );
    return null;
  }

  @Override
  public Object visitFormalsTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitActualArgumentsTree(AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitIfTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitUnlessTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitWhileTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitReturnTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitSwitchBlockTree(AST t) {
    count( t );
    return null;
  }

  @Override
  public Object visitSwitchStatementTree(AST t) {
    count( t );
    return null;
  }

  @Override
  public Object visitCaseStatementTree(AST t) {
    count( t );
    return null;
  }

  @Override
  public Object visitDefaultStatementTree(AST t) {
    count( t );
    return null;
  }

  @Override
  public Object visitAssignTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitIntTree( AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitIdentifierTree(AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitRelationalOperationTree(AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitAdditionOperationTree(AST t ) {
    count( t );
    return null;
  }

  @Override
  public Object visitMultiplicationOperationTree(AST t ) {
    count( t );
    return null;
  }

}