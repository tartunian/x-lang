package interpreter.debugger;

import interpreter.Program;
import interpreter.VirtualMachine;
import interpreter.bytecode.ByteCode;
import interpreter.bytecode.Dump;
import interpreter.bytecode.debuggercodes.DebuggerByteCode;

public class DebuggerVirtualMachine extends VirtualMachine {

  private Debugger debugger;

  public DebuggerVirtualMachine( Program program, Debugger debugger ) {
    super( program );
    this.debugger = debugger;
  }

  public Debugger getDebugger() {
    return debugger;
  }

  public void step() {
    ByteCode code = program.getCode( pc );
    if( code instanceof DebuggerByteCode ) {
      DebuggerByteCode debuggerByteCode = (DebuggerByteCode) code;
      debuggerByteCode.execute( this );
    } else {
      code.execute( this );
    }
    if( dumpEnabled && code.getClass() != Dump.class ) {
      String sourceLine = debugger.getCurrentSourceLine();
      System.out.println( String.format( "%-40s%s", code, sourceLine ) );
      System.out.println( runTimeStack );
    }
    pc++;
  }

}