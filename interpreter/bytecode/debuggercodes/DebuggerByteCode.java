package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.bytecode.ByteCode;
import interpreter.debugger.DebuggerVirtualMachine;

public abstract class DebuggerByteCode extends ByteCode {

  @Override
  public void execute( VirtualMachine vm ) {
  }

  public abstract void execute( DebuggerVirtualMachine vm );

}
