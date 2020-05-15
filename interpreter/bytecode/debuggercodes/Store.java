package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Store extends DebuggerByteCode {

  String identifier = "<no_id>";
  int offset;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    offset = Integer.parseInt( args.get( 1 ) );
    if( args.size() > 1 ) {
      identifier = args.elementAt( 2 );
    }
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.storeStack( offset );
    javaCode = String.format( "%s = %s", identifier, vm.peekStack() );
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
    vm.getDebugger().getCurrentFunctionEnvironmentRecord().enter( identifier, (Integer) vm.peekStack() );
  }

}