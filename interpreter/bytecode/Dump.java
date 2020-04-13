package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Dump extends ByteCode {

  boolean enableDump = false;

  public void init( Vector<String> args ) {
    super.init( args );
    if( args.size() > 1 ) {
      enableDump = args.elementAt( 1 ).toLowerCase().equals("on");
    }
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.setDumpEnabled( enableDump );
  }
}
