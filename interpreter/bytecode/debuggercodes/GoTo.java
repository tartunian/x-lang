package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class GoTo extends DebuggerByteCode {

  String targetLabel;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    targetLabel = args.get( 1 );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    if( targetLabel.startsWith("switch_case") ) {
      Integer caseValue = (Integer)vm.popStack();
      targetLabel += caseValue.toString() + ">>";
    }
    int jumpAddress = vm.getJumpAddress( targetLabel );
    vm.setProgramCounter( jumpAddress );
  }

  @Override
  public void execute( DebuggerVirtualMachine vm ) {
    execute( (VirtualMachine) vm );
  }

}