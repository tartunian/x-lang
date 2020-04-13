package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.io.IOException;
import java.util.Vector;

public class Read extends ByteCode {

  @Override
  public void init(Vector<String> args) {
    super.init( args );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    try {
      System.out.print( "Please enter a number: " );
      int valueIn = System.in.read();
      vm.pushStack( valueIn );
    } catch ( IOException e ) { };
  }

}