package interpreter.debugger;

import lexer.Symbol;
import lexer.TokenType;

public class FunctionEnvironmentRecord {

  private VariableTable variableTable;
  private String functionName;
  private int functionStartingLineNumber;
  private int functionEndingLineNumber;
  private int currentLineNumber;

  public FunctionEnvironmentRecord() {
    variableTable = new VariableTable();
  }

  public void beginScope() {
    variableTable.beginScope();
  }

  public void setFunctionInfo( String functionName, int startingLineNumber, int endingLineNumber ) {
    this.functionName = functionName;
    this.functionStartingLineNumber = startingLineNumber;
    this.functionEndingLineNumber = endingLineNumber;
  }

  public String getFunctionName() {
    return functionName;
  }

  public int getFunctionStartingLineNumber() {
    return functionStartingLineNumber;
  }

  public int getFunctionEndingLineNumber() {
    return functionEndingLineNumber;
  }

  public int getCurrentLineNumber() {
    return currentLineNumber;
  }

  public void setCurrentLineNumber( int lineNumber ) {
    currentLineNumber = lineNumber;
  }

  public void enter( String symbol, int value ) {
    Symbol symbolObject = Symbol.getSymbolForKeywordString( symbol );
    if( symbolObject == null ) {
      Symbol.put( symbol, TokenType.Identifier );
      symbolObject = Symbol.getSymbolForKeywordString( symbol );
    }
    variableTable.put( symbolObject, value );
  }

  public void pop( int count ) {

  }

  public String toString() {
    String formattedVariableTable = variableTable.keys().isEmpty()?"-":variableTable.toString();
    String formattedFunctionStartingLineNumber = functionStartingLineNumber==0?"-":String.valueOf(functionStartingLineNumber);
    String formattedFunctionEndingLineNumber = functionEndingLineNumber==0?"-":String.valueOf(functionEndingLineNumber);
    String formattedFunctionName = functionName==null?"-":functionName;
    String formattedCurrentLineNumber = currentLineNumber==0?"-":String.valueOf(currentLineNumber);
    return String.format( "< %s, %s, %s, %s, %s >",
            formattedVariableTable, formattedFunctionStartingLineNumber,
            formattedFunctionName, formattedFunctionEndingLineNumber, formattedCurrentLineNumber );
  }

  /**
   * Utility method to test your implementation. The output should be:
   * (<>, -, -, -, -)
   * (<>, g, 1, 20, -)
   * (<>, g, 1, 20, 5)
   * (<a/4>, g, 1, 20, 5)
   * (<b/2, a/4>, g, 1, 20, 5)
   * (<b/2, a/4, c/7>, g, 1, 20, 5)
   * (<b/2, a/1, c/7>, g, 1, 20, 5)
   * (<b/2, a/4>, g, 1, 20, 5)
   * (<a/4>, g, 1, 20, 5)
   */
  public static void main(String[] args) {
    FunctionEnvironmentRecord record = new FunctionEnvironmentRecord();

    record.beginScope();
    System.out.println(record);

    record.setFunctionInfo("g", 1, 20);
    System.out.println(record);

    record.setCurrentLineNumber(5);
    System.out.println(record);

    record.enter("a", 4);
    System.out.println(record);

    record.enter("b", 2);
    System.out.println(record);

    record.enter("c", 7);
    System.out.println(record);

    record.enter("a", 1);
    System.out.println(record);

    record.pop(2);
    System.out.println(record);

    record.pop(1);
    System.out.println(record);
  }
}