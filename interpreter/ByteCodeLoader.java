package interpreter;

import interpreter.bytecode.ByteCode;
import interpreter.debugger.DebuggerCodeTable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.util.Vector;

public class ByteCodeLoader {

  FileReader fileReader;
  BufferedReader codeReader;

  public ByteCodeLoader( String byteCodeFile ) throws IOException {
    fileReader = new FileReader( byteCodeFile );
    codeReader = new BufferedReader( fileReader );
  }

  public Program loadCodes() throws IOException {
    Program program = new Program();
    String codeLine;
    StringTokenizer tokenizer;
    while( ( codeLine = codeReader.readLine() ) != null ) {
      tokenizer = new StringTokenizer( codeLine );
      ByteCode code;
      Vector<String> args = new Vector<>();
      if( tokenizer.hasMoreTokens() ) {
        String codeString = tokenizer.nextToken().toUpperCase();
        code = instantiateByteCode( codeString );
        args.add( codeLine );
        while( tokenizer.hasMoreTokens() ) {
          args.add( tokenizer.nextToken() );
        }
        program.addByteCode( code, args );
      }
    }
    return program;
  }

  private ByteCode instantiateByteCode( String code ) {
    String className = DebuggerCodeTable.get( code );
    try {
      Class<?> byteCodeType = Class.forName( className );
      Constructor<?> constructor = byteCodeType.getConstructor();
      return ( ByteCode ) constructor.newInstance();
    } catch ( ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
      System.out.println( String.format( "No class file found for ByteCode %s. Recompile?", code ) );
    }
    return null;
  }

}