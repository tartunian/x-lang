package interpreter.debugger.ui;

import interpreter.debugger.Debugger;

import java.util.Vector;

public abstract class DebuggerCommand {

  public void init( Vector<String> args ) {
    /* Do nothing */
  }

  public abstract void execute( Debugger debugger );

}
