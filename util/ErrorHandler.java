package util;

public class ErrorHandler {

  private static ErrorHandler instance;
  // private static String queue = "";

  static {
    instance = new ErrorHandler();
  }

  public static ErrorHandler getInstance() {
    return instance;
  }

  public void logError( String message ) {
    System.out.println( message );
  }

  public void logError( String errorType, String message ) {
    String m = String.format( "%-20s %s", errorType, message );
    logError( m );
  }

  public void logError( String errorType, int lineNumber, String message ) {
    String m = String.format( "line: %-4d %-20s %s", lineNumber, errorType, message );
    logError( m );
  }

  public void logError( String errorType, int lineNumber, int columnNumber, String message ) {
    String m = String.format( "line: %-4d column: %-4d %-20s %s", lineNumber, columnNumber, errorType, message );
    logError( m );
  }


}
