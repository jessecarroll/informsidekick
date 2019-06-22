package sidekick.test;

import junit.framework.*;
import sidekick.inform.MockBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: Naiobrin
 * Date: Aug 29, 2005
 * Time: 12:55:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestMockBuffer extends TestCase {
  public void setUp()
  {
  }

  public void tearDown()
  {
  }

  public void testGetLineCount()
  {
    MockBuffer mb = new MockBuffer();
    try
    {
      mb.insertString(0, "\n\nblah blah\nfdsgsg\n\nfdsfds", null);
      assertEquals(6, mb.getLineCount());
      mb.remove(0, mb.getLength());

      mb.insertString(0, "", null);
      assertEquals(0, mb.getLineCount());
      mb.remove(0, mb.getLength());

      mb.insertString(0, "\n\n\n\n\n\n\n\n\n\n", null);
      assertEquals(10, mb.getLineCount());
      mb.remove(0, mb.getLength());

      mb.insertString(0, "\n\n", null);
      assertEquals(2, mb.getLineCount());
      mb.remove(0, mb.getLength());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void testGetLineStartOffset()
  {
    MockBuffer mb = new MockBuffer();
    try
    {
      mb.insertString(0, "\n\nblah blah\nfdsgsg\n\nfdsfds", null);
      assertEquals(0, mb.getLineStartOffset(0));
      assertEquals(1, mb.getLineStartOffset(1));
      assertEquals(2, mb.getLineStartOffset(2));
      assertEquals(12, mb.getLineStartOffset(3));
      assertEquals(19, mb.getLineStartOffset(4));
      assertEquals(20, mb.getLineStartOffset(5));
      //mb.remove(0, mb.getLength());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void testGetLineEndOffset()
  {
    MockBuffer mb = new MockBuffer();
    try
    {
      mb.insertString(0, "\n\nblah blah\nfdsgsg\n\nfdsfds", null);
      assertEquals(1, mb.getLineEndOffset(0));
      assertEquals(2, mb.getLineEndOffset(1));
      assertEquals(12, mb.getLineEndOffset(2));
      assertEquals(19, mb.getLineEndOffset(3));
      assertEquals(20, mb.getLineEndOffset(4));
      assertEquals(27, mb.getLineEndOffset(5));
      //mb.remove(0, mb.getLength());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void testGetLineText()
  {
    MockBuffer mb = new MockBuffer();
    try
    {
      mb.insertString(0, "\n\nblah blah\nfdsgsg\n\nfdsfds", null);
      assertEquals("", mb.getLineText(0));
      assertEquals("", mb.getLineText(1));
      assertEquals("blah blah", mb.getLineText(2));
      assertEquals("fdsgsg", mb.getLineText(3));
      assertEquals("", mb.getLineText(4));
      assertEquals("fdsfds", mb.getLineText(5));
      mb.remove(0, mb.getLength());

      mb.insertString(0, "", null);
      assertEquals(0, mb.getLineCount());
      mb.remove(0, mb.getLength());

      mb.insertString(0, "\n\n\n\n\n\n\n\n\n\n", null);
      assertEquals(10, mb.getLineCount());
      mb.remove(0, mb.getLength());

      mb.insertString(0, "\n\n", null);
      assertEquals(2, mb.getLineCount());
      mb.remove(0, mb.getLength());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
