package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Lit extends DebuggerByteCode {

  String identifier = "?";
  Integer value;

  private void parseValue( String rawValue ) {
    try {
      value = Integer.parseInt( rawValue );
      return;
    } catch ( Exception e ) {
    }
  }

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    String rawValue = args.get( 1 );

    parseValue( rawValue );

    if( args.size() > 2 ) {
      identifier = args.get( 2 );
      String type = value.getClass().getSimpleName();
      javaCode = String.format("%s %s", "int", identifier );
    }
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.pushStack( value );
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
    vm.getDebugger().getCurrentFunctionEnvironmentRecord().enter( identifier, (Integer) vm.peekStack() );
  }

}