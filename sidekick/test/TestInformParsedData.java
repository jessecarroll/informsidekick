package sidekick.test;

import sidekick.inform.*;
import junit.framework.TestCase;

import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Created by IntelliJ IDEA. User: Naiobrin Date: Sep 18, 2005 Time: 9:45:19 PM To change
 * this template use File | Settings | File Templates.
 */
public class TestInformParsedData extends TestCase {

  private String testData = "Constant Story \"Rewind\";\n" + "Constant Headline \"^An Interactive Fiction experiment by me.^\";\n" + "\n" + "Constant INTRO 0;\n" + "Constant F_INTRO_PHASE 0;\n" + "Constant F_KNOWS_ABOUT_BATHROOM_NOISES 1;\n" + "\n" + "Global GamePhase = INTRO;\n" + "\n" + "Object -> MalformedChild \"orphan\";\n" + "\n" + "Toyroom Toyshop \"Toyshop\",\n" + "  with description\n" + "          \"The centre of a long east-west hall. Shelves are lined\n" + "           with toys, painted clowns face you from the walls and\n" + "           the floor is lightly padded with colourful mats. A doorway\n" + "           leads north, with a red warning triangle above it.\",\n" + "       name \"clowns\" \"painted\" \"shelves\" \"triangle\",\n" + "       e_to East_End, w_to West_End, n_to Danger_Zone;\n" + "\n" + "Class Toyroom\n" + "  has  light;\n" + "\n" + "Toyroom Toyshop \"Toyshop\"\n" + "  with description\n" + "          \"The centre of a long east-west hall. Shelves are lined\n" + "           with toys, painted clowns face you from the walls and\n" + "           the floor is lightly padded with colourful mats. A doorway\n" + "           leads north, with a red warning triangle above it.\",\n" + "       name \"clowns\" \"painted\" \"shelves\" \"triangle\",\n" + "       e_to East_End, w_to West_End, n_to Danger_Zone;\n" + "\n" + "Toyroom -> InnerToyshop \"Toyshop\";\n" + "\n" + "Object YourBathroom \"Your Bathroom\"\n" + "  with\n" + "  e_to YourHallway,\n" + "  name 'ceiling' 'south' 'north' 'floor',\n" + "  has light;\n" + "\n" + "Object -> ZMyBathroomWall\n" + "  with\n" + "  name 'wall'\n" + "  has scenery;\n" + "\n" + "Object ring \"bathtub ring\" bathtub with name 'ring';\n" + "\n" + "Object -> bathtub\n" + "  with\n" + "  name 'tub' 'bathtub'\n" + "  has enterable container;\n" + "\n" + "Object soap bathtub;\n" + "\n" + "Object;\n" + "\n" + "Object -> -> magnets \"refrigerator magnets\"\n" + "  with name 'poetry' 'magnet' 'magnets',\n" + "  lastReadPoem, poem\n" + "  has static;\n" + "\n" + "Object ->-> -> \"Speck of Dirt\";\n" + "\n" + "Object -> TowelRack \"towel rack\",\n" + "  with\n" + "  name 'towel' 'rack'\n" + "  has static;\n" + "  \n" + "\n" + "\n" + "Object towel \"towel\" TowelRack;\n" + "\n" + "Object shampoo bathtubb;\n" + "\n" + "\n" + "Object bathroomVoiceTopics \"bathroom voice topics\";\n" + "Object -> t_disasterarea with name 'disaster' 'area' 'concert';\n" + "Object t_fridge bathroomVoiceTopics with name 'fridge' 'refrigerator';\n" + "Object -> t_name with name 'herself' 'name' 'christine';\n" + "\n" + "[ KnockSub;\n" + "  if (noun == 0) \"You need to specify something to knock on.\";\n" + "  print \"RAP RAP RAP!  You knock on \", (the) noun, \".^\";\n" + "];\n" + "Verb 'knock'\n" + "  * -> Knock\n" + "  * 'on' noun -> Knock;\n" + "\n" + "[ TalkSub;\n" + "  if (noun has talkable)\n" + "  {\n" + "    if (RunLife(noun,##Talk) ~= 0) rfalse;\n" + "    print \"All attempts fail to meaningfully converse with \", (the) noun, \".^\";\n" + "  }\n" + "  else\n" + "    print noun, \" is incapable of speech.^\";\n" + "];\n" + "Verb 'talk'\n" + "  * 'to' noun -> Talk;";

  public void testGetTreePathForPositionWorker()
  {
    //create a mock buffer to serve as a data source for the parsing code
    MockBuffer mb = new MockBuffer(testData);

    //parse the mock buffer
    InformParser ip = new InformParser("inform parser");
    ip.setShow(
        InformParser.GLOBALS |
        InformParser.FUNCTIONS | InformParser.VERBS |
        InformParser.CONSTANTS | InformParser.CLASSES |
        InformParser.SPECIAL_NODES_ON_TOP
    );
    InformParsedData pd = (InformParsedData) ip.parseWorker(null, mb);

    TreePath tp;
    SortableTreeNode n;

    //when you ask for the path to the node corresponding to the 'Toyshop' object
    // it should give you the path to the one in the main object tree, not the one
    // in under the special Classes node
    tp = pd.getTreePathForPosition(700);
    assertNotNull(tp);
    n = (SortableTreeNode) tp.getLastPathComponent();
    assertEquals(2, tp.getPath().length);
    assertEquals(n.getUserObject(), getAssetAt(pd.root, "9"));

    //but when you ask for the path to the Toyroom class, it should still be found
    // under the special Classes node
    tp = pd.getTreePathForPosition(665);
    assertNotNull(tp);
    n = (SortableTreeNode) tp.getLastPathComponent();
    assertEquals(3, tp.getPath().length);
    assertEquals(n.getUserObject(), getAssetAt(pd.root, "00"));
  }

  //helper functions to hide some of the complexity involved in getting the name of
  //
  //  the asset associated with a given tree node
  DefaultMutableTreeNode getTreeNodeAt(TreeNode rootNode, String pathString)
  {
    //interpret the given string as a series of single-digit numbers, each
    // representing the index of the child to take at each successive
    // branch of the tree to reach a specific node; return the short
    // name of that tree node's InformAsset
    TreeNode t = rootNode;
    for (int i = 0; i < pathString.length(); i++)
    {
      char c = pathString.charAt(i);
      int childToTake = Character.digit(c, 16);
      t = t.getChildAt(childToTake);
    }
    return (DefaultMutableTreeNode) t;
  }

  InformAsset getAssetAt(TreeNode rootNode, String pathString)
  {
    return (InformAsset) getTreeNodeAt(rootNode, pathString).getUserObject();
  }

}
