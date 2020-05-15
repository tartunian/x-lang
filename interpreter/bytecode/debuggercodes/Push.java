package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Push extends DebuggerByteCode {

  int value;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    value = Integer.parseInt( args.get( 1 ) );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.pushStack( value );
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
  }
}
