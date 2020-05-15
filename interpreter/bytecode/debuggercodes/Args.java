package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;
import interpreter.debugger.ui.DebuggerCommand;

import java.util.Vector;

public class Args extends DebuggerByteCode {

  int numArgs;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    numArgs = Integer.parseInt( args.get( 1 ) );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.newStackFrameAt( numArgs );
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
  }

}