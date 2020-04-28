package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Write extends ByteCode {

  @Override
  public void init(Vector<String> args) {
    super.init( args );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    Object value = vm.popStack();
    System.out.println( value );
  }

}