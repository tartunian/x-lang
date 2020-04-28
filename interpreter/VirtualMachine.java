/**
 * DO NOT provide a method that returns components contained WITHIN the VM (this 
 * is the exact situation that will break encapsulation) - you should request 
 * that the VM performs operations on its components. This implies that the VM 
 * owns the components and is free to change them, as needed, without breaking 
 * clients' code (e.g., suppose I decide to change the name of the variable that 
 * holds my runtime stack - if your code had referenced that variable then your 
 * code would break. This is not an unusual situation - you can consider the 
 * names of methods in the Java libraries that have been deprecated).
 * 
 * Consider that the VM calls the individual ByteCodes' execute method and 
 * passes itself as a parameter. For the ByteCode to execute, it must invoke 
 * one or more methods in the runStack. It can do this by executing 
 * VM.runStack.pop(); however, this does break encapsulation. To avoid this, 
 * you'll need to have a corresponding set of methods within the VM that do 
 * nothing more than pass the call to the runStack. e.g., you would want to 
 * define a VM method:
 *     public int popRunStack() {
 *       return runStack.pop();
 *     }
 * called by, e.g.,
 *     int temp = VM.popRunStack();
 */
package interpreter;

import java.util.HashMap;
import java.util.Stack;
import interpreter.bytecode.ByteCode;
import interpreter.bytecode.Dump;

public class VirtualMachine implements IRunTimeStackManager {

  private int pc;
  private RunTimeStack runTimeStack;
  // This may not be the right parameterized type!!
  private Stack<Integer> returnAddresses;
  private boolean isRunning;
  private boolean dumpEnabled = false;
  private Program program;

  public VirtualMachine( Program program ) {
    this.program = program;
  }

  public void executeProgram() {
    pc = 0;
    runTimeStack = new RunTimeStack();
    returnAddresses = new Stack<>();
    isRunning = true;

    while ( isRunning ) {
      ByteCode code = program.getCode( pc );
      code.execute( this );
      if( dumpEnabled && code.getClass() != Dump.class ) {
        System.out.println( code );
        System.out.println( runTimeStack );
      }
      pc++;
    }
  }

  public void setDumpEnabled( boolean value ) {
    dumpEnabled = value;
  }

  public void pushReturnAddress( int address ) {
    returnAddresses.push( address );
  }

  public int popReturnAddress() {
    return returnAddresses.pop();
  }

  public int getProgramCounter() {
    return pc;
  }

  public void setProgramCounter( int pc ) {
    this.pc = pc;
  }

  public int getJumpAddress( String label ) {
    return program.getJumpAddress(label);
  }

  @Override
  public Object loadStack( int offset ) {
    return runTimeStack.load( offset );
  }

  @Override
  public void newStackFrameAt( int offset ) {
    runTimeStack.newFrameAt( offset );
  }

  @Override
  public Object peekStack() {
    return runTimeStack.peek();
  }

  @Override
  public Object popStack() {
    return runTimeStack.pop();
  }

  @Override
  public void popStackFrame() {
    runTimeStack.popFrame();
  }

  @Override
  public Object pushStack( Object item ) {
    return runTimeStack.push( item );
  }

  @Override
  public Object storeStack( int offset ) {
    return runTimeStack.store( offset );
  }

}