/* Generated By:JJTree: Do not edit this line. CLVFReturnStatement.java */

package org.jetel.interpreter.node;
import org.jetel.interpreter.ExpParser;
import org.jetel.interpreter.TransformLangParserVisitor;
public class CLVFReturnStatement extends SimpleNode {
  public CLVFReturnStatement(int id) {
    super(id);
  }

  public CLVFReturnStatement(ExpParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
