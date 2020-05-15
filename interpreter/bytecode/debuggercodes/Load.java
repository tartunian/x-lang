package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Load extends DebuggerByteCode {

  int offset;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    offset = Integer.parseInt( args.get( 1 ) );
    javaCode = String.format( "<load %s>", args.get( 2 ) );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.loadStack( offset );
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
  }

}