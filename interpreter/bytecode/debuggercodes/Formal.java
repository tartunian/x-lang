package interpreter.bytecode.debuggercodes;

import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Formal extends DebuggerByteCode {

  private String varName = "";
  private int offset;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    varName = args.get( 1 );
    offset = Integer.parseInt( args.get( 2 ) );
  }

  @Override
  public void execute( DebuggerVirtualMachine vm ) {
    vm.getDebugger().getCurrentFunctionEnvironmentRecord().enter( varName, (Integer) vm.loadStack( offset ) );
  }

}