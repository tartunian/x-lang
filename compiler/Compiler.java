package compiler;

import ast.*;
import lexer.LexicalException;
import parser.Parser;
import parser.SyntaxException;
import util.DebugOptions;
import util.DebugOptions.Options;
import constrain.Constrainer;
import codegen.*;
import visitor.*;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 * The Compiler class contains the main program for compiling a source program
 * to bytecodes
 */
public class Compiler {

  /**
   * The Compiler class reads and compiles a source program
   */

  String sourceFile;

  public Compiler(String sourceFile) {
    this.sourceFile = sourceFile;
  }

  void compileProgram() {
    try {
      Parser parser = new Parser(sourceFile, new DebugOptions( Options.SOURCECODE, Options.AST ) );
      AST t = parser.execute();
      //System.out.println("---------------AST-------------");
      PrintVisitor pv = new PrintVisitor();
      //t.accept(pv);
      /* COMMENT CODE FROM HERE UNTIL THE CATCH CLAUSE WHEN TESTING PARSER */
      Constrainer con = new Constrainer(t, parser);
      con.execute();
      System.out.println("---------------DECORATED AST-------------");
      t.accept(pv);
      /* COMMENT CODE FROM HERE UNTIL THE CATCH CLAUSE WHEN TESTING CONSTRAINER */
      Codegen generator = new Codegen(t);
      Program program = generator.execute();
      System.out.println("---------------AST AFTER CODEGEN-------------");
      t.accept(pv);
      System.out.println("---------------INTRINSIC TREES-------------");
      System.out.println("---------------READ/WRITE TREES-------------");
      Constrainer.readTree.accept(pv);
      Constrainer.writeTree.accept(pv);
      System.out.println("---------------INT/BOOL TREES-------------");
      Constrainer.intTree.accept(pv);
      Constrainer.boolTree.accept(pv);
      System.out.println();
      System.out.println("---------------BYTE CODE-------------");
      program.printCodes(sourceFile + ".cod");
      // if the source file is "abc" print bytecodes to abc.cod
      } catch ( IOException | LexicalException | SyntaxException e ) {
        System.out.println( e );
      };
  }

  public static void main(String args[]) throws Exception {
    if (args.length == 0) {
      System.out.println("***Incorrect usage, try: java compiler.Compiler <file>");
      System.exit(1);
    }
    (new Compiler(args[0])).compileProgram();
  }
}
