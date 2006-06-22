/* Generated By:JJTree: Do not edit this line. SimpleNode.java */

package org.jetel.interpreter.node;
import org.jetel.interpreter.ExpParser;
import org.jetel.interpreter.TransformLangParserTreeConstants;
import org.jetel.interpreter.TransformLangParserVisitor;


public class SimpleNode implements Node {
  protected Node parent;
  protected Node[] children;
  protected int id;
  protected ExpParser parser;
  protected int lineNumber;
  protected int columnNumber;

  public SimpleNode(int i) {
    id = i;
  }

  public SimpleNode(ExpParser p, int i) {
    this(i);
    parser = p;
  }

  public void jjtOpen() {
  }

  public void jjtClose() {
  }
  
  public void jjtSetParent(Node n) { parent = n; }
  public Node jjtGetParent() { return parent; }

  public void jjtAddChild(Node n, int i) {
    if (children == null) {
      children = new Node[i + 1];
    } else if (i >= children.length) {
      Node c[] = new Node[i + 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = n;
  }

  public Node jjtGetChild(int i) {
    return children[i];
  }

  public int jjtGetNumChildren() {
    return (children == null) ? 0 : children.length;
  }

  /** Accept the visitor. **/
  public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  /** Accept the visitor. **/
  public Object childrenAccept(TransformLangParserVisitor visitor, Object data) {
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        children[i].jjtAccept(visitor, data);
      }
    }
    return data;
  }

  public boolean jjtHasChildren(){
      return (children!=null && children.length>0) ? true : false;
  }
  
  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

  public String toString() { return TransformLangParserTreeConstants.jjtNodeName[id]; }
  public String toString(String prefix) { return prefix + toString(); }

  /* Override this method if you want to customize how the node dumps
     out its children. */

  public void dump(String prefix) {
    System.out.println(toString(prefix));
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
	SimpleNode n = (SimpleNode)children[i];
	if (n != null) {
	  n.dump(prefix + " ");
	}
      }
    }
  }
  
  /* implicitly call or childern with init(), if there is a need to
   * initialize node, then the node should implement its own init() method
   *  (non-Javadoc)
   * @see org.jetel.interpreter.Node#init()
   */
  
  public void init(){
  	int i, k = jjtGetNumChildren();

    for (i = 0; i < k; i++)
       jjtGetChild(i).init(); 
  }

/**
 * @return Returns the lineNumber.
 */
public int getLineNumber() {
    return lineNumber;
}

/**
 * @param lineNumber The lineNumber to set.
 */
public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
}

/**
 * @return Returns the columnNumber.
 */
public int getColumnNumber() {
    return columnNumber;
}

/**
 * @param columnNumber The columnNumber to set.
 */
public void setColumnNumber(int columnNumber) {
    this.columnNumber = columnNumber;
}
}

