package sidekick.inform;

import org.gjt.sp.jedit.View;
import sidekick.SideKickCompletion;


/**
 * A start at a completion class -- very very basic.
 *
 * @author Jesse Carroll
 * @version $Revision: 1.0 $
 */
public class InformSideKickCompletion extends SideKickCompletion {
  private String[] attributes = {"absent", "animate", "clothing", "concealed", "container", "door", "edible", "enterable", "female", "general", "light", "lockable", "locked", "male", "moved", "neuter", "on", "open", "openable", "pluralname", "proper", "scenery", "scored", "static", "supporter", "switchable", "talkable", "transparent", "visited", "workflag", "worn"};

  String partialText = "";

  public InformSideKickCompletion(View view, String partialText)
  {
    super(view, partialText);

    //save the partial text so the keystroke handler can see it
    this.partialText = partialText;

    //only put items in the list that begin with the partial text being completed
    // and are longer than the partial text being completed
    for (int i = 0; i < attributes.length; i++)
    {
      if (attributes[i].length() > partialText.length() &&
          attributes[i].startsWith(partialText))
      {
        items.add(attributes[i]);
      }
    }
  }

  /**
   * Parsers providing completion have to extend this function if they want to have
   * backspaces typed while the completion box is up to be handled properly.  To prevent
   * the completion popup from lingering on the screen after
   */
  public boolean handleKeystroke(int selectedIndex, char keyChar)
  {
    //handle backspace
    if (keyChar == '\b')
    {
      //determine whether completion should continue after this backspace; if the
      // the character being deleted is a space, completion stops
      int caret = textArea.getCaretPosition();
      boolean continueCompletion = !textArea.getText(caret - 1, 1).equals(" ");
      //send a backspace to the text editor
      textArea.backspace();

      return continueCompletion;
    }

    //if this keystroke will cause the partial text to exactly match one of the
    // completion items, return false to get completion to stop and the popup to go away
    partialText += keyChar;
    if (items.size() == 1 && items.get(0).toString().equals(partialText))
    {
      super.handleKeystroke(selectedIndex, keyChar);
      return false;
    }

    //let the parent handle all other cases
    return super.handleKeystroke(selectedIndex, keyChar);
  }
}
