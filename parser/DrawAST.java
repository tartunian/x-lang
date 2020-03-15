package parser;

import ast.AST;
import visitor.CountVisitor;
import visitor.DrawVisitor;

import javax.imageio.ImageIO;
import java.io.File;

public class DrawParseTree {

  public static void main( String[] args ) {
    String sourceFile = args[0];
    Parser parser;
    CountVisitor countVisitor = new CountVisitor();

    try {
      parser = new Parser(sourceFile);
      AST tree = parser.execute();
      countVisitor.visitProgramTree( tree );
      int[] treeDepthCount = countVisitor.getCount();
      DrawVisitor drawVisitor = new DrawVisitor( treeDepthCount );
      drawVisitor.draw( "Program", tree );

      String name = String.format( "%s_ASTRender.png", sourceFile.substring( sourceFile.indexOf('\\')+1, sourceFile.length()) );
      System.out.println( name );

      File render = new File( name );
      ImageIO.write( drawVisitor.getImage(), "png", render );

    } catch ( Exception e ) {
      System.out.println( e );
    }

  }

}
