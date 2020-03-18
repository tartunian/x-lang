package visitor;

import ast.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author Lowell Milliken
 */
public class DrawOffsetVisitor extends ASTVisitor {

  private final int nodew = 100;
  private final int nodeh = 30;
  private final int vertSep = 50;
  private final int horizSep = 10;

  private int width;
  private int height;

  private HashMap<AST,Offset> offsetHashMap;

  private int [] nCount;
  private int [] progress;
  private int depth = 0;
  private BufferedImage bimg;
  private Graphics2D g2;

  public DrawOffsetVisitor( int[] nCount, HashMap<AST,Offset> offsetHashMap ) {
    this.nCount = nCount;
    this.offsetHashMap = offsetHashMap;
    progress = new int[ nCount.length ];

    Entry<AST, Offset> maxEntry = Collections.max(
      offsetHashMap.entrySet(),
      ( Entry<AST, Offset> e1, Entry<AST, Offset> e2 ) ->
      Integer.compare( e1.getValue().getValue(), e2.getValue().getValue() ) );
    width = ( maxEntry.getValue().getValue() + 2 ) * ( nodew + horizSep ) / 2 ;
    height = nCount.length * ( nodeh + vertSep );

    g2 = createGraphics2D( width, height );
    g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
  }

  private int max( int [] array ) {
    int max = array[ 0 ];

    for( int i = 1; i < array.length; i++ )
      if( max < array[i] ) {
        max = array[ i ];
      }

    return max;
  }

  public void draw( String s, AST t ) {
    
    int hstep = nodew + horizSep;
    int vstep = nodeh + vertSep;

    //int x = width / 2 + progress[ depth ] * hstep - nCount[ depth ] * hstep / 2;
    Offset offset = offsetHashMap.get( t );
    int x = offset.getValue() * hstep / 2;
    int y = depth * vstep;

    // g2.setColor( Color.RED );
    // g2.drawRect( x, y, hstep, vstep );
    // g2.drawRect( x, y, nodew, nodeh );

    g2.setColor( Color.black );
    g2.drawOval( x, y, nodew, nodeh );
    g2.setColor( Color.BLACK );
    g2.drawString( String.format( "%s", s ), x + 10, y + 2 * nodeh / 3 );

    int startx = x + nodew / 2;
    int starty = y + nodeh;
    int endx;
    int endy;
    g2.setColor( Color.black );

    // for( int i = 0; i < t.getChildCount(); i++ ) {
    //   endx = width / 2 + ( progress[ depth + 1 ] + i ) * hstep - nCount[ depth + 1 ] * hstep / 2 + nodew / 2;
    //   endy = ( depth + 1 ) * vstep;
    //   g2.drawLine( startx, starty, endx, endy );
    // }

    for( AST child : t.getChildren() ) {
      Offset childOffset = offsetHashMap.get( child );
      endx = ( childOffset.getValue() + 1 ) * hstep / 2 - horizSep / 2;
      endy = ( depth + 1 ) * vstep;
      g2.drawLine( startx, starty, endx, endy );
    }

    progress[ depth ]++;
    depth++;
    visitChildren( t );
    depth--;
  }

  private Graphics2D createGraphics2D( int w, int h ) {
    Graphics2D g2;

    if( bimg == null || bimg.getWidth() != w || bimg.getHeight() != h ) {
      bimg = new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB );
    }

    g2 = bimg.createGraphics();
    g2.setBackground( Color.WHITE );
    g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
    g2.clearRect( 0, 0, w, h );
    return g2;
  }

  public BufferedImage getImage() {
    return bimg;
  }

  public Object visitProgramTree( AST t ) {
    draw( "Program", t );
    return null;
  }

  public Object visitBlockTree( AST t ) {
    draw( "Block", t );
    return null;
  }

  public Object visitFunctionDeclarationTree(AST t ) {
    draw( "FunctionDecl", t );
    return null;
  }

  public Object visitCallTree( AST t ) {
    draw( "Call", t );
    return null;
  }

  public Object visitDeclarationTree(AST t ) {
    draw( "Decl", t );
    return null;
  }

  public Object visitIntTypeTree( AST t ) {
    draw( "IntType", t );
    return null;
  }

  @Override
  public Object visitBoolTypeTree( AST t ) {
    draw( "BoolType", t );
    return null;
  }

  @Override
  public Object visitStringTypeTree(AST t) {
    draw( "StringType", t );
    return null;
  }

  @Override
  public Object visitCharTypeTree(AST t) {
    draw( "CharType", t );
    return null;
  }

  @Override
  public Object visitStringTree(AST t) {
    draw( "StringType", t );
    return null;
  }

  @Override
  public Object visitCharTree(AST t) {
    draw( "Char", t );
    return null;
  }

  @Override
  public Object visitFormalsTree( AST t ) {
    draw( "Formals", t );
    return null;
  }

  @Override
  public Object visitActualArgumentsTree(AST t ) {
    draw( "ActualArgs", t );
    return null;
  }

  @Override
  public Object visitIfTree( AST t ) {
    draw( "If", t );
    return null;
  }

  @Override
  public Object visitUnlessTree( AST t ) {
    draw( "Unless", t );
    return null;
  }

  @Override
  public Object visitWhileTree( AST t ) {
    draw( "While", t );
    return null;
  }

  @Override
  public Object visitReturnTree(AST t) {
    draw( "While", t );
    return null;
  }

  @Override
  public Object visitSwitchBlockTree(AST t) {
    draw( "SwitchBlock", t );
    return null;
  }

  @Override
  public Object visitSwitchStatementTree(AST t) {
    draw( "SwitchStatement", t );
    return null;
  }

  @Override
  public Object visitCaseStatementTree(AST t) {
    draw( "CaseStatement", t );
    return null;
  }

  @Override
  public Object visitDefaultStatementTree(AST t) {
    draw( "DefaultStatement", t );
    return null;
  }

  @Override
  public Object visitAssignTree( AST t ) {
    draw( "Assign", t );
    return null;
  }

  @Override
  public Object visitIntTree( AST t ) {
    draw( "Int: " + ( ( IntTree ) t ).getSymbol().toString(), t );
    return null;
  }

  @Override
  public Object visitIdentifierTree( AST t ) {
    draw( "Id: " + ( ( IdentifierTree ) t ).getSymbol().toString(), t );
    return null;
  }

  @Override
  public Object visitRelationalOperationTree( AST t ) {
    draw( "RelOp: " + ( ( RelationalOperationTree ) t ).getSymbol().toString(), t );
    return null;
  }

  @Override
  public Object visitAdditionOperationTree( AST t ) {
    draw( "AddOp: " + ( ( AdditionOperationTree ) t ).getSymbol().toString(), t );
    return null;
  }

  @Override
  public Object visitMultiplicationOperationTree( AST t ) {
    draw( "MultOp: " + ( ( MultiplicationOperationTree ) t ).getSymbol().toString(), t );
    return null;
  }

}