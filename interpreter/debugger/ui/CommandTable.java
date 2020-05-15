package interpreter.debugger.ui;

import java.util.HashMap;

public class CommandTable {

  private static HashMap<String,DebuggerCommand> commands = new HashMap<>();

  static {
    commands.put( "currentline",      new PrintCurrentLineNumberCommand() );
    commands.put( "displaylocals",    new DisplayLocalsCommand() );
    commands.put( "displaysource",    new DisplaySourceCommand() );
    commands.put( "end",              new ExitCommand() );
    commands.put( "exit",             new ExitCommand() );
    commands.put( "help",             new HelpCommand() );
    commands.put( "listbreakpoints",  new ListBreakpointsCommand() );
    commands.put( "setbreakpoint",    new SetBreakpointCommand() );
    commands.put( "step",             new StepCommand() );
    commands.put( "?",                new HelpCommand() );
  }

  public static HashMap<String, DebuggerCommand> getCommands() { return commands; }

}
