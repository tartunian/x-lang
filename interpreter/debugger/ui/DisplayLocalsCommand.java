package interpreter.debugger.ui;

import interpreter.debugger.Debugger;

import java.util.Vector;

public class DisplayLocalsCommand extends DebuggerCommand {

  @Override
  public void execute( Debugger debugger ) {
    debugger.displayLocals();
  }

}
