package constrain;

public class ConstraintException extends Exception {

  private ConstraintExceptionType type;

  public ConstraintException( ConstraintExceptionType type ) {
    this.type = type;
  }

}
