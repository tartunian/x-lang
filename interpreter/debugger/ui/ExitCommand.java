package interpreter.debugger.ui;

import interpreter.debugger.Debugger;

public class ExitCommand extends DebuggerCommand {

  @Override
  public void execute( Debugger debugger ) {
    System.exit( 1 );
  }

}
