package interpreter.debugger.ui;

import interpreter.debugger.Debugger;

public class DebuggerShell {

  private Debugger debugger;
  private Prompt prompt;

  public DebuggerShell( Debugger debugger ) {
    this.debugger = debugger;
    this.prompt = new Prompt( this );
  }

  public DebuggerCommand prompt() {
    return prompt.execute();
  }

}