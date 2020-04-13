package interpreter.bytecode;

import interpreter.VirtualMachine;
import java.util.Vector;

public class Args extends ByteCode {

    int numArgs;

    @Override
    public void init( Vector<String> args ) {
        super.init( args );
        numArgs = Integer.parseInt( args.get( 1 ) );
    }

    @Override
    public void execute( VirtualMachine vm ) {
        vm.newStackFrameAt( numArgs );
    }

}