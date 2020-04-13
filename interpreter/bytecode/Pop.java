package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Pop extends ByteCode {

  int n = 0;

  @Override
  public void init(Vector<String> args) {
    super.init( args );
    n = Integer.parseInt( args.get( 1 ) );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    for( int i=0;i<n;i++ ) {
      vm.popStack();
    }
  }

}