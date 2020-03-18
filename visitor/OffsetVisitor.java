package visitor;

import ast.AST;

import java.util.HashMap;

public class OffsetVisitor extends ASTVisitor {

  private int depth = 0;
  int[] nextAvailableOffset = new int[ 100 ];
  private HashMap<AST,Offset> offsetHashMap = new HashMap<>();

  public HashMap<AST,Offset> getOffsetHashMap() {
    return offsetHashMap;
  }

  private void shiftChildren( AST t, int offsetShift ) {
    depth++;
    for(AST child : t.getChildren() ) {
      if( child.getChildCount() > 0 ) {
        shiftChildren( child, offsetShift );
      }
      Offset offset = offsetHashMap.get( child );
      offset.shiftOffset( offsetShift );
      offsetHashMap.put( child, offset );
      nextAvailableOffset[ depth ] = offset.getValue() + 2;
    }
    depth--;
  }

  private String getPad() {
    String pad = "";
    for(int i=0;i<=depth;i++) {
      pad+="|  ";
    }
    return pad;
  }

  private void dbg( String msg ) {
    System.out.println( String.format( "%s%s", getPad(), msg ) );
  }

  private void generateOffsetHashMap(AST t ) {
    Offset offset;
    if( t.getChildCount() > 0 ) {

      depth++;
      visitChildren( t );
      depth--;

      AST rightChild = t.getChild( t.getChildCount() - 1 );
      AST leftChild = t.getChild( 0 );

      Offset rightChildOffset = offsetHashMap.get( rightChild );
      Offset leftChildOffset = offsetHashMap.get( leftChild );

      int childrenOffsetDifference = rightChildOffset.getValue() + leftChildOffset.getValue();
      int calculatedOffsetValue = childrenOffsetDifference / 2;
      int offsetValue = calculatedOffsetValue;

      if( calculatedOffsetValue < nextAvailableOffset[ depth ] ) {
        int childOffsetShift = nextAvailableOffset[ depth ] - calculatedOffsetValue;
        shiftChildren( t, childOffsetShift );
        offsetValue = nextAvailableOffset[ depth ];
      }

      offset = new Offset( depth, offsetValue );
      offsetHashMap.put( t, offset );
      nextAvailableOffset[ depth ] = offsetValue + 2;

    } else {

      offset = new Offset( depth, nextAvailableOffset[ depth ] );
      offsetHashMap.put( t, offset );
      nextAvailableOffset[ depth ] += 2;
    }
  }
  
  @Override
  public Object visitProgramTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitBlockTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitFunctionDeclarationTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitCallTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitDeclarationTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitIntTypeTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitBoolTypeTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitStringTypeTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitCharTypeTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitStringTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitCharTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitFormalsTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitActualArgumentsTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitIfTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitUnlessTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitWhileTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitReturnTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitSwitchBlockTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitSwitchStatementTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitCaseStatementTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitDefaultStatementTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitAssignTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitIntTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitIdentifierTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitRelationalOperationTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitAdditionOperationTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }

  @Override
  public Object visitMultiplicationOperationTree(AST t) {
    generateOffsetHashMap( t );
    return null;
  }
}
