package interpreter;

public class RunTimeStackDriver {

    static RunTimeStack runTimeStack = new RunTimeStack();

    public static void main(String args[]) {
        System.out.println("RunTimeStackDriver");
        System.out.println("==================");
        runTimeStack.push(5);
        runTimeStack.push(0);
        runTimeStack.push(3);
        runTimeStack.push(0);
        runTimeStack.push(7);
        runTimeStack.push(9);
        runTimeStack.push(0);
        runTimeStack.push(4);
        runTimeStack.newFrameAt(5);
        System.out.println( runTimeStack );
        runTimeStack.store(3);
        System.out.println( runTimeStack );
        runTimeStack.load(2);
        System.out.println( runTimeStack );
        runTimeStack.store(1);
        System.out.println( runTimeStack );
        runTimeStack.popFrame();
        System.out.println( runTimeStack );
    }

}