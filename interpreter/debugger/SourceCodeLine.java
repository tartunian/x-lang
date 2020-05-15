package interpreter.debugger;

public class SourceCodeLine {

  private String sourceCode = "";
  private int lineNumber;
  private Boolean isBreakpointLine = false;

  public SourceCodeLine(String sourceLine, int lineNumber, Boolean isBreakpointLine ) {
    this.sourceCode = sourceLine;
    this.lineNumber = lineNumber;
    this.isBreakpointLine = isBreakpointLine;
  }

  public String getSourceCode() {
    return sourceCode;
  }

  public int getLineNumber() { return lineNumber; }

  public Boolean getHasBreakpoint() {
    return isBreakpointLine;
  }

  public void setHasBreakpoint( Boolean isBreakpointLine ) {
    this.isBreakpointLine = isBreakpointLine;
  }

  public int getNumberOfLeadingWhitespaces() {
    char c;
    int counter = 0;
    while( Character.isWhitespace( c = sourceCode.charAt( counter ) ) ) {
      counter++;
    }
    return counter;
  }

}