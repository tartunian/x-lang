package interpreter.debugger.ui;

import interpreter.debugger.Debugger;

public class StepCommand extends DebuggerCommand {

  @Override
  public void execute( Debugger debugger ) {
    debugger.stepVM();
  }

}
