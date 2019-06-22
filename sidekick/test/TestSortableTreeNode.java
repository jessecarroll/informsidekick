package sidekick.test;

import junit.framework.TestCase;
import sidekick.inform.SortableTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: Naiobrin
 * Date: Aug 29, 2005
 * Time: 10:51:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSortableTreeNode extends TestCase {
  public void setUp()
  {
  }

  public void tearDown()
  {
  }

  public void testSort()
  {
    //create some out-of-order data in a tree
    SortableTreeNode root = new SortableTreeNode();
    SortableTreeNode subnode = new SortableTreeNode("subnode");
      subnode.add(new SortableTreeNode("item 2"));
      subnode.add(new SortableTreeNode("item 1"));
      subnode.add(new SortableTreeNode("Item 3"));
    root.add(new SortableTreeNode("Minnesota"));
    root.add(new SortableTreeNode("texas"));
    root.add(new SortableTreeNode("oklahoma"));
    root.add(subnode);
    root.add(new SortableTreeNode("Florida"));

    //sort the tree
    root.sort();

    //assess results
    assertEquals("Florida", root.getChildAt(0).toString());
    assertEquals("Minnesota", root.getChildAt(1).toString());
    assertEquals("oklahoma", root.getChildAt(2).toString());
    assertEquals(subnode, root.getChildAt(3));
      assertEquals("item 1", root.getChildAt(3).getChildAt(0).toString());
      assertEquals("item 2", root.getChildAt(3).getChildAt(1).toString());
      assertEquals("Item 3", root.getChildAt(3).getChildAt(2).toString());
    assertEquals("texas", root.getChildAt(4).toString());
  }

  public void testAdd()
  {
    SortableTreeNode root = new SortableTreeNode("rootnode");
    root.add(new SortableTreeNode("Minnesota"));
    root.add(new SortableTreeNode("texas"));
    root.add(1, new SortableTreeNode("oklahoma"));
    root.add(0, new SortableTreeNode("Wyoming"));

    assertEquals("Wyoming", root.getChildAt(0).toString());
    assertEquals("Minnesota", root.getChildAt(1).toString());
    assertEquals("oklahoma", root.getChildAt(2).toString());
    assertEquals("texas", root.getChildAt(3).toString());

    assertNotNull(root.getChildAt(0).getParent());
    assertEquals("rootnode", root.getChildAt(0).getParent().toString());
  }

  public void testProperties()
  {
    SortableTreeNode n = new SortableTreeNode("node");
    n.setProperty("test");
    assertTrue(n.hasProperty("test"));
  }

  public void testRecursiveClone()
  {
    //create some nested tree nodes
    SortableTreeNode house = new SortableTreeNode("house");
    SortableTreeNode firstFloor = new SortableTreeNode("firstFloor");
    SortableTreeNode bathroom = new SortableTreeNode("bathroom");
    SortableTreeNode bedroom1 = new SortableTreeNode("bedroom1");
    SortableTreeNode bedroom2 = new SortableTreeNode("bedroom2");
    Object livingroomUserObject = new String("livingroom");
    SortableTreeNode livingroom = new SortableTreeNode(livingroomUserObject);
    SortableTreeNode kitchen = new SortableTreeNode("kitchen");
    SortableTreeNode lowerhall = new SortableTreeNode("lowerhall");
    SortableTreeNode secondFloor = new SortableTreeNode("secondFloor");
    SortableTreeNode upperhall = new SortableTreeNode("upperhall");
    SortableTreeNode attic = new SortableTreeNode("attic");
    Object bedroomUserObject = new String("bedroom3");
    SortableTreeNode bedroom3 = new SortableTreeNode(bedroomUserObject);

    house.add(firstFloor);
    house.add(secondFloor);

    firstFloor.add(bathroom);
    firstFloor.add(bedroom1);
    firstFloor.add(bedroom2);
    firstFloor.add(livingroom);
    firstFloor.add(kitchen);
    firstFloor.add(lowerhall);

    secondFloor.add(upperhall);
    secondFloor.add(attic);
    secondFloor.add(bedroom3);

    //make a recursive copy of these nodes
    SortableTreeNode houseClone = house.recursiveClone();
    assertFalse(house.hasProperty("ignoreInTreePathSearch"));
    assertTrue(houseClone.hasProperty("ignoreInTreePathSearch"));

    //verify that the node objects are distinct, but the user objects associated with
    // the nodes are the same between the clone and the original
    SortableTreeNode firstFloorClone = (SortableTreeNode) houseClone.getChildAt(0);
    assertEquals("firstFloor", firstFloorClone.toString());
    assertNotSame(firstFloor, firstFloorClone);

    SortableTreeNode bathroomClone = (SortableTreeNode) firstFloorClone.getChildAt(0);
    SortableTreeNode bedroom1Clone = (SortableTreeNode) firstFloorClone.getChildAt(1);
    SortableTreeNode bedroom2Clone = (SortableTreeNode) firstFloorClone.getChildAt(2);
    SortableTreeNode livingroomClone = (SortableTreeNode) firstFloorClone.getChildAt(3);
    SortableTreeNode kitchenClone = (SortableTreeNode) firstFloorClone.getChildAt(4);
    SortableTreeNode lowerhallClone = (SortableTreeNode) firstFloorClone.getChildAt(5);
    assertEquals("bathroom", bathroomClone.toString());
    assertEquals("bedroom1", bedroom1Clone.toString());
    assertEquals("bedroom2", bedroom2Clone.toString());
    assertEquals("livingroom", livingroomClone.toString());
    assertEquals("kitchen", kitchenClone.toString());
    assertEquals("lowerhall", lowerhallClone.toString());
    assertNotSame(bathroom, bathroomClone);
    assertNotSame(bedroom1, bedroom1Clone);
    assertNotSame(bedroom2, bedroom2Clone);
    assertNotSame(livingroom, livingroomClone);
    assertNotSame(kitchen, kitchenClone);
    assertNotSame(lowerhall, lowerhallClone);
    assertSame(livingroomUserObject, livingroomClone.getUserObject());

    SortableTreeNode secondFloorClone = (SortableTreeNode) houseClone.getChildAt(1);
    assertEquals("secondFloor", secondFloorClone.toString());
    assertNotSame(secondFloor, secondFloorClone);

    SortableTreeNode upperhallClone = (SortableTreeNode) secondFloorClone.getChildAt(0);
    SortableTreeNode atticClone = (SortableTreeNode) secondFloorClone.getChildAt(1);
    SortableTreeNode bedroom3Clone = (SortableTreeNode) secondFloorClone.getChildAt(2);
    assertEquals("upperhall", upperhallClone.toString());
    assertEquals("attic", atticClone.toString());
    assertEquals("bedroom3", bedroom3Clone.toString());
    assertNotSame(upperhall, upperhallClone);
    assertNotSame(attic, atticClone);
    assertNotSame(bedroom3, bedroom3Clone);
    assertSame(bedroomUserObject, bedroom3Clone.getUserObject());
  }
}
