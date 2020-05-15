package interpreter.debugger.ui;

import interpreter.debugger.Debugger;

public class PrintCurrentLineNumberCommand extends DebuggerCommand {

  @Override
  public void execute( Debugger debugger ) {
    debugger.displayCurrentLineNumber();
  }

}
