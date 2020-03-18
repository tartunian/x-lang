package visitor;

public class Offset {

  private int depth;
  private int value;

  public Offset( int depth, int offset ) {
    this.depth = depth;
    this.value = offset;
  }

  public int getDepth() {
    return depth;
  }
  public int getValue() {
    return value;
  }
  public void shiftOffset( int offsetShift ) {
    this.value += offsetShift;
  }

  @Override
  public String toString() {
    return String.format( "[Depth: %d Offset: %d]", depth, value );
  }

}