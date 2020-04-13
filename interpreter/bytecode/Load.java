package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Load extends ByteCode {

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

}