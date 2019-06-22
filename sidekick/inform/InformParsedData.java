package sidekick.inform;

import sidekick.SideKickParsedData;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


/**
 * Builds on SideKickParsedData by using a SortableTreeNode as the root of the tree
 * instead of DefaultMutableTreeNode and overrides methods for mapping buffer lines to
 * their corresponding tree nodes.
 *
 * @author Jesse Carroll
 * @version $Revision: 1.0 $
 */

public class InformParsedData extends SideKickParsedData {

  /**
   * Constructs a new InformParsedData.  It uses an enhanced subclass of
   * DefaultMutableTreeNode as the root of the tree in order to facilitate node sorting
   * and rearrangement.
   *
   * @param fileName The file name being parsed, used as the root of the tree.
   */
  public InformParsedData(String fileName)
  {
    super(fileName);
    root = new SortableTreeNode(fileName);
    tree = new DefaultTreeModel(root);
  }

  /**
   * Find the TreePath to a node corresponding to the given buffer offset.
   *
   * @param dot The offset.
   */
  public TreePath getTreePathForPosition(int dot)
  {
    return getTreePathForPositionWorker(dot, (SortableTreeNode) root);
  }

  private TreePath getTreePathForPositionWorker(int dot, SortableTreeNode node)
  {
    /**
     * During the parse, some nodes may have been marked with the ignoreInTreePathSearch
     * flag, indicating that the node and all of its children should not be considered
     * when searching for the treepath of a node.  This is useful for providing hints to
     * this method for which node to select when two tree nodes refer to the same asset,
     * as happens when the special Classes node.  Also excluded from consideration are
     * nodes marked with the "copy" property, which is taken to indicate that this
     * node is a copy of another node that is already in the tree. is turned on or when an object is
     * found_in multiple locations.
     */
    if (node.hasProperty("ignoreInTreePathSearch"))
    {
      return null;
    }

    //stop recursion and return the tree path to this node if its
    // associated asset contains the offset
    Object o = node.getUserObject();
    if (o != null && o instanceof InformAsset)
    {
      InformAsset a = (InformAsset) o;
      if (dot > a.getStart().getOffset() && dot < a.getEnd().getOffset())
      {
        return new TreePath(node.getPath());
      }
    }

    //if any of this node's children yield a tree path, return it
    if (node.getChildCount() > 0 )
    {
      for (int i = 0; i < node.getChildCount(); i++)
      {
        TreePath tp = getTreePathForPositionWorker(dot,
            (SortableTreeNode) node.getChildAt(i));
        if (tp != null) return tp;
      }
    }

    //if no node beneath this one represented an asset containing the offset
    return null;
  }
}
