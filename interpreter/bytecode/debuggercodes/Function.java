package interpreter.bytecode.debuggercodes;

import interpreter.debugger.DebuggerVirtualMachine;
import interpreter.debugger.FunctionEnvironmentRecord;

import java.util.Vector;

public class Function extends DebuggerByteCode {

  private String functionName = "";
  private int functionStartingLineNumber;
  private int functionEndingLineNumber;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    functionName = args.get( 1 );
    functionStartingLineNumber = Integer.parseInt( args.get( 2 ) );
    functionEndingLineNumber = Integer.parseInt( args.get( 3 ) );
  }

  @Override
  public void execute( DebuggerVirtualMachine vm ) {
    FunctionEnvironmentRecord functionEnvironmentRecord = new FunctionEnvironmentRecord();
    functionEnvironmentRecord.setFunctionInfo( functionName, functionStartingLineNumber, functionEndingLineNumber );
    functionEnvironmentRecord.setCurrentLineNumber( functionStartingLineNumber );
    vm.getDebugger().enterScope( functionEnvironmentRecord );
  }

}
