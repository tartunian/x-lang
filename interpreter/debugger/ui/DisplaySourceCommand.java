package interpreter.debugger.ui;

import interpreter.debugger.Debugger;

public class DisplaySourceCommand extends DebuggerCommand {

  @Override
  public void execute( Debugger debugger ) {
    debugger.displaySource();
  }

}
