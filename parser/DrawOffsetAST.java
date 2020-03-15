package parser;

import ast.AST;
import visitor.CountVisitor;
import visitor.DrawVisitor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class DrawAST {

  public static void main( String[] args ) {
    String sourceFile = args[0];
    Parser parser;
    CountVisitor countVisitor = new CountVisitor();

    try {
      parser = new Parser( sourceFile, false );
      AST tree = parser.execute();
      countVisitor.visitProgramTree( tree );
      int[] treeDepthCount = countVisitor.getCount();
      DrawVisitor drawVisitor = new DrawVisitor( treeDepthCount );
      drawVisitor.visitProgramTree( tree );

      String name = String.format( "%s_ASTRender.png", sourceFile.substring( sourceFile.indexOf('\\') + 1 ) );

      File renderFile = new File( name );
      ImageIO.write( drawVisitor.getImage(), "png", renderFile );

      Desktop.getDesktop().open( renderFile );

    } catch ( Exception e ) {
      System.out.println( e );
    }

  }

}
