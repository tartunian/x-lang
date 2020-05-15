package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;

public class Bop extends DebuggerByteCode {

  String operator;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    operator = args.get( 1 );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    int secondOperand = (int)vm.popStack();
    int firstOperand = (int)vm.popStack();
    int result = 0;
    switch( operator ) {
      case "+": {
        result = firstOperand + secondOperand;
        break;
      }
      case "-": {
        result = firstOperand - secondOperand;
        break;
      }
      case "*": {
        result = firstOperand * secondOperand;
        break;
      }
      case "/": {
        result = firstOperand / secondOperand;
        break;
      }
      case "==": {
        result = firstOperand == secondOperand ? 1 : 0;
        break;
      }
      case ">=": {
        result = firstOperand >= secondOperand ? 1 : 0;
        break;
      }
      case "<=": {
        result = firstOperand <= secondOperand ? 1 : 0;
        break;
      }
      case ">": {
        result = firstOperand > secondOperand ? 1 : 0;
        break;
      }
      case "<": {
        result = firstOperand < secondOperand ? 1 : 0;
        break;
      }
      case "|": {
        result = ( firstOperand + secondOperand ) >= 1 ? 1 : 0;
        break;
      }
      case "&": {
        result = ( firstOperand * secondOperand ) >= 1 ? 1 : 0;
        break;
      }
    }
    vm.pushStack( result );
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
  }

}