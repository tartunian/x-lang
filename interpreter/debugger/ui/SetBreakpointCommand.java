package interpreter.debugger.ui;

import interpreter.debugger.Debugger;

import java.util.Vector;

public class SetBreakpointCommand extends DebuggerCommand {

  private int lineNumber;

  @Override
  public void init(Vector<String> args ) {
    lineNumber = Integer.parseInt( args.get( 0 ) );
  }

  @Override
  public void execute( Debugger debugger ) {
    debugger.setHasBreakpoint( lineNumber, true );
  }

}
