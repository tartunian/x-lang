package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Return extends ByteCode {

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.popStackFrame();
    int returnAddress = vm.popReturnAddress();
    vm.setProgramCounter( returnAddress );
  }

}