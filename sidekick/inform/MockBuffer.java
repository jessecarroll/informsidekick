package sidekick.inform;

import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import java.util.ArrayList;

/**
 * A mock buffer meant to emulate jEdit's <code>Buffer</code> for testing purposes only.
 * <code>Buffer</code> inherits from <code>PlainDocument</code>, so some of the methods
 * are already implemented by that class (or can be augmented from its implementation),
 * but <code>Buffer</code> builds on <code>PlainDocument</code> quite a bit, so there's a
 * lot that is implemented from scratch.
 *
 * @author Jesse Carroll
 * @version $Revision: 1.0 $
 */
public class MockBuffer extends PlainDocument {

  private int lineCount = 0;
  private ArrayList lineOffsetArrayList = null;

  /**
   * Apparently, JEdit's <code>Buffer</code> subclass of <code>PlainDocument</code>
   * supresses the exception this function can throw, so the mock object needs to do the
   * same.
   */
  public Position createPosition(int i)
  {
    try
    {
      return super.createPosition(i);
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public MockBuffer()
  {
    super();
  }

  public MockBuffer(String s)
  {
    try
    {
      insertString(0, s, null);
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }
  }

  public String getName()
  {
    return "mock buffer";
  }

  public String getEntireText()
  {
    String text = null;
    try
    {
      text = getText(0, getLength());
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    return text;
  }

  /**
   * Appends line counting and re-indexing code to string insertion.
   *
   * @throws BadLocationException
   */
  public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException
  {
    super.insertString(offs, str, a);

    //get the entire text of the document into a string
    String text = getEntireText();

    //re-initialize line offset index
    lineOffsetArrayList = new ArrayList();

    //special case: if the text is empty, it has no lines at all
    if (text.length() == 0)
    {
      lineCount = 0;
      return;
    }

    //count endlines and record index line offsets along the way; since the special
    // case of an empty text as already been dealt with, the text will always
    // have at least 1 line beginning at an offset of 0
    int offset = 0;
    lineCount = 1;
    lineOffsetArrayList.add(new Integer(offset));
    while ((offset = text.indexOf("\n", offset)) != -1)
    {
      //System.out.println(offset);
      offset++;
      lineCount++;
      lineOffsetArrayList.add(new Integer(offset));
    }

    //special case: if the text ends in an endline, there is nothing at all
    // after the endline, so it shouldn't be counted as a line
    if (text.charAt(text.length() - 1) == '\n')
      lineCount--;
  }

  public int getLineCount()
  {
    return lineCount;
  }

  public int getLineStartOffset(int lineNumber)
  {
    //handle out of bounds specials cases
    if (lineNumber < 0 || lineNumber >= getLineCount())
      throw new ArrayIndexOutOfBoundsException(lineNumber);

    //use the index that was built during the call to getLineCount to return an offset
    return ((Integer) lineOffsetArrayList.get(lineNumber)).intValue();
  }

  public int getLineEndOffset(int lineNumber)
  {
    return getLineStartOffset(lineNumber) + getLineText(lineNumber).length() + 1;
  }

  public String getLineText(int lineNumber)
  {
    //handle out of bounds specials cases
    if (lineNumber < 0 || lineNumber >= getLineCount())
      throw new ArrayIndexOutOfBoundsException(lineNumber);

    //get the offset of the requested line
    int startOffset = getLineStartOffset(lineNumber);

    //determine the end offset of the requested line
    String text = getEntireText();
    int endOffset = (lineNumber + 1 < lineCount) ?
        getLineStartOffset(lineNumber + 1) - 1 :
        text.length();

    //extract and return all text until the end of the line or the end of the text
    return text.substring(startOffset, endOffset);
  }

  public String getText(int start, int length)
  {
    String s = "";
    try
    {
      s = super.getText(start, length);
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }

    return s;
  }
}
