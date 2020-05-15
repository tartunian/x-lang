package interpreter.bytecode.debuggercodes;

import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Line extends DebuggerByteCode {

  private int lineNumber;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    lineNumber = Integer.parseInt( args.get( 1 ) );
  }

  @Override
  public void execute( DebuggerVirtualMachine vm ) {
    vm.getDebugger().setCurrentLine( lineNumber );
  }

}
