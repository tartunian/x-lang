package interpreter.debugger.ui;

import interpreter.debugger.Debugger;

public class ListBreakpointsCommand extends DebuggerCommand {

  @Override
  public void execute( Debugger debugger ) {
    debugger.listBreakpoints();
  }

}
