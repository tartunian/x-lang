package interpreter.debugger;

import java.util.HashMap;

import interpreter.CodeTable;
import interpreter.bytecode.debuggercodes.*;

public class DebuggerCodeTable {

  private static HashMap<String, Class> codeMap = new HashMap<>();

  static {
    codeMap.put( "ARGS", Args.class );
    codeMap.put( "BOP", Bop.class );
    codeMap.put( "CALL", Call.class );
    codeMap.put( "DUMP", Dump.class );
    codeMap.put( "FALSEBRANCH", FalseBranch.class );
    codeMap.put( "FORMAL", Formal.class );
    codeMap.put( "FUNCTION", Function.class );
    codeMap.put( "GOTO", GoTo.class );
    codeMap.put( "HALT", Halt.class );
    codeMap.put( "LABEL", Label.class );
    codeMap.put( "LINE", Line.class );
    codeMap.put( "LIT", Lit.class );
    codeMap.put( "LOAD", Load.class );
    codeMap.put( "POP", Pop.class );
    codeMap.put( "PUSH", Push.class );
    codeMap.put( "READ", Read.class );
    codeMap.put( "RETURN", Return.class );
    codeMap.put( "STORE", Store.class );
    codeMap.put( "WRITE", Write.class );
  }

  public static void init() {
  }

  public static String get( String code ) {
    String codeString = code.trim().toUpperCase();
    if( codeMap.keySet().contains( codeString ) ) {
      return codeMap.get( codeString ).getName();
    } else {
      return CodeTable.get( codeString );
    }
  }

}