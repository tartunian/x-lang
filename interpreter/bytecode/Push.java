package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Push extends ByteCode {

  int value;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    value = Integer.parseInt( args.get( 1 ) );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.pushStack( value );
  }
}
