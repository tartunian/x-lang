package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Lit extends ByteCode {

  int value;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    value = Integer.parseInt( args.get( 1 ) );
    if( args.size() > 2 ) {
      javaCode = String.format("int %s", args.get(2));
    }
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.pushStack( value );
  }

}