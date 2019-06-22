package sidekick.inform;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Hashtable;

/**
 * Enhances DefaultMutableTreeNode by providing:
 *   -child sorting
 *   -a more flexible way to add children.
 *   -recursive cloning capability
 *
 * @author Jesse Carroll
 * @version $Revision: 1.0 $
 */
public class SortableTreeNode extends DefaultMutableTreeNode implements Comparable {

  public SortableTreeNode()
  {
    super();
  }

  public SortableTreeNode(Object o)
  {
    super(o);
  }

  public SortableTreeNode(Object o, boolean b)
  {
    super(o, b);
  }

  /**
   * Sorts this node's children in alphabetical order.
   */
  public void sort()
  {
    //bail if there are no children to sort
    if (children == null) return;

    //sort the children
    java.util.Collections.sort(children);

    //for every child that is a SortableTreeNode, sort its children as well
    for (int i = 0; i < children.size(); i++)
    {
      Object child = children.get(i);
      if (SortableTreeNode.class.equals(child.getClass()))
        ((SortableTreeNode) child).sort();
    }
  }

  public int compareTo(Object o)
  {
    return this.toString().toLowerCase().compareTo(o.toString().toLowerCase());
  }

  /**
   * Adds a new child node at the specified index.
   *
   * @param index Zero-based index of where in the list of children this node should be
   *              placed.
   * @param n     The node to add.
   */
  public void add(int index, MutableTreeNode n)
  {
    children.add(index, n);
    n.setParent(this);
  }

  public boolean hasProperty(String propertyName)
  {
    return propertyHash.containsKey(propertyName);
  }

  public void setProperty(String propertyName)
  {
    propertyHash.put(propertyName, "");
  }

  public void unsetProperty(String propertyName)
  {
    propertyHash.remove(propertyName);
  }

  /**
   * Performs a shallow copy of this node and its children.  Copied nodes are marked with
   * the 'ignoreInTreePathSearch' property (see InformParsedData).
   *
   * @return A SortableTreeNode referring to a shallow copy of this node and all of its
   *         children, arranged in an identical hierarchy.
   */
  public SortableTreeNode recursiveClone()
  {
    //clone this node
    SortableTreeNode n = (SortableTreeNode) this.clone();
    n.setProperty("ignoreInTreePathSearch");

    //stop this recursion branch if this node has no children
    if (children == null) return n;

    //go on to clone each child
    for (int i = 0; i < children.size(); i++)
    {
      Object child = children.get(i);
      if (child instanceof SortableTreeNode)
        n.add(((SortableTreeNode) child).recursiveClone());
    }

    return n;
  }

  /**
   * Extend clone to support this class's data.
   */
  public Object clone()
  {
    SortableTreeNode n = (SortableTreeNode) super.clone();
    n.setPropertyHash((Hashtable) propertyHash.clone());
    return n;
  }

  public void setPropertyHash(Hashtable ht)
  {
    this.propertyHash = ht;
  }
  protected Hashtable propertyHash = new Hashtable();
}
