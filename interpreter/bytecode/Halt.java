package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Halt extends ByteCode {

  @Override
  public void init(Vector<String> args) {
    super.init( args );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    System.exit( 0 );
  }

}