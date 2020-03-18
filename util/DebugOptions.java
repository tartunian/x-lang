package util;

import java.util.ArrayList;
import java.util.Collections;

public class DebugOptions {

    public enum Options { SOURCECODE, TOKENS, AST, BYTECODE }
    private ArrayList<Options> options;

    public boolean contains( Options option ) {
        return options.contains( option );
    }

    public DebugOptions( Options... options ) {
        this.options = new ArrayList<Options>();
        Collections.addAll( this.options, options );
    }

}