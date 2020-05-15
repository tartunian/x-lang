package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Dump extends DebuggerByteCode {

  boolean enableDump = false;

  public void init( Vector<String> args ) {
    super.init( args );
    if( args.size() > 1 ) {
      enableDump = args.elementAt( 1 ).toLowerCase().equals("on");
    }
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.setDumpEnabled( enableDump );
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
  }
}
