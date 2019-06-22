package sidekick.test;

import junit.framework.TestCase;
import sidekick.inform.InformEntity;
import sidekick.inform.MockBuffer;

import javax.swing.text.Position;

/**
 * Created by IntelliJ IDEA. User: Naiobrin Date: Sep 22, 2005 Time: 5:00:34 AM To change
 * this template use File | Settings | File Templates.
 */
public class TestInformEntity extends TestCase {

  String constant1Text = "Constant\nINTRO 0;";
  String constant2Text = "Constant F_INTRO_PHASE = 0;";
  String constant3Text = "Constant DEBUG;";

  String class1Text = "Class  Block\n" +
      "  with description \"Just a child's building block, four inches on a side.\",\n" +
      "\n" +
      "       !   The parse_name routine below ensures that \"take blocks\"\n" +
      "       !   works correctly:\n" +
      "\n" +
      "       parse_name\n" +
      "       [ i j;\n" +
      "         for (::)\n" +
      "         {   j=NextWord();\n" +
      "             if (j=='block' or 'cube' or 'building' or (self.name)) i++;\n" +
      "             else\n" +
      "             {   if (j=='blocks' or 'cubes')\n" +
      "                 {   parser_action=##PluralFound; i++; }\n" +
      "                 else return i;\n" +
      "             }\n" +
      "         }\n" +
      "       ],\n" +
      "\n" +
      "       describe\n" +
      "       [ c d e;\n" +
      "           d = child(self);\n" +
      "           while (d~=0 && d ofclass Block)\n" +
      "           {   c++; e=d; d=child(d); }\n" +
      "           if (c==0) rfalse;\n" +
      "           print \"^There is a pile of building blocks here, \";\n" +
      "           while (c>=0)\n" +
      "           {   print (address) e.name;  ! Sneaky: print the \"name\" out\n" +
      "               if (c>0) print \" on \";   ! using its dictionary address\n" +
      "               c--; e=parent(e);\n" +
      "           }\n" +
      "           \".\";\n" +
      "       ],\n" +
      "       before\n" +
      "       [ c;\n" +
      "         PutOn:\n" +
      "           if (second ofclass Block)\n" +
      "           {   if (child(second)~=0 && child(second) ofclass Block)\n" +
      "                   \"There's no room on the top of one cube for two more, side \n" +
      "                    by side.\";\n" +
      "           }\n" +
      "           else\n" +
      "               print \"(They're really intended \n" +
      "                      to be piled on top of each other.)^\";\n" +
      "           c=second; while (c ofclass Block) c=parent(c);\n" +
      "           if (c~=location or mantelpiece) \"Too unsteady a base.\";\n" +
      "       ],\n" +
      "       after\n" +
      "       [ c stack;\n" +
      "         PutOn:\n" +
      "           stack=noun;\n" +
      "           while (parent(stack) ofclass Block) { stack=parent(stack); c++; }\n" +
      "           if (c<2)\n" +
      "           {   if (Chris has general) rtrue;\n" +
      "               rfalse;\n" +
      "           }\n" +
      "           if (c==2) \"The pile of three cubes is unsteady, but viable.\";\n" +
      "           if (Chris has general)\n" +
      "           {   Achieved(3);\n" +
      "               \"^Expertly he keeps the pile of four cubes stable.\";\n" +
      "           }\n" +
      "           stack=noun;\n" +
      "           while (parent(stack) ofclass Block)\n" +
      "           {   c=stack; stack=parent(stack); move c to location; }\n" +
      "           \"The pile of four cubes wobbles, wobbles, steadies... and suddenly \n" +
      "            collapses!\";\n" +
      "         Take:\n" +
      "           stack=child(noun); if (stack==0) rfalse;\n" +
      "           while (stack~=0)\n" +
      "           { c=stack; stack=child(stack); move c to location; }\n" +
      "           \"Your pile of cubes is collapsed as a result.\";\n" +
      "       ],\n" +
      "  has  supporter;";

  String class2Text = "Class ! j00 are d00m3d LOL!\n" +
      "evilClass(10) with\n" +
      "  isParserDoomed 1, initial \"What?! Who's there?\" class ! does; the; has; the;\n" +
      " ! a comment^\";\n" +
      "  ORLibClass   evilClass1\n" +
      "evilClass2;";

  String function1Text = "[ Initialise;\n" +
      "        TitlePage();\n" +
      "        location = Forest;\n" +
      "        move map to warthog;\n" +
      "        player = warthog;\n" +
      "        move sodium_lamp to player;\n" +
      "        move dictionary to player;\n" +
      "        StartDaemon(sodium_lamp);\n" +
      "        thedark.description =\n" +
      "            \"The darkness of ages presses in on you, and you feel\n" +
      "             claustrophobic.\";\n" +
      "        \"^^^Days of searching, days of thirsty hacking through the briars of\n" +
      "         the forest, but at last your patience was rewarded. A discovery!^\";\n" +
      "        ];";

  String global1Text = "Global GamePhase = INTRO;";

  String object1Text = "Object\n" +
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
      "  evilClass2;";

  String object1NoComments = "Object\n" +
      "      -> ->-> wickedClassy1\n" +
      "\"parsing with class; has to be done!!!\" has\n" +
      "light static\n" +
      "with name 'wicked' 'devious' 'horrible',\n" +
      "description \"[This is not the beginning of a function block\",\n" +
      "before [; print \"But this is inside of a function.\"; ],\n" +
      "initial [; print \"]It's all valid Inform, really\"; ],\n" +
      "class evilClass1 ORLibClass\n" +
      "  evilClass2;";

  String object1SimplifiedText = "Object\n" +
      "      -> ->-> wickedClassy1 \n" +
      " has\n" +
      "light static\n" +
      "\n" +
      "with name   ,\n" +
      "description ,\n" +
      "before ,\n" +
      "initial ,\n" +
      "class evilClass1 ORLibClass\n" +
      "  evilClass2;";

  String object2Text = "Object Machine \"Machine room\" with\n" +
      "description \"This room is practically littered with machines, devices,\n" +
      "gizmos, gimmicks, thingumabobs, thingumajigs... damn, I should get\n" +
      "either an education or a thesaurus!\",\n" +
      "n_to Monsters_room,\n" +
      "e_to Doctor,\n" +
      "has light;";

  String object3Text = "WickedClass\n" +
      "      -> ->-> -> wickedClassy2\n" +
      "does_indeed_have_a_parent has\n" +
      "light static\n" +
      "with name 'wicked' 'devious' 'horrible',\n" +
      "description \"[This is not the beginning of a function block\",\n" +
      "before [; print \"But this is inside of a function.\"; ],\n" +
      "initial [; print \"]It's all valid Inform, really\"; ],\n" +
      "found_in diningRoom Kitchen Ballroom\n" +
      "class evilClass1 ORLibClass\n" +
      "  evilClass2;";

  String object4Text = "Object ->-> -> \"Speck of Dirt\";";

  String object5Text = "Object \"fluorescent juggling ball\" Chris\n" +
      "  with initial \"On the floor is a fluorescent juggling ball!\",\n" +
      "       name \"fluorescent\" \"juggling\" \"ball\",\n" +
      "       description \"It glows with soft light.\"\n" +
      "  has  light;";

  String object6Text = "Object\n" +
      "->\n" +
      "totem \"mysterious totem pole\" with\n" +
      "  name 'totem',\n" +
      "  initial \"THE TOTEM IS HERE\",\n" +
      "  class testClass testClass1 testClass2\n" +
      "  has static;";

  String verb1Text = "Verb 'knock'\n" + "  * -> Knock\n" + "  * 'on' noun -> Knock;";

  String verb2Text = "Extend 'ask' first\n" +
      "        * creature 'about' scope=TopicScope -> Ask;";

  String verb3Text = "Verb 'yell' 'scream' 'holler'\n" +
      "  * -> Shout\n" +
      "  * 'to'/'at' noun -> Shout;";

  String verb4Text = "Extend only 'shout' replace\n" +
      "  * -> Shout\n" +
      "  * 'to'/'at' noun -> Shout;";

  public void testParse()
  {
    MockBuffer mb = new MockBuffer("dummy");
    Position p1 = mb.createPosition(0);
    Position p2 = mb.createPosition(1);
    InformEntity class1 = new InformEntity(class1Text, p1, p2, 0);
    InformEntity class2 = new InformEntity(class2Text, p1, p2, 0);
    InformEntity constant1 = new InformEntity(constant1Text, p1, p2, 0);
    InformEntity constant2 = new InformEntity(constant2Text, p1, p2, 0);
    InformEntity constant3 = new InformEntity(constant3Text, p1, p2, 0);
    InformEntity function1 = new InformEntity(function1Text, p1, p2, 0);
    InformEntity global1 = new InformEntity(global1Text, p1, p2, 0);
    InformEntity object1 = new InformEntity(object1Text, p1, p2, 0);
    InformEntity object2 = new InformEntity(object2Text, p1, p2, 0);
    InformEntity object3 = new InformEntity(object3Text, p1, p2, 0);
    InformEntity object4 = new InformEntity(object4Text, p1, p2, 0);
    InformEntity object5 = new InformEntity(object5Text, p1, p2, 0);
    InformEntity object6 = new InformEntity(object6Text, p1, p2, 0);
    InformEntity verb1 = new InformEntity(verb1Text, p1, p2, 0);
    InformEntity verb2 = new InformEntity(verb2Text, p1, p2, 0);
    InformEntity verb3 = new InformEntity(verb3Text, p1, p2, 0);
    InformEntity verb4 = new InformEntity(verb4Text, p1, p2, 0);
    InformEntity malformed1 = new InformEntity(";", p1, p2, 0);

    //verify correct parsing of constants
    assertEquals(InformEntity.CONSTANT_TYPE, constant1.getType());
    assertEquals("INTRO", constant1.getIdentifier());
    assertEquals(InformEntity.CONSTANT_TYPE, constant2.getType());
    assertEquals("F_INTRO_PHASE", constant2.getIdentifier());
    assertEquals(InformEntity.CONSTANT_TYPE, constant3.getType());
    assertEquals("DEBUG", constant3.getIdentifier());

    //verify correct parsing of classes
    assertEquals(InformEntity.CLASS_TYPE, class1.getType());
    assertEquals("Block", class1.getIdentifier());
    assertEquals(0, class1.getClasses().length);

    assertEquals(InformEntity.CLASS_TYPE, class2.getType());
    assertEquals("evilClass", class2.getIdentifier());
    assertEquals(3, class2.getClasses().length);
    assertEquals("ORLibClass", class2.getClasses()[0]);
    assertEquals("evilClass1", class2.getClasses()[1]);
    assertEquals("evilClass2", class2.getClasses()[2]);

    //verify correct parsing of functions
    assertEquals(InformEntity.FUNCTION_TYPE, function1.getType());
    assertEquals("Initialise", function1.getIdentifier());

    //verify correct parsing of globals
    assertEquals(InformEntity.GLOBAL_TYPE, global1.getType());
    assertEquals("GamePhase", global1.getIdentifier());

    //verify correct parsing of objects
    assertEquals(InformEntity.OBJECT_TYPE, object1.getType());
    assertEquals(3, object1.getArrowLevel());
    assertEquals("wickedClassy1", object1.getIdentifier());
    assertEquals("parsing with class; has to be done!!!", object1.getTextualDescription());
    assertEquals(4, object1.getClasses().length);
    assertEquals("Object", object1.getClasses()[0]);
    assertEquals("evilClass1", object1.getClasses()[1]);
    assertEquals("ORLibClass", object1.getClasses()[2]);
    assertEquals("evilClass2", object1.getClasses()[3]);
    assertEquals(0, object1.getParent().length());

    assertEquals(InformEntity.OBJECT_TYPE, object2.getType());
    assertEquals(0, object2.getArrowLevel());
    assertEquals("Machine", object2.getIdentifier());
    assertEquals("Machine room", object2.getTextualDescription());
    assertEquals(1, object2.getClasses().length);
    assertEquals("Object", object2.getClasses()[0]);
    assertEquals(0, object2.getParent().length());

    assertEquals(InformEntity.OBJECT_TYPE, object3.getType());
    assertEquals(4, object3.getArrowLevel());
    assertEquals("wickedClassy2", object3.getIdentifier());
    assertEquals("", object3.getTextualDescription());
    assertEquals(4, object3.getClasses().length);
    assertEquals("WickedClass", object3.getClasses()[0]);
    assertEquals("evilClass1", object3.getClasses()[1]);
    assertEquals("ORLibClass", object3.getClasses()[2]);
    assertEquals("evilClass2", object3.getClasses()[3]);
    assertEquals(3, object3.getFoundIn().length);
    assertEquals("diningRoom", object3.getFoundIn()[0]);
    assertEquals("Kitchen", object3.getFoundIn()[1]);
    assertEquals("Ballroom", object3.getFoundIn()[2]);

    assertEquals(InformEntity.OBJECT_TYPE, object4.getType());
    assertEquals(3, object4.getArrowLevel());
    assertEquals("Speck of Dirt", object4.getTextualDescription());
    assertEquals(1, object4.getClasses().length);
    assertEquals("Object", object4.getClasses()[0]);
    assertEquals(0, object4.getParent().length());

    assertEquals(InformEntity.OBJECT_TYPE, object5.getType());
    assertEquals(0, object5.getArrowLevel());
    assertEquals("fluorescent juggling ball", object5.getTextualDescription());
    assertEquals(1, object5.getClasses().length);
    assertEquals("Object", object5.getClasses()[0]);
    assertEquals("Chris", object5.getParent());

    assertEquals(InformEntity.OBJECT_TYPE, object6.getType());
    assertEquals(1, object6.getArrowLevel());
    assertEquals("mysterious totem pole", object6.getTextualDescription());
    assertEquals(4, object6.getClasses().length);
    assertEquals("Object", object6.getClasses()[0]);
    assertEquals("testClass", object6.getClasses()[1]);
    assertEquals("testClass1", object6.getClasses()[2]);
    assertEquals("testClass2", object6.getClasses()[3]);
    assertEquals(0, object6.getParent().length());

    assertEquals(InformEntity.VERB_TYPE, verb1.getType());
    assertEquals("knock", verb1.getVerbs()[0]);

    assertEquals(InformEntity.VERB_TYPE, verb2.getType());
    assertEquals("ask", verb2.getVerbs()[0]);

    assertEquals(InformEntity.VERB_TYPE, verb3.getType());
    assertEquals("yell", verb3.getVerbs()[0]);
    assertEquals("scream", verb3.getVerbs()[1]);
    assertEquals("holler", verb3.getVerbs()[2]);

    assertEquals(InformEntity.VERB_TYPE, verb4.getType());
    assertEquals("shout", verb4.getVerbs()[0]);

    assertEquals(InformEntity.UNKNOWN_TYPE, malformed1.getType());
  }

  public void testSimplify()
  {
    assertEquals(object1SimplifiedText.replaceAll("\\s+", " "), InformEntity.simplify(object1Text).replaceAll("\\s+", " "));
    assertEquals(object1NoComments.replaceAll("\\s+", " "), InformEntity.removeComments(object1Text).replaceAll("\\s+", " "));
  }

  public void testArrows()
  {
    MockBuffer mb = new MockBuffer("dummy");
    Position p1 = mb.createPosition(0);
    Position p2 = mb.createPosition(1);
    InformEntity ie1 = new InformEntity("Object-> -> id \"a test object\";", p1, p2, 0);
    InformEntity ie2 = new InformEntity("Object-> ->id \"a test object\";", p1, p2, 0);
    InformEntity ie3 = new InformEntity("Object ->id \"a test object\";", p1, p2, 0);
    InformEntity ie4 = new InformEntity("Object-> -> -> id \"a test object\";", p1, p2, 0);
    InformEntity ie5 = new InformEntity("Object-> ->->-> -> id \"a test object\";", p1, p2, 0);

    assertEquals(2, ie1.getArrowLevel());
    assertEquals(2, ie2.getArrowLevel());
    assertEquals(1, ie3.getArrowLevel());
    assertEquals(3, ie4.getArrowLevel());
    assertEquals(5, ie5.getArrowLevel());
  }
}
