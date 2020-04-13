package visitor;

import ast.*;

/**
 *  PrintVisitor is used to visit an AST and print it using
 *  appropriate indentation:<br>
 *  <pre>
 *  1. root
 *  2.   Kid1
 *  3.   Kid2
 *  4.     Kid21
 *  5.     Kid22
 *  6.     Kid23
 *  7.   Kid3
 *  </pre>
 */
public class PrintVisitor extends ASTVisitor {
  private int indent = 0;

  private String getRepeatedChar( char c, int n ) {
    String s = "";
    for (int i=0;i<n; i++) {
      s += c;
    }
    //System.out.print( s );
    return s;
  }

  /**
   *  Print the tree
   *  @param s is the String for the root of t
   *  @param t is the tree to print - print the information
   *  in the node at the root (e.g. decoration) and its kids
   *  indented appropriately
   */
  public void print( String s, AST t ) {
    // assume less than 1000 nodes; no problem for csc 413
    int num = t.getNodeNum();
    AST decoration = t.getDecoration();
    int decNum = decoration == null ? -1 : decoration.getNodeNum();

    String prefix = String.format( "%3d: ", num );
    prefix += getRepeatedChar( ' ', indent);
    s = prefix + s;

    s = decNum == -1 ? s : String.format("%-28s %s", s, String.format( "Dec: %d", decNum) );

    String label = t.getLabel();
    s = label.isEmpty() ? s : s + "  Label: " + label;

    if ( t.getClass() == IdentifierTree.class ) {
      int offset = ( (IdentifierTree) t ).getFrameOffset();
      s = offset >=0 ? s + "  Addr: " + offset : s;
    }
    String treeString = t.toString();
    treeString = treeString.substring( treeString.lastIndexOf( '@' ) );
    System.out.println( String.format( "%-48s %s", s, treeString ) );
    indent += 2;
    visitChildren( t );
    indent -= 2;
  }

  public Object visitProgramTree(AST t) { print("Program",t);  return null; }
  public Object visitBlockTree(AST t) { print("Block",t);  return null; }
  public Object visitFunctionDeclarationTree(AST t) { print("FunctionDecl",t);  return null; }
  public Object visitCallTree(AST t) { print("Call",t);  return null; }
  public Object visitDeclarationTree(AST t) { print("Decl",t);  return null; }

  // Type Trees
  public Object visitIntTypeTree(AST t) { print("IntType",t);  return null; }
  public Object visitBoolTypeTree(AST t) { print("BoolType",t);  return null; }
  public Object visitStringTypeTree(AST t) { print("StringType",t);  return null; }
  public Object visitCharTypeTree(AST t) { print("CharType",t);  return null; }

  // Literal Trees
  public Object visitStringTree(AST t) { print("String",t);  return null; }
  public Object visitCharTree(AST t) { print("Char",t);  return null; }

  public Object visitFormalsTree(AST t) { print("Formals",t);  return null; }
  public Object visitActualArgumentsTree(AST t) { print("ActualArgs",t);  return null; }
  public Object visitIfTree(AST t) { print("If",t);  return null; }
  public Object visitUnlessTree(AST t) { print("Unless",t);  return null; }
  public Object visitWhileTree(AST t) { print("While",t);  return null; }
  public Object visitReturnTree(AST t) { print("Return",t);  return null; }

  public Object visitSwitchStatementTree(AST t) { print("SwitchStatement", t ); return null; };
  public Object visitSwitchBlockTree(AST t) { print("SwitchBlock", t ); return null; };
  public Object visitCaseStatementTree(AST t) { print("CaseStatement", t ); return null; };
  public Object visitDefaultStatementTree(AST t) { print("DefaultStatement", t ); return null; };

  public Object visitAssignTree(AST t) { print("Assign",t);  return null; }
  public Object visitIntTree(AST t) { print("Int: "+((IntTree)t).getSymbol().toString(),t);  return null; }
  public Object visitIdentifierTree(AST t) { print("Id: "+((IdentifierTree)t).getSymbol().toString(),t);  return null; }
  public Object visitRelationalOperationTree(AST t) { print("RelOp: "+((RelationalOperationTree)t).getSymbol().toString(),t);  return null; }
  public Object visitAdditionOperationTree(AST t) { print("AddOp: "+((AdditionOperationTree)t).getSymbol().toString(),t);  return null; }
  public Object visitMultiplicationOperationTree(AST t) { print("MultOp: "+((MultiplicationOperationTree)t).getSymbol().toString(),t);  return null; }
}