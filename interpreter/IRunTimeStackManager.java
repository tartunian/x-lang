package interpreter;

public interface IRunTimeStackManager {

    public Integer loadStack( int offset );
    public void newStackFrameAt( int offset );
    public Integer peekStack();
    public Integer popStack();
    public void popStackFrame();
    public Integer pushStack( int value );
    public Integer storeStack( int offset );

}