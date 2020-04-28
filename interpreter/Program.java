package interpreter;

import interpreter.bytecode.ByteCode;
import interpreter.bytecode.Label;

import java.util.HashMap;
import java.util.Vector;

public class Program {

  private Vector<ByteCode> codes = new Vector<>();
  private HashMap<String,Integer> jumpLabels = new HashMap<>();

  public void addByteCode( ByteCode code, Vector<String> args ) {
    code.init( args );
    codes.add( code );
    if( code instanceof Label ) {
      jumpLabels.put( args.get( 1 ), codes.size() - 1 );
    }
  }

  public int getJumpAddress( String label ) {
    String newLabel = "";
    if( jumpLabels.containsKey( label ) ) {
      newLabel = label;
    } else {
      newLabel = label.substring( 0,label.lastIndexOf('_') + 1 ) + "default>>";
    }
    Integer jumpAddress = jumpLabels.get( newLabel );
    return jumpAddress;
  }

  public ByteCode getCode( int programCounter ) {
    ByteCode code = codes.elementAt( programCounter );
    return codes.elementAt( programCounter );
  }

}