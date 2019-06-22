package sidekick.test;

import junit.framework.TestCase;
import sidekick.SideKickParsedData;
import sidekick.inform.InformAsset;
import sidekick.inform.InformParser;
import sidekick.inform.MockBuffer;
import sidekick.inform.InformEntity;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Created by IntelliJ IDEA. User: Naiobrin Date: Aug 28, 2005 Time: 8:07:33 PM To change
 * this template use File | Settings | File Templates.
 */
public class TestInformParser extends TestCase {

  private String testData = "Constant Story \"Rewind\";\n" +
      "Constant Headline \"^An Interactive Fiction experiment by me.^\";\n" +
      "\n" +
      "Constant INTRO 0;\n" +
      "Constant F_INTRO_PHASE 0;\n" +
      "Constant F_KNOWS_ABOUT_BATHROOM_NOISES 1;\n" +
      "\n" +
      "Global GamePhase = INTRO;\n" +
      "\n" +
      "Object -> MalformedChild \"orphan\";\n" +
      "\n" +
      "Toyroom Toyshop \"Toyshop\",\n" +
      "  with description\n" +
      "          \"The centre of a long east-west hall. Shelves are lined\n" +
      "           with toys, painted clowns face you from the walls and\n" +
      "           the floor is lightly padded with colourful mats. A doorway\n" +
      "           leads north, with a red warning triangle above it.\",\n" +
      "       name \"clowns\" \"painted\" \"shelves\" \"triangle\",\n" +
      "       e_to East_End, w_to West_End, n_to Danger_Zone;\n" +
      "\n" +
      "Class Toyroom\n" +
      "  has  light;\n" +
      "\n" +
      "Toyroom Toyshop \"Toyshop\" with\n" +
      "  description\n" +
      "          \"The centre of a long east-west hall. Shelves are lined\n" +
      "           with toys, painted clowns face you from the walls and\n" +
      "           the floor is lightly padded with colourful mats. A doorway\n" +
      "           leads north, with a red warning triangle above it.\",\n" +
      "       name \"clowns\" \"painted\" \"shelves\" \"triangle\",\n" +
      "       e_to East_End, w_to West_End, n_to Danger_Zone;\n" +
      "\n" +
      "Toyroom -> InnerToyshop \"Toyshop\";\n" +
      "\n" +
      "Object YourBathroom \"Your Bathroom\"\n" +
      "  with\n" +
      "  e_to YourHallway,\n" +
      "  name 'ceiling' 'south' 'north' 'floor',\n" +
      "  has light;\n" +
      "\n" +
      "CompassDirection n_obj \"north wall\" compass\n" +
      "  with name 'n' 'north' 'wall', door_dir n_to;\n" +
      "\n" +
      "CompassDirection s_obj \"south wall\" compass\n" +
      "  with name 's' 'south' 'wall', door_dir s_to;\n" +
      "\n" +
      "Object -> ZMyBathroomWall\n" +
      "  with\n" +
      "  name 'wall'\n" +
      "  has scenery; ! should be able to handle comments that start outside of an entity but not at the start of the line\n" +
      "\n" +
      "Object ring \"bathtub ring\" bathtub with name 'ring';\n" +
      "\n" +
      "Object -> bathtub\n" +
      "  with\n" +
      "  name 'tub' 'bathtub'\n" +
      "  has enterable container;\n" +
      "\n" +
      "Object soap bathtub;\n" +
      "\n" +
      "Object;\n" +
      "\n" +
      "Object -> -> magnets \"refrigerator magnets\"\n" +
      "  with name 'poetry' 'magnet' 'magnets',\n" +
      "  lastReadPoem, poem\n" +
      "  has static;\n" +
      "\n" +
      "Object ->-> -> \"Speck of Dirt\" with found_in bogusParent1 magnets wickedClassy1 bogusParent2;\n" +
      "\n" +
      "Object\n" +
      "      -> ->-> wickedClassy1 ! has brackets [ and comments with \"quotes\n" +
      "\"parsing with class; has to be done!!!\" has\n" +
      "light static\n" +
      "  ! with whole lines of comments\n" +
      "with name 'wicked' 'devious' 'horrible',\n" +
      "description \"[This is not the beginning of a function block\",\n" +
      "before [; print \"But this is inside of a function.\";\n" +
      " ! a comment^\";\n" +
      "],\n" +
      "initial [; print \"]It's all valid Inform, really\"; ],\n" +
      ";\n" +
      "\n" +
      "Object -> ->-> wickedClassy2 ! has brackets [ and comments with \"quotes\n" +
      "\"inherits from invalid classes!\" has\n" +
      "light static\n" +
      "  ! with whole lines of comments\n" +
      "with name 'wicked' 'devious' 'horrible',\n" +
      "description \"[This is not the beginning of a function block\",\n" +
      "before [; print \"But this is inside of a function.\";\n" +
      " ! a comment^\";\n" +
      "],\n" +
      "initial [; print \"]It's all valid Inform, really\"; ],\n" +
      "class evilClass1 ORLibClass\n" +
      "  evilClass2;\n" +
      "\n" +
      "Object ->->->-> \"still has\";\n" +
      "Object ->->->-> \"children\";\n" +
      "\n" +
      "Object -> TowelRack \"towel rack\",\n" +
      "  with\n" +
      "  name 'towel' 'rack'\n" +
      "  has static;\n" +
      "  \n" +
      "\n" +
      "\n" +
      "Object towel \"towel\" TowelRack;\n" +
      "\n" +
      "Object shampoo bathtubb;\n" +
      "\n" +
      "\n" +
      "Object bathroomVoiceTopics \"bathroom voice topics\";\n" +
      "Object -> t_disasterarea with name 'disaster' 'area' 'concert';\n" +
      "Object t_fridge bathroomVoiceTopics with name 'fridge' 'refrigerator';\n" +
      "Object -> t_name with name 'herself' 'name' 'christine';\n" +
      "\n" +
      "[ KnockSub;\n" +
      "  if (noun == 0) \"You need to specify something to knock on.\";\n" +
      "  print \"RAP RAP RAP!  You knock on \", (the) noun, \".^\";\n" +
      "]; ! should be able to handle comments that start outside of an entity but not at the start of the line\n" +
      "Verb 'knock'\n" +
      "  * -> Knock\n" +
      "  * 'on' noun -> Knock;\n" +
      "\n" +
      "Array    task_scores  -> 1 1 1 1 1 1;\n" +
      "\n" +
      "[ TalkSub;\n" +
      "  if (noun has talkable)\n" +
      "  {\n" +
      "    if (RunLife(noun,##Talk) ~= 0) rfalse;\n" +
      "    print \"All attempts fail to meaningfully converse with \", (the) noun, \".^\";\n" +
      "  }\n" +
      "  else\n" +
      "    print noun, \" is incapable of speech.^\";\n" +
      "];\n" +
      "Verb 'talk'\n" +
      "  * 'to' noun -> Talk;";

  public void testParseWorker()
  {
    //create a mock buffer to serve as a data source for the parsing code
    MockBuffer mb = new MockBuffer(testData);

    //parse the mock buffer without showing any special nodes
    InformParser ip = new InformParser("inform parser");
    SideKickParsedData pd = ip.parseWorker(null, mb);

    //check the results
    TreeNode rootNode = pd.root;

    assertEquals(7, rootNode.getChildCount());

    assertEquals("bathroomVoiceTopics", getAssetNameAt(rootNode, "0"));
      assertEquals(3, getTreeNodeAt(rootNode, "0").getChildCount());
      assertEquals("t_disasterarea", getAssetNameAt(rootNode, "00"));
      assertEquals("t_fridge", getAssetNameAt(rootNode, "01"));
      assertEquals("t_name", getAssetNameAt(rootNode, "02"));

    assertEquals("Compass", getAssetNameAt(rootNode, "1"));
      assertEquals(2, getTreeNodeAt(rootNode, "1").getChildCount());
      assertEquals("n_obj", getAssetNameAt(rootNode, "10"));
      assertEquals("s_obj", getAssetNameAt(rootNode, "11"));

    assertEquals("MalformedChild", getAssetNameAt(rootNode, "2"));

    assertEquals("ring", getAssetNameAt(rootNode, "3"));

    assertEquals("shampoo", getAssetNameAt(rootNode, "4"));

    assertEquals("Toyshop", getAssetNameAt(rootNode, "5"));
      assertEquals(1, getTreeNodeAt(rootNode, "5").getChildCount());
      assertEquals("InnerToyshop", getAssetNameAt(rootNode, "50"));

    assertEquals("YourBathroom", getAssetNameAt(rootNode, "6"));
      assertEquals(3, getTreeNodeAt(rootNode, "6").getChildCount());
      assertEquals("bathtub", getAssetNameAt(rootNode, "60"));
        assertEquals(2, getTreeNodeAt(rootNode, "60").getChildCount());
        assertEquals("magnets", getAssetNameAt(rootNode, "600"));
          assertEquals(3, getTreeNodeAt(rootNode, "600").getChildCount());
          assertEquals("Speck of Dirt", getAssetNameAt(rootNode, "6000"));
          assertEquals("wickedClassy1", getAssetNameAt(rootNode, "6001"));
            assertEquals(1, getTreeNodeAt(rootNode, "6001").getChildCount());
            assertEquals("Speck of Dirt", getAssetNameAt(rootNode, "60010"));
          assertEquals("wickedClassy2", getAssetNameAt(rootNode, "6002"));
            assertEquals(2, getTreeNodeAt(rootNode, "6002").getChildCount());
            assertEquals("children", getAssetNameAt(rootNode, "60020"));
            assertEquals("still has", getAssetNameAt(rootNode, "60021"));
        assertEquals("soap", getAssetNameAt(rootNode, "601"));
      assertEquals("TowelRack", getAssetNameAt(rootNode, "61"));
        assertEquals("towel", getAssetNameAt(rootNode, "610"));
      assertEquals("ZMyBathroomWall", getAssetNameAt(rootNode, "62"));
  }

  public void testParseWorkerWithSpecialNodes()
  {
    //create a mock buffer to serve as a data source for the parsing code
    MockBuffer mb = new MockBuffer(testData);

    //parse the mock buffer, showing all special nodes
    InformParser ip = new InformParser("inform parser");
    ip.setShow(
        InformParser.GLOBALS |
        InformParser.FUNCTIONS | InformParser.VERBS |
        InformParser.CONSTANTS | InformParser.CLASSES
    );
    SideKickParsedData pd = ip.parseWorker(null, mb);

    //check the results
    TreeNode rootNode = pd.root;

    assertEquals(12, rootNode.getChildCount());

    assertEquals("bathroomVoiceTopics", getAssetNameAt(rootNode, "0"));
      assertEquals(3, getTreeNodeAt(rootNode, "0").getChildCount());
      assertEquals("t_disasterarea", getAssetNameAt(rootNode, "00"));
      assertEquals("t_fridge", getAssetNameAt(rootNode, "01"));
      assertEquals("t_name", getAssetNameAt(rootNode, "02"));

    assertEquals("Compass", getAssetNameAt(rootNode, "1"));
      assertEquals(2, getTreeNodeAt(rootNode, "1").getChildCount());
      assertEquals("n_obj", getAssetNameAt(rootNode, "10"));
      assertEquals("s_obj", getAssetNameAt(rootNode, "11"));

    assertEquals("MalformedChild", getAssetNameAt(rootNode, "2"));

    assertEquals("ring", getAssetNameAt(rootNode, "3"));

    assertEquals("shampoo", getAssetNameAt(rootNode, "4"));

    assertEquals("Toyshop", getAssetNameAt(rootNode, "5"));
      assertEquals(1, getTreeNodeAt(rootNode, "5").getChildCount());
      assertEquals("InnerToyshop", getAssetNameAt(rootNode, "50"));

    assertEquals("YourBathroom", getAssetNameAt(rootNode, "6"));
      assertEquals(3, getTreeNodeAt(rootNode, "6").getChildCount());
      assertEquals("bathtub", getAssetNameAt(rootNode, "60"));
        assertEquals(2, getTreeNodeAt(rootNode, "60").getChildCount());
        assertEquals("magnets", getAssetNameAt(rootNode, "600"));
          assertEquals(3, getTreeNodeAt(rootNode, "600").getChildCount());
          assertEquals("Speck of Dirt", getAssetNameAt(rootNode, "6000"));
          assertEquals("wickedClassy1", getAssetNameAt(rootNode, "6001"));
            assertEquals(1, getTreeNodeAt(rootNode, "6001").getChildCount());
            assertEquals("Speck of Dirt", getAssetNameAt(rootNode, "60010"));
          assertEquals("wickedClassy2", getAssetNameAt(rootNode, "6002"));
            assertEquals(2, getTreeNodeAt(rootNode, "6002").getChildCount());
            assertEquals("children", getAssetNameAt(rootNode, "60020"));
            assertEquals("still has", getAssetNameAt(rootNode, "60021"));
        assertEquals("soap", getAssetNameAt(rootNode, "601"));
      assertEquals("TowelRack", getAssetNameAt(rootNode, "61"));
        assertEquals("towel", getAssetNameAt(rootNode, "610"));
      assertEquals("ZMyBathroomWall", getAssetNameAt(rootNode, "62"));

    assertEquals("Classes", getAssetNameAt(rootNode, "7"));
      assertEquals(2, getTreeNodeAt(rootNode, "7").getChildCount());
      assertEquals("CompassDirection", getAssetNameAt(rootNode, "70"));
        assertEquals(2, getTreeNodeAt(rootNode, "70").getChildCount());
        assertEquals("n_obj", getAssetNameAt(rootNode, "700"));
        assertEquals("s_obj", getAssetNameAt(rootNode, "701"));
      assertEquals("Toyroom", getAssetNameAt(rootNode, "71"));
        assertEquals(2, getTreeNodeAt(rootNode, "71").getChildCount());
        assertEquals("InnerToyshop", getAssetNameAt(rootNode, "710"));
        assertEquals("Toyshop", getAssetNameAt(rootNode, "711"));

    assertEquals("Constants", getAssetNameAt(rootNode, "8"));
      assertEquals("F_INTRO_PHASE", getAssetNameAt(rootNode, "80"));
      assertEquals("F_KNOWS_ABOUT_BATHROOM_NOISES", getAssetNameAt(rootNode, "81"));
      assertEquals("Headline", getAssetNameAt(rootNode, "82"));
      assertEquals("INTRO", getAssetNameAt(rootNode, "83"));
      assertEquals("Story", getAssetNameAt(rootNode, "84"));
    assertEquals("Functions", getAssetNameAt(rootNode, "9"));
      assertEquals("KnockSub", getAssetNameAt(rootNode, "90"));
      assertEquals("TalkSub", getAssetNameAt(rootNode, "91"));
    assertEquals("Globals", getAssetNameAt(rootNode, "A"));
      assertEquals("GamePhase", getAssetNameAt(rootNode, "A0"));
    assertEquals("Verbs", getAssetNameAt(rootNode, "B"));
      assertEquals("knock", getAssetNameAt(rootNode, "B0"));
      assertEquals("talk", getAssetNameAt(rootNode, "B1"));
  }

  public void testParseWorkerAlphabetic()
  {
    //create a mock buffer to serve as a data source for the parsing code
    MockBuffer mb = new MockBuffer(testData);
    InformParser ip = new InformParser("inform parser");
    ip.setViewType(InformParser.ALPHABETICAL_VIEW);
    SideKickParsedData pd = ip.parseWorker(null, mb);

    assertEquals(23, pd.root.getChildCount());
    String[] nodes = {
      "bathroomVoiceTopics",
      "bathtub",
      "children",
      "InnerToyshop",
      "magnets",
      "MalformedChild",
      "n_obj",
      "ring",
      "s_obj",
      "shampoo",
      "soap",
      "Speck of Dirt",
      "still has",
      "t_disasterarea",
      "t_fridge",
      "t_name",
      "towel",
      "TowelRack",
      "Toyshop",
      "wickedClassy1",
      "wickedClassy2",
      "YourBathroom",
      "ZMyBathroomWall"
    };
    for (int i = 0; i < nodes.length; i++)
      assertEquals(nodes[i], pd.root.getChildAt(i).toString());
  }

  public void testSpecialNodesOnTop()
  {
    //create a mock buffer to serve as a data source for the parsing code
    MockBuffer mb = new MockBuffer(testData);

    //parse the mock buffer, showing all special nodes
    InformParser ip = new InformParser("inform parser");
    ip.setShow(
        InformParser.GLOBALS |
        InformParser.FUNCTIONS | InformParser.VERBS |
        InformParser.CONSTANTS | InformParser.CLASSES |
        InformParser.SPECIAL_NODES_ON_TOP
    );
    SideKickParsedData pd = ip.parseWorker(null, mb);

    //check the results
    TreeNode rootNode = pd.root;

    assertEquals(12, rootNode.getChildCount());

    assertEquals("Classes", getAssetNameAt(rootNode, "0"));
      assertEquals("CompassDirection", getAssetNameAt(rootNode, "00"));
        assertEquals(2, getTreeNodeAt(rootNode, "00").getChildCount());
        assertEquals("n_obj", getAssetNameAt(rootNode, "000"));
        assertEquals("s_obj", getAssetNameAt(rootNode, "001"));
      assertEquals("Toyroom", getAssetNameAt(rootNode, "01"));
        assertEquals(2, getTreeNodeAt(rootNode, "01").getChildCount());
        assertEquals("InnerToyshop", getAssetNameAt(rootNode, "010"));
        assertEquals("Toyshop", getAssetNameAt(rootNode, "011"));

    assertEquals("Constants", getAssetNameAt(rootNode, "1"));
      assertEquals("F_INTRO_PHASE", getAssetNameAt(rootNode, "10"));
      assertEquals("F_KNOWS_ABOUT_BATHROOM_NOISES", getAssetNameAt(rootNode, "11"));
      assertEquals("Headline", getAssetNameAt(rootNode, "12"));
      assertEquals("INTRO", getAssetNameAt(rootNode, "13"));
      assertEquals("Story", getAssetNameAt(rootNode, "14"));
    assertEquals("Functions", getAssetNameAt(rootNode, "2"));
      assertEquals("KnockSub", getAssetNameAt(rootNode, "20"));
      assertEquals("TalkSub", getAssetNameAt(rootNode, "21"));
    assertEquals("Globals", getAssetNameAt(rootNode, "3"));
      assertEquals("GamePhase", getAssetNameAt(rootNode, "30"));
    assertEquals("Verbs", getAssetNameAt(rootNode, "4"));
      assertEquals("knock", getAssetNameAt(rootNode, "40"));
      assertEquals("talk", getAssetNameAt(rootNode, "41"));

    assertEquals("bathroomVoiceTopics", getAssetNameAt(rootNode, "5"));
      assertEquals(3, getTreeNodeAt(rootNode, "5").getChildCount());
      assertEquals("t_disasterarea", getAssetNameAt(rootNode, "50"));
      assertEquals("t_fridge", getAssetNameAt(rootNode, "51"));
      assertEquals("t_name", getAssetNameAt(rootNode, "52"));

    assertEquals("Compass", getAssetNameAt(rootNode, "6"));
      assertEquals(2, getTreeNodeAt(rootNode, "6").getChildCount());
      assertEquals("n_obj", getAssetNameAt(rootNode, "60"));
      assertEquals("s_obj", getAssetNameAt(rootNode, "61"));

    assertEquals("MalformedChild", getAssetNameAt(rootNode, "7"));

    assertEquals("ring", getAssetNameAt(rootNode, "8"));

    assertEquals("shampoo", getAssetNameAt(rootNode, "9"));

    assertEquals("Toyshop", getAssetNameAt(rootNode, "A"));
      assertEquals(1, getTreeNodeAt(rootNode, "A").getChildCount());
      assertEquals("InnerToyshop", getAssetNameAt(rootNode, "A0"));

    assertEquals("YourBathroom", getAssetNameAt(rootNode, "B"));
      assertEquals(3, getTreeNodeAt(rootNode, "B").getChildCount());
      assertEquals("bathtub", getAssetNameAt(rootNode, "B0"));
        assertEquals(2, getTreeNodeAt(rootNode, "B0").getChildCount());
        assertEquals("magnets", getAssetNameAt(rootNode, "B00"));
          assertEquals(3, getTreeNodeAt(rootNode, "B00").getChildCount());
          assertEquals("Speck of Dirt", getAssetNameAt(rootNode, "B000"));
          assertEquals("wickedClassy1", getAssetNameAt(rootNode, "B001"));
            assertEquals(1, getTreeNodeAt(rootNode, "B001").getChildCount());
            assertEquals("Speck of Dirt", getAssetNameAt(rootNode, "B0010"));
          assertEquals("wickedClassy2", getAssetNameAt(rootNode, "B002"));
            assertEquals(2, getTreeNodeAt(rootNode, "B002").getChildCount());
            assertEquals("children", getAssetNameAt(rootNode, "B0020"));
            assertEquals("still has", getAssetNameAt(rootNode, "B0021"));
        assertEquals("soap", getAssetNameAt(rootNode, "B01"));
      assertEquals("TowelRack", getAssetNameAt(rootNode, "B1"));
        assertEquals("towel", getAssetNameAt(rootNode, "B10"));
      assertEquals("ZMyBathroomWall", getAssetNameAt(rootNode, "B2"));
  }

  public void testParseWorkerPlayerThedarkCompassBehavior()
  {
    MockBuffer mb;
    InformParser ip = new InformParser("inform parser");
    SideKickParsedData pd;

    mb = new MockBuffer("Object  dictionary \"Waldeck's Mayan dictionary\";\n" +
        "Object  map \"sketch-map of Quintana Roo\";\n" +
        "Object  sodium_lamp \"sodium lamp\";\n" +
        "[ Initialise;\n" +
        "  TitlePage();\n" +
        "  location = Forest;\n" +
        "  move map to player;\n" +
        "  move nonobject to player;\n" +
        "  move sodium_lamp to self;\n" +
        "  move nonobject to nonplayer;\n" +
        "  move dictionary to player;\n" +
        "  StartDaemon(sodium_lamp);\n" +
        "  thedark.description =\n" +
        "      \"The darkness of ages presses in on you, and you feel\n" +
        "       claustrophobic.\";\n" +
        "  \"^^^Days of searching, days of thirsty hacking through the briars of\n" +
        "   the forest, but at last your patience was rewarded. A discovery!^\";\n" +
        "];\n" +
        "CompassDirection n_obj \"north wall\" compass\n" +
        "  with name 'n' 'north' 'wall', door_dir n_to;");
    ip.setShow(InformParser.ANALYZE_INIT);
    pd = ip.parseWorker(null, mb);
    assertEquals(2, pd.root.getChildCount());
    assertEquals("Compass", getAssetNameAt(pd.root, "0"));
      assertEquals(1, getTreeNodeAt(pd.root, "0").getChildCount());
      assertEquals("n_obj", getAssetNameAt(pd.root, "00"));
    assertEquals("Player", getAssetNameAt(pd.root, "1"));
      assertEquals(3, getTreeNodeAt(pd.root, "1").getChildCount());
      assertEquals("dictionary", getAssetNameAt(pd.root, "10"));
      assertEquals("map", getAssetNameAt(pd.root, "11"));
      assertEquals("sodium_lamp", getAssetNameAt(pd.root, "12"));

    mb = new MockBuffer("Object  dictionary \"Waldeck's Mayan dictionary\";\n" + "Object  map \"sketch-map of Quintana Roo\";\n" + "Object  sodium_lamp \"sodium lamp\";\n" + "Object customPlayer \"custom player\";\n" + "Object -> hat;\n" + "Object -> \"Notebook\";\n" + "Object -> axe;\n" + "[ Initialise;\n" + "  TitlePage();\n" + "  location = Forest;\n" + "  move map to customPlayer;\n" + "  move gurglefluncheon to player;\n" + "  move zarphglof to customPlayer;\n" + "  move sodium_lamp to player;\n" + "  move dictionary to player;\n" + "  player = customPlayer;\n" + "  player = noobjecthere;\n" + "  move dictionary to player;\n" + "  StartDaemon(sodium_lamp);\n" + "  thedark.description =\n" + "      \"The darkness of ages presses in on you, and you feel\n" + "       claustrophobic.\";\n" + "  \"^^^Days of searching, days of thirsty hacking through the briars of\n" + "   the forest, but at last your patience was rewarded. A discovery!^\";\n" + "];");
    pd = ip.parseWorker(null, mb);
    assertEquals(1, pd.root.getChildCount());
    assertEquals("customPlayer", getAssetNameAt(pd.root, "0"));
      assertEquals(5, getTreeNodeAt(pd.root, "0").getChildCount());
      assertEquals("axe", getAssetNameAt(pd.root, "00"));
      assertEquals("dictionary", getAssetNameAt(pd.root, "01"));
      assertEquals("hat", getAssetNameAt(pd.root, "02"));
      assertEquals("map", getAssetNameAt(pd.root, "03"));
      assertEquals("Notebook", getAssetNameAt(pd.root, "04"));
  }


  public void testParseWorkerFunctionsBehavior()
  {
    MockBuffer mb;
    InformParser ip = new InformParser("inform parser");
    SideKickParsedData pd;

    mb = new MockBuffer("Class Toyroom\n" + "  has  light;\n" + "\n" + "Toyroom West_End \"West End\"\n" + "  with name \"soldiers\" \"model\" \"aircraft\" \"planes\",\n" + "       description\n" + "          \"The western end of the toyshop is blue, and soldiers and\n" + "           model aircraft line the shelves; a small office lies to\n" + "           the south.\",\n" + "       before\n" + "       [wicked to the core;\n" + "         print \"This should throw the parser for a loop!\"\n" + "       ],\n" + "       e_to Toyshop, s_to Office;\n" + "\n" + "Class  Block\n" + "  with description \"Just a child's building block, four inches on a side.\",\n" + "\n" + "       !   The parse_name routine below ensures that \"take blocks\"\n" + "       !   works correctly:\n" + "\n" + "       parse_name\n" + "       [ i j;\n" + "         for (::)\n" + "         {   j=NextWord();\n" + "             if (j=='block' or 'cube' or 'building' or (self.name)) i++;\n" + "             else\n" + "             {   if (j=='blocks' or 'cubes')\n" + "                 {   parser_action=##PluralFound; i++; }\n" + "                 else return i;\n" + "             }\n" + "         }\n" + "       ],\n" + "\n" + "       describe\n" + "       [ c d e;\n" + "           d = child(self);\n" + "           while (d~=0 && d ofclass Block)\n" + "           {   c++; e=d; d=child(d); }\n" + "           if (c==0) rfalse;\n" + "           print \"^There is a pile of building blocks here, \";\n" + "           while (c>=0)\n" + "           {   print (address) e.name;  ! Sneaky: print the \"name\" out\n" + "               if (c>0) print \" on \";   ! using its dictionary address\n" + "               c--; e=parent(e);\n" + "           }\n" + "           \".\";\n" + "       ],\n" + "       before\n" + "       [ c;\n" + "         PutOn:\n" + "           if (second ofclass Block)\n" + "           {   if (child(second)~=0 && child(second) ofclass Block)\n" + "                   \"There's no room on the top of one cube for two more, side\n" + "                    by side.\";\n" + "           }\n" + "           else\n" + "               print \"(They're really intended\n" + "                      to be piled on top of each other.)^\";\n" + "           c=second; while (c ofclass Block) c=parent(c);\n" + "           if (c~=location or mantelpiece) \"Too unsteady a base.\";\n" + "       ],\n" + "       after\n" + "       [ c stack;\n" + "         PutOn:\n" + "           stack=noun;\n" + "           while (parent(stack) ofclass Block) { stack=parent(stack); c++; }\n" + "           if (c<2)\n" + "           {   if (Chris has general) rtrue;\n" + "               rfalse;\n" + "           }\n" + "           if (c==2) \"The pile of three cubes is unsteady, but viable.\";\n" + "           if (Chris has general)\n" + "           {   Achieved(3);\n" + "               \"^Expertly he keeps the pile of four cubes stable.\";\n" + "           }\n" + "           stack=noun;\n" + "           while (parent(stack) ofclass Block)\n" + "           {   c=stack; stack=parent(stack); move c to location; }\n" + "           \"The pile of four cubes wobbles, wobbles, steadies... and suddenly\n" + "            collapses!\";\n" + "         Take:\n" + "           stack=child(noun); if (stack==0) rfalse;\n" + "           while (stack~=0)\n" + "           { c=stack; stack=child(stack); move c to location; }\n" + "           \"Your pile of cubes is collapsed as a result.\";\n" + "       ],\n" + "  has  supporter;\n" + "\n" + "Block -> \"green cube\"\n" + "  with name \"green\";\n" + "\n" + "[ PrintTaskName achievement a b c d;\n" + "  switch(achievement)\n" + "  {   0: \"eating a sweet\";\n" + "      1: \"driving the car\";\n" + "      2: \"shutting out the draught\";\n" + "      3: \"building a tower of four\";\n" + "      4: \"seeing which way the mantelpiece leans\";\n" + "      5: \"writing on the blackboard\";\n" + "  }\n" + "];\n" + "\n" + "Object Chris \"Christopher\"\n" + "  with name \"child\" \"boy\" \"chris\" \"christopher\",\n" + "       describe\n" + "       [;  print \"^A boy called Christopher sits here\";\n" + "           if (child(Chris) ~= nothing)\n" + "               print \", playing with \", (a) child(Chris);\n" + "           \".\";\n" + "       ],\n" + "       life\n" + "       [ x;\n" + "           Ask:\n" + "              switch(second)\n" + "              {   'juggling', 'fluorescent', 'ball': \"~That's mine!~\";\n" + "                  'helium', 'balloon': \"Christopher yawns.\";\n" + "                  'cube', 'cubes': \"~Bet I can make a higher tower than you.~\";\n" + "                  'toys', 'toyshop': \"~Isn't it fabulous here?~\";\n" + "                  default: \"~Dunno.~\";\n" + "              }\n" + "           Answer:\n" + "              switch(noun)\n" + "              {   'hello', 'hallo', 'hi':\n" + "                       \"~Hello,~ says Christopher cheerfully.\";\n" + "                  default: \"Christopher seems preoccupied.\";\n" + "              }\n" + "           Attack: remove self;\n" + "             \"Christopher makes a run for it, effortlessly slipping past you!\";\n" + "           Kiss: \"~That's soppy, that is.~\";\n" + "           Give:\n" + "             if (noun==balloon) \"He's too bored by the balloon.\";\n" + "             x=child(Chris);\n" + "             if (x~=0)\n" + "             {   move x to location;\n" + "                 print \"He forgets about \", (the) x, \" and \";\n" + "             }\n" + "             else print \"He \";\n" + "             print \"eagerly grabs \", (the) noun; move noun to Chris; \".\";\n" + "       ],\n" + "       orders\n" + "       [;  Drop: if (noun in Chris) \"~Won't!  It's mine!~\";\n" + "           Take: \"Christopher can't be bothered.\";\n" + "           Give: if (second==player) \"~Get your own!~\";\n" + "           Go: \"~But I like it here!~\";\n" + "           PutOn: if (noun notin Chris) \"He is mightily confused.\";\n" + "                 if (~~(noun ofclass Block && second ofclass Block))\n" + "                     \"He can't see the point of this.\";\n" + "                 print \"Christopher leans over with great concentration\n" + "                     and does so.^\";\n" + "                 move noun to player; give self general;\n" + "                 <PutOn noun second>;\n" + "                 give self ~general; rtrue;\n" + "       ],\n" + "       each_turn\n" + "       [;  if (random(3)~=1) rtrue;\n" + "           print \"^Christopher \";\n" + "           switch(random(4))\n" + "           {  1: \"yawns.\";     2: \"frowns.\";\n" + "              3: \"stretches.\"; 4: \"hums tonelessly.\";\n" + "           }\n" + "       ],\n" + "  has  animate proper transparent;\n" + "\n" + "Object \"fluorescent juggling ball\" Chris\n" + "  with initial \"On the floor is a fluorescent juggling ball!\",\n" + "       name \"fluorescent\" \"juggling\" \"ball\",\n" + "       description \"It glows with soft light.\"\n" + "  has  light;");
    ip.setShow(InformParser.FUNCTIONS);
    pd = ip.parseWorker(null, mb);
    assertEquals(3, pd.root.getChildCount());
    assertEquals("Chris", getAssetNameAt(pd.root, "0"));
      assertEquals(1, getTreeNodeAt(pd.root, "0").getChildCount());
      assertEquals("fluorescent juggling ball", getAssetNameAt(pd.root, "00"));
    assertEquals("West_End", getAssetNameAt(pd.root, "1"));
      assertEquals(1, getTreeNodeAt(pd.root, "1").getChildCount());
      assertEquals("green cube", getAssetNameAt(pd.root, "10"));
    assertEquals("Functions", getAssetNameAt(pd.root, "2"));
      assertEquals(1, getTreeNodeAt(pd.root, "2").getChildCount());
      assertEquals("PrintTaskName", getAssetNameAt(pd.root, "20"));
  }


  public void testParseWorkerVerbsBehavior()
  {
    MockBuffer mb;
    InformParser ip = new InformParser("inform parser");
    SideKickParsedData pd;

    mb = new MockBuffer("[ WakeUpSub;\n" + "  \"This is not some lame dream sequence.\";\n" + "];\n" + "Extend 'wake' replace\n" + "  * -> WakeUp\n" + "  * 'up' -> WakeUp;\n" + "\n" + "Verb 'yell' 'scream' 'holler'\n" + "  * -> Shout\n" + "  * 'to'/'at' noun -> Shout;\n" + "Extend only 'shout' replace\n" + "  * -> Shout\n" + "  * 'to'/'at' noun -> Shout;\n" + "\n" + "Verb 'knock'\n" + "  * -> Knock\n" + "  * 'on' noun -> Knock;\n" + "\n" + "[ TalkSub;\n" + "  if (noun has talkable)\n" + "  {\n" + "    if (RunLife(noun,##Talk) ~= 0) rfalse;\n" + "    print \"All attempts fail to meaningfully converse with \", (the) noun, \".^\";\n" + "  }\n" + "  else\n" + "    print noun, \" is incapable of speech.^\";\n" + "];\n" + "Verb 'talk'\n" + "  * 'to' noun -> Talk;\n" + "\n" + "Verb 'knock'\n" + "  * -> Knock\n" + "  * 'on' noun -> Knock;\n" + "\n" + "Extend replace 'knock'\n" + "  * -> Knock\n" + "  * 'on' noun -> Knock;");
    ip.setShow(InformParser.FUNCTIONS | InformParser.VERBS);
    pd = ip.parseWorker(null, mb);
    assertEquals(2, pd.root.getChildCount());
    assertEquals("Functions", getAssetNameAt(pd.root, "0"));
      assertEquals(2, getTreeNodeAt(pd.root, "0").getChildCount());
      assertEquals("TalkSub", getAssetNameAt(pd.root, "00"));
      assertEquals("WakeUpSub", getAssetNameAt(pd.root, "01"));
    assertEquals("Verbs", getAssetNameAt(pd.root, "1"));
      assertEquals(7, getTreeNodeAt(pd.root, "1").getChildCount());
      assertEquals("holler", getAssetNameAt(pd.root, "10"));
      assertEquals("knock", getAssetNameAt(pd.root, "11"));
      assertEquals("scream", getAssetNameAt(pd.root, "12"));
      assertEquals("shout", getAssetNameAt(pd.root, "13"));
      assertEquals("talk", getAssetNameAt(pd.root, "14"));
      assertEquals("wake", getAssetNameAt(pd.root, "15"));
      assertEquals("yell", getAssetNameAt(pd.root, "16"));
  }

  public void testAlphabeticalOverridesAnalyzeInit()
  {
    MockBuffer mb;
    InformParser ip = new InformParser("inform parser");
    SideKickParsedData pd;

    mb = new MockBuffer("Object  dictionary \"Waldeck's Mayan dictionary\";\n" +
        "Object  map \"sketch-map of Quintana Roo\";\n" +
        "Object  sodium_lamp \"sodium lamp\";\n" +
        "[ Initialise;\n" +
        "  TitlePage();\n" +
        "  location = Forest;\n" +
        "  move map to player;\n" +
        "  move nonobject to player;\n" +
        "  move sodium_lamp to player;\n" +
        "  move nonobject to nonplayer;\n" +
        "  move dictionary to player;\n" +
        "  StartDaemon(sodium_lamp);\n" +
        "  thedark.description =\n" +
        "      \"The darkness of ages presses in on you, and you feel\n" +
        "       claustrophobic.\";\n" +
        "  \"^^^Days of searching, days of thirsty hacking through the briars of\n" +
        "   the forest, but at last your patience was rewarded. A discovery!^\";\n" +
        "];");
    ip.setShow(InformParser.ANALYZE_INIT);
    ip.setViewType(InformParser.ALPHABETICAL_VIEW);
    pd = ip.parseWorker(null, mb);
    assertEquals(3, pd.root.getChildCount());
    assertEquals("dictionary", getAssetNameAt(pd.root, "0"));
    assertEquals("map", getAssetNameAt(pd.root, "1"));
    assertEquals("sodium_lamp", getAssetNameAt(pd.root, "2"));
  }

/*
  public void testExternalFile()
  {
    MockBuffer mb;
    InformParser ip = new InformParser("inform parser");
    SideKickParsedData pd;

    String fileData = "";
    try
    {
      File f = new File("d:\\rewind\\inform\\ruins.inf");
      FileInputStream in = new FileInputStream(f);
      byte[] buffer = new byte[(int) f.length()];
      while (-1 != in.read(buffer))
      {
        fileData += new String(buffer);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    mb = new MockBuffer(fileData);
    ip.setShow(InformParser.ANALYZE_INIT);
    pd = ip.parseWorker(null, mb);
  }
*/

  public void testFoundIn()
  {
    MockBuffer mb;
    InformParser ip = new InformParser("inform parser");
    SideKickParsedData pd;

    mb = new MockBuffer("Object bridge with found_in north_side south_side;\n" +
        "Object troll bridge;\n" +
        "Object axe troll;\n" +
        "Object ball_of_string troll;\n" +
        "Object north_side;\n" +
        "Object-> waypost;\n" +
        "Object south_side;");
    pd = ip.parseWorker(null, mb);

    assertEquals(2, pd.root.getChildCount());
    assertEquals("north_side", getAssetNameAt(pd.root, "0"));
      assertEquals(2, getTreeNodeAt(pd.root, "0").getChildCount());
      assertEquals("bridge", getAssetNameAt(pd.root, "00"));
        assertEquals(1, getTreeNodeAt(pd.root, "00").getChildCount());
        assertEquals("troll", getAssetNameAt(pd.root, "000"));
          assertEquals(2, getTreeNodeAt(pd.root, "000").getChildCount());
          assertEquals("axe", getAssetNameAt(pd.root, "0000"));
          assertEquals("ball_of_string", getAssetNameAt(pd.root, "0001"));
      assertEquals("waypost", getAssetNameAt(pd.root, "01"));
    assertEquals("south_side", getAssetNameAt(pd.root, "1"));
      assertEquals(1, getTreeNodeAt(pd.root, "1").getChildCount());
      assertEquals("bridge", getAssetNameAt(pd.root, "10"));
        assertEquals(1, getTreeNodeAt(pd.root, "10").getChildCount());
        assertEquals("troll", getAssetNameAt(pd.root, "100"));
          assertEquals(2, getTreeNodeAt(pd.root, "100").getChildCount());
          assertEquals("axe", getAssetNameAt(pd.root, "1000"));
          assertEquals("ball_of_string", getAssetNameAt(pd.root, "1001"));
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

  String getAssetNameAt(TreeNode rootNode, String pathString)
  {
    return ((InformAsset) getTreeNodeAt(rootNode, pathString).getUserObject()).getShortString();
  }


  public void testExtractEntities()
  {
    MockBuffer mb = new MockBuffer("   Class ! j00 are d00m3d LOL!\n" +
        "evilClass(10) with\n" +
        "  isParserDoomed 1, initial \"What?! Who's there?\" class ! does; the; has; the;\n" +
        " ! a comment^\";\n" +
        "  ORLibClass   evilClass1\n" +
        "evilClass2;\n\n" +
        "    \n" +
        "Object\n" +
        "      -> ->-> wickedClassy1 ! has brackets [ and comments with \"quotes\n" +
        "\"parsing with class; has to be done!!!\" has\n" +
        "light static\n" +
        "  ! with whole lines of comments\n" +
        "with name 'wicked' 'devious' 'horrible',\n" +
        "description \"[This is not the beginning of a function block\",\n" +
        "before [; print \"But this is inside of a function.\";\n" +
        " ! a comment^\";\n" +
        "],\n" +
        "initial [; print \"]It's all valid Inform, really\"; ],\n" +
        "class evilClass1 ORLibClass\n" +
        "  evilClass2;\n" +
        "\n" +
        "Object Machine \"Machine room\" with\n" +
        "description \"This room is practically littered with machines, devices,\n" +
        "gizmos, gimmicks, thingumabobs, thingumajigs... damn, I should get\n" +
        "either an education or a thesaurus!\",\n" +
        "n_to Monsters_room,\n" +
        "e_to Doctor,\n" +
        "has light;\n" +
        "\n" +
        "WickedClass\n" +
        "      -> ->-> -> wickedClassy2\n" +
        "does_indeed_have_a_parent has\n" +
        "light static\n" +
        "with name 'wicked' 'devious' 'horrible',\n" +
        "description \"[This is not the beginning of a function block\",\n" +
        "before [; print \"But this is inside of a function.\"; ],\n" +
        "initial [; print \"]It's all valid Inform, really\"; ],\n" +
        "found_in diningRoom Kitchen Ballroom\n" +
        "class evilClass1 ORLibClass\n" +
        "  evilClass2;");

    InformParser ip = new InformParser("inform parser");
    InformEntity[] entities = ip.extractEntities(null, mb);

    assertEquals(4, entities.length);

    assertEquals(new String("Class ! j00 are d00m3d LOL!\n" +
        "evilClass(10) with\n" +
        "  isParserDoomed 1, initial \"What?! Who's there?\" class ! does; the; has; the;\n" +
        " ! a comment^\";\n" +
        "  ORLibClass   evilClass1\n" +
        "evilClass2;").replaceAll(" +", " "),
        entities[0].getText().replaceAll(" +", " "));

    assertEquals(new String("Object\n" +
        "      -> ->-> wickedClassy1 ! has brackets [ and comments with \"quotes\n" +
        "\"parsing with class; has to be done!!!\" has\n" +
        "light static\n" +
        "  ! with whole lines of comments\n" +
        "with name 'wicked' 'devious' 'horrible',\n" +
        "description \"[This is not the beginning of a function block\",\n" +
        "before [; print \"But this is inside of a function.\";\n" +
        " ! a comment^\";\n" +
        "],\n" +
        "initial [; print \"]It's all valid Inform, really\"; ],\n" +
        "class evilClass1 ORLibClass\n" +
        "  evilClass2;").replaceAll(" +", " "),
        entities[1].getText().replaceAll(" +", " "));

    assertEquals(new String("Object Machine \"Machine room\" with\n" +
        "description \"This room is practically littered with machines, devices,\n" +
        "gizmos, gimmicks, thingumabobs, thingumajigs... damn, I should get\n" +
        "either an education or a thesaurus!\",\n" +
        "n_to Monsters_room,\n" +
        "e_to Doctor,\n" +
        "has light;").replaceAll(" +", " "),
        entities[2].getText().replaceAll(" +", " "));

    assertEquals(new String("WickedClass\n" +
        "      -> ->-> -> wickedClassy2\n" +
        "does_indeed_have_a_parent has\n" +
        "light static\n" +
        "with name 'wicked' 'devious' 'horrible',\n" +
        "description \"[This is not the beginning of a function block\",\n" +
        "before [; print \"But this is inside of a function.\"; ],\n" +
        "initial [; print \"]It's all valid Inform, really\"; ],\n" +
        "found_in diningRoom Kitchen Ballroom\n" +
        "class evilClass1 ORLibClass\n" +
        "  evilClass2;").replaceAll(" +", " "),
        entities[3].getText().replaceAll(" +", " "));

  }
}

