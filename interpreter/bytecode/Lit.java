package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class Lit extends ByteCode {

  Object value;

  private void parseValue( String rawValue ) {
    try {
      value = Integer.parseInt( rawValue );
      return;
    } catch ( Exception e ) {
    }
    try {
      String temp = rawValue.substring(1,rawValue.length()-1);
      if( temp.length() == 1 ) {
        value = temp.charAt( 0 );
      } else {
        value = temp;
      }
      return;
    } catch ( Exception e ) {
    }
  }

  @Override
  public void init( Vector<String> args ) {
    super.init( args );
    String rawValue = args.get( 1 );

    parseValue( rawValue );

    if( args.size() > 2 ) {
      String type = value.getClass().getSimpleName();
      javaCode = String.format("%s %s", "int", args.get(2));
    }
  }

  @Override
  public void execute( VirtualMachine vm ) {
    vm.pushStack( value );
  }

}