package parser;

import ast.AST;
import visitor.CountVisitor;
import visitor.DrawOffsetVisitor;
import visitor.OffsetVisitor;
import util.DebugOptions;
import util.DebugOptions.Options;

import javax.imageio.ImageIO;

import java.awt.*;
import java.io.File;

public class DrawOffsetAST {

  public static void main( String[] args ) {
    String sourceFile = args[0];
    Parser parser;
    CountVisitor countVisitor = new CountVisitor();
    OffsetVisitor offsetVisitor = new OffsetVisitor();

    try {
      parser = new Parser( sourceFile, new DebugOptions( Options.TOKENS, Options.AST ) );
      AST tree = parser.execute();
      countVisitor.visitProgramTree( tree );
      int[] treeDepthCount = countVisitor.getCount();
      offsetVisitor.visitProgramTree( tree );

      DrawOffsetVisitor drawOffsetVisitor = new DrawOffsetVisitor( treeDepthCount, offsetVisitor.getOffsetHashMap() );
      drawOffsetVisitor.visitProgramTree( tree );

      String name = String.format( "%s_ASTRender.png", sourceFile.substring( sourceFile.indexOf('\\') + 1 ) );

      File renderFile = new File( name );
      ImageIO.write( drawOffsetVisitor.getImage(), "png", renderFile );

      Desktop.getDesktop().open( renderFile );

    } catch ( Exception e ) {
      System.out.println( e );
    }

  }

}
