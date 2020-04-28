package interpreter;

public interface IRunTimeStackManager {

    public Object loadStack( int offset );
    public void newStackFrameAt( int offset );
    public Object peekStack();
    public Object popStack();
    public void popStackFrame();
    public Object pushStack( Object value );
    public Object storeStack( int offset );

}