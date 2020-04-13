package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class GoTo extends ByteCode {

  String targetLabel;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    targetLabel = args.get( 1 );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    int jumpAddress = vm.getJumpAddress( targetLabel );
    vm.setProgramCounter( jumpAddress );
    //System.out.println( String.format( "Set program counter to %d. Value is %d", jumpAddress, vm.getProgramCounter() ) );
  }

}