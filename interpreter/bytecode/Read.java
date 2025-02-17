package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Read extends ByteCode {

  @Override
  public void init(Vector<String> args) {
    super.init( args );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    Integer input = null;
    while ( input == null ) {
      try {
        System.out.print("Please enter a number: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        input = Integer.parseInt( reader.readLine() );
      } catch (IOException | NumberFormatException e) {
        /* Do nothing */
      }
    }
    vm.pushStack( input );
  }

}