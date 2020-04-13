package interpreter;

import java.io.*;
import java.util.Vector;

public class Interpreter {

  ByteCodeLoader byteCodeLoader;

  public Interpreter( String codeFile ) {
    try {
      byteCodeLoader = new ByteCodeLoader( codeFile );
    } catch (IOException e) {
      System.out.println("**** " + e);
    }
  }

  void run() throws IOException {
    Program program = byteCodeLoader.loadCodes();
    VirtualMachine vm = new VirtualMachine(program);
    vm.executeProgram();
  }

  public static void main( String args[] ) throws IOException {
    if ( args.length == 0 ) {
      System.out.println("***Incorrect usage, try: java interpreter.Interpreter <file>");
      System.exit( 1 );
    }
    ( new Interpreter( args[0] ) ).run();
  }
}