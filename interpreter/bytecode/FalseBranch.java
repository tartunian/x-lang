package interpreter.bytecode;

import interpreter.VirtualMachine;

import java.util.Vector;

public class FalseBranch extends ByteCode {

    String targetLabel;

    @Override
    public void init( Vector<String> args ) {
        super.init( args );
        targetLabel = args.get( 1 );
    }

    @Override
    public void execute( VirtualMachine vm ) {
        int val = vm.popStack();
        boolean b = (val==0) ? false : true;
        if(!b) {
            int jumpAddress = vm.getJumpAddress( targetLabel );
            vm.setProgramCounter( jumpAddress );
        }
    }

}