package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Label extends DebuggerByteCode {

  String labelName;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    labelName = args.get( 1 );
  }

  @Override
  public void execute( VirtualMachine vm ) {
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
  }

}