/* Generated By:JJTree: Do not edit this line. CLVFForStatement.java */

package org.jetel.interpreter.node;
import org.jetel.interpreter.ExpParser;
import org.jetel.interpreter.TransformLangParserVisitor;

public class CLVFForStatement extends SimpleNode {
  public CLVFForStatement(int id) {
    super(id);
  }

  public CLVFForStatement(ExpParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
