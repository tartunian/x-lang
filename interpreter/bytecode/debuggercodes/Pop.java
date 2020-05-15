package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Pop extends DebuggerByteCode {

  int n = 0;

  @Override
  public void init(Vector<String> args) {
    super.init( args );
    n = Integer.parseInt( args.get( 1 ) );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    for( int i=0;i<n;i++ ) {
      vm.popStack();
    }
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
  }

}