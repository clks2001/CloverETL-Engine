/* Generated By:JJTree: Do not edit this line. CLVFVarDeclaration.java */

package org.jetel.interpreter.node;

import org.jetel.interpreter.ExpParser;
import org.jetel.interpreter.TransformLangParserVisitor;
public class CLVFVarDeclaration extends SimpleNode {
 
    public int type;
    public int varSlot; 
    public String name;
    public boolean localVar;
    
    public CLVFVarDeclaration(int id) {
    super(id);
  }

  public CLVFVarDeclaration(ExpParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
  
  public void setType(int type){
      this.type=type;
  }
  
  public void setVarSlot(int slot){
      this.varSlot=slot;
  }
  
  public void setName(String name){
      this.name=name;
  }
  
  public void setLocalVariale(boolean isLocal){
      this.localVar=isLocal;
  }
}
