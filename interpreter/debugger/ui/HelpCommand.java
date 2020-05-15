package interpreter.debugger.ui;

import interpreter.debugger.Debugger;

public class HelpCommand extends DebuggerCommand {

  @Override
  public void execute( Debugger debugger ) {
    debugger.displayCommandList();
  }

}
