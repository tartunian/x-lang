package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Halt extends DebuggerByteCode {

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    System.exit( 0 );
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
  }

}