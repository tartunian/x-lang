package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Label extends ByteCode {

  String labelName;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    labelName = args.get( 1 );
  }

  @Override
  public void execute( VirtualMachine vm ) {
  }

}