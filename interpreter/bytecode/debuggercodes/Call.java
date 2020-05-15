package interpreter.bytecode.debuggercodes;

import interpreter.VirtualMachine;
import interpreter.debugger.DebuggerVirtualMachine;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Call extends DebuggerByteCode {

  String functionLabel;

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    functionLabel = args.get( 1 );
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.pushReturnAddress( vm.getProgramCounter() );
    int jumpAddress = vm.getJumpAddress( functionLabel );
    vm.setProgramCounter( jumpAddress - 1 );
    Matcher m = Pattern.compile( "\\w+" ).matcher( functionLabel );
    m.find();
    javaCode = String.format( "%s(%s)", m.group( 0 ), vm.peekStack() );
  }

  @Override
  public void execute(DebuggerVirtualMachine vm) {
    execute( (VirtualMachine) vm );
  }

}