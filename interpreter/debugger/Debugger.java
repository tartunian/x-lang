package interpreter.debugger;

import interpreter.Interpreter;
import interpreter.Program;
import interpreter.debugger.ui.CommandTable;
import interpreter.debugger.ui.DebuggerCommand;
import interpreter.debugger.ui.DebuggerShell;
import lexer.SourceReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.Vector;

public class Debugger extends Interpreter {

  private Stack<FunctionEnvironmentRecord> functionEnvironmentRecordStack;
  private Vector<SourceCodeLine> sourceLineProperties;
  private DebuggerVirtualMachine vm;
  private DebuggerShell shell;
  private String baseFileName;

  public Debugger( String baseFileName ) {
    super( String.format( "sample_files\\%s.x.cod", baseFileName ) );
    this.baseFileName = baseFileName;
    sourceLineProperties = new Vector<>();
    functionEnvironmentRecordStack = new Stack<>();
    this.shell = new DebuggerShell( this );
    init();
  }

  private String getSourceFileName() {
    return String.format( "sample_files\\%s.x", baseFileName );
  }

  private  String getBytecodeFileName() {
    return String.format( "%s.cod", getSourceFileName() );
  }

  private void loadSourceCode() throws IOException {
    SourceReader sourceReader = new SourceReader( getSourceFileName() );
    String sourceCodeLine;
    int lineCounter = 1;
    while ( ( sourceCodeLine = sourceReader.readLine() ) != null ) {
      sourceCodeLine = sourceCodeLine.stripTrailing();
      sourceLineProperties.add( new SourceCodeLine( sourceCodeLine, lineCounter, false ) );
      lineCounter++;
    }
  }

  private void loadProgram() {
    Program program = null;
    try {
      loadSourceCode();
      program = byteCodeLoader.loadCodes();
      vm = new DebuggerVirtualMachine( program, this );
    } catch ( IOException e ) {
      System.out.println( "Error reading code file." );
      System.exit( 1 );
    }
  }

  private void init() {
    loadProgram();
  }

  private Boolean mainFunctionInitialized() {
    return !functionEnvironmentRecordStack.isEmpty();
  }

  public void displayCurrentLineNumber() {
    if( mainFunctionInitialized() ) {
      System.out.println( getCurrentFunctionEnvironmentRecord().getCurrentLineNumber() );
    }
  }

  private void displaySourceCode() {
    for ( SourceCodeLine line : sourceLineProperties ) {
      System.out.println( String.format( "%3s: %s", line.getLineNumber(), line.getSourceCode() ) );
    }
  }

  public void displayCommandList() {
    ArrayList<String> commandSet = new ArrayList<String>(CommandTable.getCommands().keySet());
    Collections.sort( commandSet );
    for ( String commandString : commandSet ) {
      System.out.println( String.format( "  %s", commandString ) );
    }
  }

  public void displayLocals() {
    for ( FunctionEnvironmentRecord record : functionEnvironmentRecordStack ) {
      System.out.println( record );
    }
  }

  public void displaySource() {
    if( mainFunctionInitialized() ) {
      FunctionEnvironmentRecord currentFunctionEnvironmentRecord = functionEnvironmentRecordStack.peek();
      int startLine = currentFunctionEnvironmentRecord.getFunctionStartingLineNumber();
      int endLine = currentFunctionEnvironmentRecord.getFunctionEndingLineNumber();
      int currentLine = currentFunctionEnvironmentRecord.getCurrentLineNumber();
      if (currentLine < 1) {
        return;
      }
      for (int i = startLine; i <= endLine; i++) {
        String sourceLine = getSourceLine(i);
        System.out.println(String.format("%3s: %s", i, sourceLine));
        if (i == currentLine) {
          int leadingWhiteSpaces = sourceLineProperties.get(i - 1).getNumberOfLeadingWhitespaces();
          for (int j = 1; j <= 5 + leadingWhiteSpaces; j++) {
            System.out.print(' ');
          }
          System.out.println('^');
        }
      }
    }
  }

  public void enterScope( FunctionEnvironmentRecord functionEnvironmentRecord ) {
    functionEnvironmentRecordStack.push( functionEnvironmentRecord );
  }

  public void exitScope() {
    functionEnvironmentRecordStack.pop();
  }

  public FunctionEnvironmentRecord getCurrentFunctionEnvironmentRecord() {
    return functionEnvironmentRecordStack.peek();
  }

  public Boolean getHasBreakpoint( int lineNumber ) {
    if ( lineNumber < 1 ) {
      return false;
    }
    return sourceLineProperties.get( lineNumber - 1 ).getHasBreakpoint();
  }

  public Boolean getCurrentLineHasBreakpoint() {
    return getHasBreakpoint( getCurrentFunctionEnvironmentRecord().getCurrentLineNumber() );
  }

  public void setHasBreakpoint( int lineNumber, Boolean hasBreakpoint ) {
    sourceLineProperties.get( lineNumber - 1 ).setHasBreakpoint(hasBreakpoint);
  }

  public void listBreakpoints() {
    for ( int i = 0; i < sourceLineProperties.size(); i++ ) {
      SourceCodeLine lineProperties = sourceLineProperties.get(i);
      if ( lineProperties.getHasBreakpoint() ) {
        System.out.println( String.format( "%3s: %s", i + 1, lineProperties.getSourceCode() ) );
      }
    }
  }

  public void setCurrentLine( int lineNumber ) {
    if( mainFunctionInitialized() ) {
      getCurrentFunctionEnvironmentRecord().setCurrentLineNumber( lineNumber );
    }
  }

  public String getSourceLine( int lineNumber ) {
    if( lineNumber > 0 ) {
      return sourceLineProperties.get(lineNumber - 1).getSourceCode();
    } else {
      return "";
    }
  }

  public String getCurrentSourceLine() {
    if( mainFunctionInitialized() ) {
      int currentLineNumber = getCurrentFunctionEnvironmentRecord().getCurrentLineNumber();
      return getSourceLine(currentLineNumber);
    } else {
      return "";
    }
  }

  public void stepVM() {
    if( !mainFunctionInitialized() ) {
      while( !mainFunctionInitialized() ) {
        vm.step();
      }
    }
    if ( getCurrentLineHasBreakpoint() ) {
      vm.step();
    } else {
      while ( !getCurrentLineHasBreakpoint() ) {
        vm.step();
      }
    }
  }

  public void run() {
    displaySourceCode();
    while ( true ) {
      DebuggerCommand command = shell.prompt();
      command.execute(this);
    }
  }

}