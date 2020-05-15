package interpreter;

import interpreter.bytecode.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

@SuppressWarnings("all")
public class CodeTable {

  static HashMap<String,Class> codeTable = new HashMap<>();

  static {
    codeTable.put( "ARGS", Args.class );
    codeTable.put( "BOP", Bop.class );
    codeTable.put( "CALL", Call.class );
    codeTable.put( "DUMP", Dump.class );
    codeTable.put( "FALSEBRANCH", FalseBranch.class );
    codeTable.put( "GOTO", GoTo.class );
    codeTable.put( "HALT", Halt.class );
    codeTable.put( "LABEL", Label.class );
    codeTable.put( "LIT", Lit.class );
    codeTable.put( "LOAD", Load.class );
    codeTable.put( "POP", Pop.class );
    codeTable.put( "PUSH", Push.class );
    codeTable.put( "READ", Read.class );
    codeTable.put( "RETURN", Return.class );
    codeTable.put( "STORE", Store.class );
    codeTable.put( "WRITE", Write.class );
  }

  public static String get( String code ) {
    return codeTable.get( code ).getName();
    //    try {
//      Class<?> byteCodeType = codeTable.get( code );
//      Constructor<?> c = byteCodeType.getConstructor();
//      return ( ByteCode ) c.newInstance();
//    } catch ( NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
//      System.out.println( String.format( "No class file found for ByteCode %s. Recompile?", code ) );
//    }
//    return null;
  }

}