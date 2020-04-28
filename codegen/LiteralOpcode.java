package codegen;

/** NumOpcode class used for bytecodes with a number op field
 *  e.g. lit 5
*/
public class LiteralOpcode extends Code {
    Object value;

    public LiteralOpcode(Codes.ByteCodes code, Object n) {
        super(code);
        value = n;
    }
    
    Object getNum() {
        return value;
    }
    
    public String toString() {
        return super.toString() + " " + value.toString();
    }
    
    public void print() {
        System.out.println(toString());
    }
}
