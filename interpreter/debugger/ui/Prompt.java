package interpreter.debugger.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

public class Prompt {

  private DebuggerShell shell;

  public Prompt( DebuggerShell shell ) {
    this.shell = shell;
  }

  private void printPrompt() {
    System.out.println("Type ? for help");
    System.out.print(">");
  }

  public DebuggerCommand execute( ) {
    String commandString = "";
    Vector<String> args = new Vector<>();
    StringTokenizer tokenizer;
    while( !CommandTable.getCommands().containsKey( commandString ) ) {
      printPrompt();
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine().toLowerCase();
        tokenizer = new StringTokenizer( input );
        commandString = tokenizer.nextToken();
        while( tokenizer.hasMoreTokens() ) {
          args.add( tokenizer.nextToken() );
        }
      } catch ( IOException | NoSuchElementException e ) {
        /* Do nothing */
      }
    }
    DebuggerCommand command = CommandTable.getCommands().get( commandString );
    command.init( args );
    return command;
  }

}