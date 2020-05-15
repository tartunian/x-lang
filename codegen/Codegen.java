package codegen;

import constrain.*;
import visitor.*;
import java.util.*;
import ast.*;

class Frame {
  private int size = 0;
  private Stack<Block> blockSizes = new Stack<Block>();

  public Frame() {
    openBlock();
  }

  int getSize() {
    return size;
  }

  void openBlock() {
    blockSizes.push(new Block());
  }

  int closeBlock() {
    int bsize = getBlockSize();
    size -= bsize;
    blockSizes.pop();
    return bsize;
  }

  Block topBlock() {
    return (Block)blockSizes.peek();
  }

  void change(int n) {
    size += n;
    topBlock().change(n);
  }

  int getBlockSize() {
    return topBlock().getSize();
  }
}

class Block {
  int size = 0;

  void change(int n) {
    size += n;
  }

  int getSize() {
    return size;
  }
}

public class Codegen extends ASTVisitor {

  AST t;
  Stack<Frame> frameSizes;

  Program program;
  int labelNum;

  /**
   *  Create a new code generator based on the given AST
   *  @param t is the AST that will be visited
   */
  public Codegen(AST t) {
    this.t = t;
    program = new Program();
    frameSizes = new Stack<Frame>();
    labelNum = 0;
  }

  public Program execute() {
    t.accept(this);  //
    return program;
  }


  Frame topFrame() {
    if (frameSizes.empty())
      System.out.println("frames empty");
    return (Frame)frameSizes.peek();
  }

  void openFrame() {
    frameSizes.push(new Frame());
  }

  void openBlock() {  // open a new block - store local variables
    topFrame().openBlock();
  }

  void closeBlock() {
    topFrame().closeBlock();
  }

  void closeFrame() {
    frameSizes.pop();
  }

  void changeFrame(int n) {
    topFrame().change(n);
  }

  int frameSize() {  // return the current frame size
    return topFrame().getSize();
  }

  int getBlockSize() {
    return topFrame().getBlockSize();
  }

  String newLabel(String label) {  // create a new label from label
    ++labelNum;
    return label + "<<" + labelNum + ">>";
  }

  void storeop(Code code) {
    Codes.ByteCodes bytecode = code.getBytecode();
    int change = Codes.frameChange.get(bytecode);
    program.storeop(code);
    if (change == Codes.UnknownChange) {
      changeFrame( - ((NumOpcode)code).getNum());
    } else {
      changeFrame(change);
    }
  }

  void genIntrinsicCodes() {
    String readLabel = "Read",
            writeLabel = "Write";
    AST readTree = Constrainer.readTree,
            writeTree = Constrainer.writeTree;
    readTree.setLabel(readLabel);
    storeop(new LabelOpcode(Codes.ByteCodes.LABEL,readLabel));
    storeop(new Code(Codes.ByteCodes.READ));
    storeop(new Code(Codes.ByteCodes.RETURN));

    writeTree.setLabel(writeLabel);
    storeop(new LabelOpcode(Codes.ByteCodes.LABEL,writeLabel));
    String formal = ((IdentifierTree)(writeTree.getChild(2).getChild(0).getChild(1))).
            getSymbol().toString();
    storeop(new VarOpcode(Codes.ByteCodes.LOAD,0,formal));
    storeop(new Code(Codes.ByteCodes.WRITE));
    storeop(new Code(Codes.ByteCodes.RETURN));
  }

  public Object visitProgramTree(AST t) {
    String startLabel = newLabel("start");
    openFrame();
    storeop(new LabelOpcode(Codes.ByteCodes.GOTO,startLabel));
    genIntrinsicCodes();
    storeop(new LabelOpcode(Codes.ByteCodes.LABEL,startLabel));
    t.getChild(0).accept(this);
    storeop(new Code(Codes.ByteCodes.HALT));
    closeFrame();
    return null;
  }

  /** <pre>
   *  Generate codes for the Block tree:<br><br>
   *  &LT;codes for the decls and the statements in the block&GT;
   *  POP n   -- n is the number of local variables; pop them
   *  </pre>
   */
  public Object visitBlockTree(AST t) {
    openBlock();
    visitChildren(t);
    storeop(new NumOpcode(Codes.ByteCodes.POP,getBlockSize()));
    closeBlock();
    return null; }

  /** <pre>
   *  Generate codes for the function declaration; we'll also record
   *  the frame offsets for the formal parameters<br><br>
   *  GOTO continue   -- branch around codes for the function
   *  LABEL functionLabel
   *  &LT;generate codes for the function body&GT;
   *  LIT 0
   *  RETURN function
   *  LABEL continue
   *  </pre>
   */
  public Object visitFunctionDeclarationTree(AST t) {
    AST name = t.getChild(1),
            formals = t.getChild(2),
            block = t.getChild(3);
    String funcName = ((IdentifierTree)name).getSymbol().toString();
    String funcLabel = newLabel(funcName);
    t.setLabel(funcLabel);
    String continueLabel = newLabel("continue");
    storeop(new LabelOpcode(Codes.ByteCodes.GOTO,continueLabel));
    openFrame();
    storeop(new LabelOpcode(Codes.ByteCodes.LABEL,funcLabel));
    for (AST decl : formals.getChildren()) {
      IdentifierTree id = (IdentifierTree)(decl.getChild(1));
      id.setFrameOffset(frameSize());
      decl.setLabel(id.getSymbol().toString());
      changeFrame(1);
    }
    block.accept(this);
    storeop(new VarOpcode(Codes.ByteCodes.LIT,0,"   GRATIS-RETURN-VALUE"));
    storeop(new LabelOpcode(Codes.ByteCodes.RETURN,funcLabel));
    closeFrame();
    storeop(new LabelOpcode(Codes.ByteCodes.LABEL,continueLabel));
    return null;
  }

  public Object visitCallTree(AST t) {
    String funcName = ((IdentifierTree)t.getChild(0)).getDecoration().getLabel();
    int numArgs = t.getChildCount() - 1;
    for (int child = 1; child < t.getChildCount(); child++) {
      t.getChild(child).accept(this);
    }
    storeop(new NumOpcode(Codes.ByteCodes.ARGS,numArgs));
    storeop(new LabelOpcode(Codes.ByteCodes.CALL, funcName));
    return null;
  }

  public Object visitDeclarationTree(AST t) {
    IdentifierTree id = (IdentifierTree)t.getChild(1);
    String idLabel = id.getSymbol().toString();
    t.setLabel(idLabel);
    id.setFrameOffset(frameSize());
    storeop(new VarOpcode(Codes.ByteCodes.LIT,0,idLabel));
    return null;
  }

  public Object visitIntTypeTree(AST t) {
    return null; }

  public Object visitBoolTypeTree(AST t) {
    return null; }

  @Override
  public Object visitStringTypeTree(AST t) {
    System.out.println( String.format("visitStringTypeTree") );
    return null;
  }

  @Override
  public Object visitCharTypeTree(AST t) {
    System.out.println( String.format("visitCharTypeTree") );
    return null;
  }

  @Override
  public Object visitStringTree(AST t) {
    System.out.println( String.format("visitStringTree") );
    String value = ((StringTree)t).getSymbol().toString();
    storeop(new LiteralOpcode(Codes.ByteCodes.LIT,value));
    return null;
  }

  @Override
  public Object visitCharTree(AST t) {
    System.out.println( String.format("visitCharacterTree") );
    String value = ((CharTree)t).getSymbol().toString();
    storeop(new LiteralOpcode(Codes.ByteCodes.LIT,value));
    return null;
  }

  public Object visitFormalsTree(AST t) {
    return null; }

  public Object visitActualArgumentsTree(AST t) {
    return null; }

  public Object visitIfTree(AST t) {
    String elseLabel = newLabel("else"),
            continueLabel = newLabel("continue");
    t.getChild(0).accept(this);
    storeop(new LabelOpcode(Codes.ByteCodes.FALSEBRANCH,elseLabel));
    t.getChild(1).accept(this);
    storeop(new LabelOpcode(Codes.ByteCodes.GOTO,continueLabel));
    storeop(new LabelOpcode(Codes.ByteCodes.LABEL,elseLabel));
    if( t.getChildCount() > 2 ) {
      t.getChild(2).accept(this);
      storeop(new LabelOpcode(Codes.ByteCodes.LABEL, continueLabel));
    }
    return null;
  }

  @Override
  public Object visitUnlessTree(AST t) {
    return null;
  }

  public Object visitWhileTree(AST t) {
    String continueLabel = newLabel("continue"),
            whileLabel = newLabel("while");
    storeop(new LabelOpcode(Codes.ByteCodes.LABEL,whileLabel));
    t.getChild(0).accept(this);
    storeop(new LabelOpcode(Codes.ByteCodes.FALSEBRANCH,continueLabel));
    t.getChild(1).accept(this);
    storeop(new LabelOpcode(Codes.ByteCodes.GOTO,whileLabel));
    storeop(new LabelOpcode(Codes.ByteCodes.LABEL,continueLabel));
    return null;
  }

  public Object visitReturnTree(AST t) {
    t.getChild( 0 ).accept( this );
    AST fct = t.getDecoration();
    storeop(new LabelOpcode(Codes.ByteCodes.RETURN,fct.getLabel()));
    return null;
  }

  @Override
  public Object visitSwitchStatementTree(AST t) {

    IdentifierTree id = (IdentifierTree)t.getChild(0);
    id.accept( this );

    String switchGotoLabel = String.format( "switch_case<<%s_", t.hashCode() );
    storeop( new LabelOpcode( Codes.ByteCodes.GOTO, switchGotoLabel ) );

    SwitchBlockTree block = (SwitchBlockTree)t.getChild(1);
    block.setDecoration( t );
    block.accept( this );

    String switchEndLabel = String.format( "switch_end<<%s>>", t.hashCode() );
    storeop( new LabelOpcode( Codes.ByteCodes.LABEL, switchEndLabel ) );

    changeFrame( -1 );

    return null;
  }

  @Override
  public Object visitSwitchBlockTree(AST t) {
    SwitchStatementTree parent = (SwitchStatementTree)t.getDecoration();

    ArrayList<AST> caseStatements = t.getChildren();
    for ( AST caseStatement : caseStatements ) {
      caseStatement.setDecoration( parent );
      caseStatement.accept( this );
    }

    return null;
  }

  @Override
  public Object visitCaseStatementTree(AST t) {
    String switchStatementHashCode = String.valueOf( t.getDecoration().hashCode() );
    String caseValue = ((IntTree)t.getChild(0)).getSymbol().toString();
    String caseLabel = String.format( "switch_case<<%s_%s>>", switchStatementHashCode, caseValue );
    storeop( new LabelOpcode( Codes.ByteCodes.LABEL, caseLabel ) );
    t.getChild( 1 ). accept( this );
    String switchEndlabel = String.format( "switch_end<<%s>>", switchStatementHashCode );
    storeop( new LabelOpcode( Codes.ByteCodes.GOTO, switchEndlabel ) );
    return null;
  }

  @Override
  public Object visitDefaultStatementTree(AST t) {
    String switchStatementHashCode = String.valueOf( t.getDecoration().hashCode() );
    String caseLabel = String.format( "switch_case<<%s_default>>", switchStatementHashCode );
    storeop( new LabelOpcode( Codes.ByteCodes.LABEL, caseLabel ) );
    t.getChild( 0 ). accept( this );
    String switchEndlabel = String.format( "switch_end<<%s>>", switchStatementHashCode );
    storeop( new LabelOpcode( Codes.ByteCodes.GOTO, switchEndlabel ) );
    return null;
  }

  public Object visitAssignTree(AST t) {
    IdentifierTree id = (IdentifierTree)t.getChild(0);
    String vname = id.getSymbol().toString();
    int addr = ((IdentifierTree)(id.getDecoration().getChild(1))).getFrameOffset();
    t.getChild(1).accept(this);
    storeop(new VarOpcode(Codes.ByteCodes.STORE,addr,vname));
    return null;
  }

  public Object visitIntTree(AST t) {
    System.out.println("visitIntTree");
    int num = Integer.parseInt(((IntTree)t).getSymbol().toString());
    storeop(new NumOpcode(Codes.ByteCodes.LIT,num));
    return null;
  }

  public Object visitIdentifierTree(AST t) {
    AST decl = t.getDecoration();
    int addr = ((IdentifierTree)(decl.getChild(1))).getFrameOffset();
    String vname = ((IdentifierTree)t).getSymbol().toString();
    storeop(new VarOpcode(Codes.ByteCodes.LOAD,addr,vname));
    return null;
  }

  public Object visitRelationalOperationTree(AST t) {
    String op = ((RelationalOperationTree)t).getSymbol().toString();
    t.getChild(0).accept(this);
    t.getChild(1).accept(this);
    storeop(new LabelOpcode(Codes.ByteCodes.BOP,op));
    return null;
  }

  public Object visitAdditionOperationTree(AST t) {
    String op = ((AdditionOperationTree)t).getSymbol().toString();
    t.getChild(0).accept(this);
    t.getChild(1).accept(this);
    storeop(new LabelOpcode(Codes.ByteCodes.BOP,op));
    return null;
  }

  public Object visitMultiplicationOperationTree(AST t) {
    String op = ((MultiplicationOperationTree)t).getSymbol().toString();
    t.getChild(0).accept(this);
    t.getChild(1).accept(this);
    storeop(new LabelOpcode(Codes.ByteCodes.BOP,op));
    return null;
  }
}