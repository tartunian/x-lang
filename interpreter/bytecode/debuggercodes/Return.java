package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Return extends DebuggerByteCode {

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    javaCode = String.format( "exit" );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.popStackFrame();
    int returnAddress = vm.popReturnAddress();
    vm.setProgramCounter( returnAddress );
  }

  @Override
  public void execute( DebuggerVirtualMachine vm ) {
    execute( (VirtualMachine) vm );
    vm.getDebugger().exitScope();
  }

}