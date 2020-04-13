package interpreter;

import java.util.Stack;
import java.util.Vector;

public class RunTimeStack {

  private Stack<Integer> framePointers;
  // This may not be the right parameterized type!!
  private Vector<Integer> runStack;
  //private Stack<Object> runStack;
  
  // framePointers  [0      3        7  ]
  // runStack       [1 2 0][3 4 7 1][0 0]

  public RunTimeStack() {
    framePointers = new Stack<>();
    framePointers.add( 0 );
    runStack = new Vector<>();
  }

  /**
   * The purpose of this function is to dump the RunTimeStack for the 
   * purpose of debugging.
   */

  @Override
  public String toString() {
    String result = "";
    // Loop through all frame pointers
    for( int i=0; i<framePointers.size(); i++ ) {
      // Get frame start index
      int frameStart = framePointers.get( i );
      int frameEnd;
      boolean addFrameSeparator = false;
      /* Frame end index is either the end of the runStack or the
       * next frame start minus 1.
       */
      if( i == framePointers.size() - 1 ) {
        frameEnd = runStack.size() - 1;
      } else {
        frameEnd = framePointers.get( i + 1 ) - 1;
        addFrameSeparator = true;
      }
      result += '[';
      for( int j=frameStart;j<=frameEnd;j++ ) {
        result += runStack.get( j );
        if( j!= frameEnd ) {
          result += ',';
        }
      }
      result += ']';
      result += addFrameSeparator ? ' ' : "";
    }
    return result;
  }

  /**
   * Returns the top item on the runtime stack.
   */
  public int peek() {
    return runStack.lastElement();
  }

  /**
   * Pops the top item from the runtime stack, returning the item.
   */
  public int pop() {
    int result = runStack.lastElement();
    runStack.remove( runStack.size() - 1 );
    return result;
  }

  /**
   * Push an item on to the runtime stack, returning the item that was just 
   * pushed.
   */
  public int push( int item ) {
    return runStack.add( item ) ? peek() : null;
  }

  /**
   * This second form with an Integer parameter is used to load literals onto the
   * stack.
   */
  public Integer push( Integer i ) {
    return push( i );
  }

  /**
   * Start a new frame, where the parameter offset is the number of slots
   * down from the top of the RunTimeStack for starting the new frame.
   */
  public void newFrameAt( int offset ) {
    framePointers.push( runStack.size() - offset );
  }

  /**
   * We pop the top frame when we return from a function; before popping, the
   * functions’ return value is at the top of the stack so we’ll save the value,
   * pop the top frame, and then push the return value.
   */
  public void popFrame() {
    int value = runStack.lastElement();
    while( runStack.size() != framePointers.peek() ) {
      pop();
    }
    framePointers.pop();
    push( value );
  }

  /**
   * Used to store into variables.
   */
  public int store( int offset ) {
    int frameStart = framePointers.peek();
    int storeLocation = frameStart + offset;
    int value = pop();
    return runStack.set( storeLocation, value );
  }

  /**
   * Used to load variables onto the stack.
   */
  public int load( int offset ) {
    int frameStart = framePointers.peek();
    runStack.add( runStack.get( frameStart + offset ) );
    return peek();
  }
}