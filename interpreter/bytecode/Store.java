package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Store extends ByteCode {

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
    javaCode = String.format( "%s = %d", identifier, vm.peekStack() );
  }

}