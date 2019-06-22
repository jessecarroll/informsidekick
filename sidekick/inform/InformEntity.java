package sidekick.inform;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a block of Inform code which forms a complete semicolon-terminated entity.
 * You provide it with a block of text when creating a new instance, it parses the text
 * and makes various bits of information about the entity available via getters.
 *
 * @author Jesse Carroll
 * @version $Revision: 1.0 $
 */
public class InformEntity {

  //entity types
  public static final int CLASS_TYPE = 0;
  public static final int CONSTANT_TYPE = 1;
  public static final int FUNCTION_TYPE = 2;
  public static final int GLOBAL_TYPE = 3;
  public static final int VERB_TYPE = 4;
  public static final int OBJECT_TYPE = 5;
  public static final int UNKNOWN_TYPE = 6;

  //object parsing phases
  public static final int HEADER_SEGMENT = 0;
  public static final int HAS_SEGMENT = 1;
  public static final int WITH_SEGMENT = 2;
  public static final int CLASS_SEGMENT = 3;

  //([;,\[\]\{\}])|([^\s;,]+)
  //\[[^\]]+\]
  //\"[^\"]+\"
  public InformEntity(String entityText, Position startPosition, Position endPosition, int lineNumber)
  {
    Matcher m;

    //store the original entity text and location
    this.text = entityText.trim();
    this.startPosition = startPosition;
    this.endPosition = endPosition;
    this.lineNumber = lineNumber;

    //determine the entity type by examining the entity's first token
    m = Pattern.compile("^[^\\s;,]+").matcher(text);
    if (m.lookingAt())
    {
      String s = m.group(0).toLowerCase();

      if (s.startsWith("class"))
        type = CLASS_TYPE;
      else if (s.startsWith("constant"))
        type = CONSTANT_TYPE;
      else if (s.startsWith("["))
        type = FUNCTION_TYPE;
      else if (s.startsWith("global"))
        type = GLOBAL_TYPE;
      else if (s.startsWith("verb") || s.startsWith("extend"))
        type = VERB_TYPE;
      else
        type = OBJECT_TYPE;
    }
    else
    {
      type = UNKNOWN_TYPE;
      return;
    }

    //processing for Constant and Global entities (which have a similar form):
    if (type == CONSTANT_TYPE || type == GLOBAL_TYPE)
    {
      String[] textArray = text.split("[\\s;,]+");
      if (textArray.length > 1) this.identifier = textArray[1];
    }

    //processing for Function entities:
    else if (type == FUNCTION_TYPE)
    {
      m = Pattern.compile("^\\s*\\[\\s*(\\w+)").matcher(text);
      if (m.lookingAt())
      {
        this.identifier = m.group(1);
      }
    }

    //processing for Verb entities:
    else if (type == VERB_TYPE)
    {
      /**
       * Tokenize the text, looking for quoted tokens (which are the verbs affected by
       * this entity) until an asterisk occurs, ending the header portion.
       *
       * Sample verb entity:
       *   Verb 'yell' 'scream' 'holler'
       *     * -> Shout
       *     * 'to'/'at' noun -> Shout;
       */
      m = Pattern.compile("[^\\s\\*;,\\(\\)]+|,|;|\\*").matcher(text);
      for (int tokenNumber = 0; m.find(); tokenNumber++)
      {
        String token = m.group(0);

        if (tokenNumber == 0)
          continue;
        else if (token.matches("\"[^\"]+\"") || token.matches("'[^']+'"))
        {
          verbs.add(token.substring(1, token.length() - 1));
        }
        else if (token.equals("*")) break;
      }
    }

    //processing for Class entities:
    else if (type == CLASS_TYPE)
    {
      //simplify the entity by removing comments, bracketed sections and quoted sections
      String s = simplify(text);

      /**
       * Tokenize the remaining text.  Tokens are commas, semicolons, and contiguous
       * runs of characters that are not commas, semicolons, parens, or whitespace.
       */
      m = Pattern.compile("[^\\s;,\\(\\)]+|,|;").matcher(s);
      int segment = HEADER_SEGMENT;
      for (int tokenNumber = 0; m.find(); tokenNumber++)
      {
        String token = m.group(0);
        String tokenLower = token.toLowerCase();

        //first token is the entity class, which is always "Class" -- skip it
        if (tokenNumber == 0)
          continue;

        //second token is the name of the class
        else if (tokenNumber == 1)
          this.identifier = token;

        //note the beginning of the "has", "with", and "class" segments
        else if (tokenLower.equals("has"))
          segment = HAS_SEGMENT;
        else if (tokenLower.equals("with"))
          segment = WITH_SEGMENT;
        else if (tokenLower.equals("class"))
          segment = CLASS_SEGMENT;

        //semicolon signifies the end of the entity
        else if (token.equals(";"))
          break;

        else
        {
          //process according to which segment we're in
          switch (segment)
          {
          //a class entity may have a class segment to indicate that it inherits from
          // another class; it is a whitespace-separated list of parent classes
          // terminated by a comma
          case CLASS_SEGMENT:
            if (token.equals(",")) continue;

            classes.add(token);
            break;
          }
        }
      }
    }

    //processing for Object entities:
    else if (type == OBJECT_TYPE)
    {
      final Pattern tokenPattern = Pattern.compile("[^\\s>;,\"\\(\\)\\-]+|,|;|\"|(->)+");

      /**
       * Before parsing the body of an object, the text is simplified by removing all
       * text occurring inside of double quotes, brackets, and comments.  This makes
       * parsing much easier, but presents a small problem: the optional textual
       * description occuring in the header is contained in quotes.  The solution:
       * parse the header first, then simplify, then parse the body using the simplified
       * text.
       */

      //parse the header:
      String textNoComments = removeComments(text);
      log("Entity with no comments:\n" + textNoComments);
      m = tokenPattern.matcher(textNoComments);
      int textualDescriptionStart = 0;
      int textualDescriptionEnd = 0;
      for (int tokenNumber = 0; m.find(); tokenNumber++)
      {
        String token = m.group(0);
        String tokenLower = token.toLowerCase();

        //first token may be the name of a class instead of "object"
        if (tokenNumber == 0)
        {
          this.classes.add(token);
        }

        //watch for the beginning and end of the textual description
        else if (token.equals("\""))
        {
          if (textualDescriptionStart == 0)
            textualDescriptionStart = textNoComments.indexOf("\"");
          else
          {
            textualDescriptionEnd = textNoComments.indexOf("\"",
                textualDescriptionStart + 1);
            this.textual_description = textNoComments.substring(
                textualDescriptionStart + 1,
                textualDescriptionEnd);
          }
        }

        //all tokens should be skipped while we're inside of the textual description
        else if (textualDescriptionStart > 0 && textualDescriptionEnd == 0)
          continue;

        //watch for arrows
        else if (token.startsWith("->"))
        {
          if (token.matches("(->)+"))
          {
            this.arrowLevel += token.length() / 2;
          }
        }

        //watch for the end of the header coming before any quotes, which indicates that
        // there is no textual description for this object
        else if (tokenLower.equals("has") ||
            tokenLower.equals("with") ||
            tokenLower.equals("class") ||
            token.equals(";"))
          break;

        //watch for an identifier or parent
        else if (token.matches("\\w+"))
        {
          if (this.textual_description.length() > 0 || this.identifier.length() > 0)
            this.parent = token;
          else
            this.identifier = token;
        }
      }

      /**
       * With the header parsing out of the way, simplify the entity by
       * removing double quotes, bracketed sections, and comments.
       */
      String s = simplify(text);

      /**
       * Tokenize the simplified text and extract the important bits from the object
       * body.
       */
      m = tokenPattern.matcher(s);
      int segment = HEADER_SEGMENT;
      String property = "";
      for (int tokenNumber = 0; m.find(); tokenNumber++)
      {
        String token = m.group(0);
        String tokenLower = token.toLowerCase();

        //note the beginning of the "has", "with", and "class" segments
        if (tokenLower.equals("has"))
          segment = HAS_SEGMENT;
        else if (tokenLower.equals("with"))
          segment = WITH_SEGMENT;
        else if (tokenLower.equals("class"))
          segment = CLASS_SEGMENT;

        //semicolon signifies the end of the entity
        else if (token.equals(";"))
          break;

        //process all other tokens in the context of the current segment
        else
        {
          switch (segment)
          {
          case CLASS_SEGMENT:
            if (token.equals(",")) continue;

            this.classes.add(token);
            break;

          case WITH_SEGMENT:

            //capture tokens in the found_in property
            if (tokenLower.equals("found_in"))
              property = "found_in";
            else if (token.equals(","))
              property = "";
            else if (property.equals("found_in"))
              this.found_in.add(token);

            break;
          }
        }
      }
    }
  }

  public int getType()
  {
    return this.type;
  }

  public String getText()
  {
    return text;
  }

  public String getIdentifier()
  {
    return identifier;
  }

  public String[] getClasses()
  {
    return (String[]) classes.toArray(new String[0]);
  }

  public String getParent()
  {
    return parent;
  }

  public String[] getFoundIn()
  {
    return (String[]) found_in.toArray(new String[0]);
  }

  public String[] getVerbs()
  {
    return (String[]) verbs.toArray(new String[0]);
  }

  public int getArrowLevel()
  {
    return arrowLevel;
  }

  public String getTextualDescription()
  {
    return textual_description;
  }

  public Position getStartPosition()
  {
    return startPosition;
  }

  public Position getEndPosition()
  {
    return endPosition;
  }

  public String toString()
  {
    switch (this.type)
    {
    case CLASS_TYPE:
    case CONSTANT_TYPE:
    case FUNCTION_TYPE:
    case GLOBAL_TYPE:
    case OBJECT_TYPE:
      if (identifier.length() > 0) return identifier;
      return textual_description;

    case VERB_TYPE:
      if (verbs.size() > 0) return verbs.get(0).toString();
      break;
    }

    return "";
  }

  public int getLineNumber()
  {
    return lineNumber;
  }

  private final String text;
  private final int type;
  private final int lineNumber;
  private Position startPosition;
  private Position endPosition;
  private int arrowLevel = 0;
  private String identifier = "";
  private String textual_description = "";
  private String parent = "";
  private ArrayList found_in = new ArrayList();
  private ArrayList classes = new ArrayList();
  private ArrayList verbs = new ArrayList();

  private void log(String s)
  {
//    System.out.println(s);
//    JOptionPane.showConfirmDialog(null, s);
  }

  public static String simplify(String s)
  {
    char[] array = new char[s.length()];
    boolean inSingleQuotes = false;
    boolean inDoubleQuotes = false;
    boolean inComment = false;
    int bracketLevel = 0;
    int j = 0;
    for (int i = 0; i < array.length; i++)
    {
      char c = s.charAt(i);
      switch (c)
      {
      case '\n':
        if (inComment) inComment = false;
        array[j++] = c;
        break;

      case '[':
        if (!inSingleQuotes && !inDoubleQuotes && !inComment)
        {
          bracketLevel++;
        }
        break;

      case ']':
        if (!inSingleQuotes && !inDoubleQuotes && !inComment)
        {
          bracketLevel--;
        }
        break;

      case '\"':
        if (inDoubleQuotes)
          inDoubleQuotes = false;
        else if (!inSingleQuotes && !inComment)
          inDoubleQuotes = true;
        break;

      case '\'':
        if (inSingleQuotes)
          inSingleQuotes = false;
        else if (!inDoubleQuotes && !inComment)
          inSingleQuotes = true;
        break;

      case '!':
        if (!inSingleQuotes && !inDoubleQuotes) inComment = true;
        break;

      default:
        if (bracketLevel == 0 &&
            !inSingleQuotes &&
            !inDoubleQuotes &&
            !inComment)
          array[j++] = c;
        break;
      }
    }
    return new String(array, 0, j);
  }

  public static String removeComments(String s)
  {
    char[] array = new char[s.length()];
    boolean inSingleQuotes = false;
    boolean inDoubleQuotes = false;
    boolean inComment = false;
    int j = 0;
    for (int i = 0; i < array.length; i++)
    {
      char c = s.charAt(i);
      switch (c)
      {
      case '\n':
        if (inComment) inComment = false;
        break;

      case '\"':
        if (inDoubleQuotes)
          inDoubleQuotes = false;
        else if (!inSingleQuotes && !inComment) inDoubleQuotes = true;
        break;

      case '\'':
        if (inSingleQuotes)
          inSingleQuotes = false;
        else if (!inDoubleQuotes && !inComment) inSingleQuotes = true;
        break;

      case '!':
        if (!inSingleQuotes && !inDoubleQuotes) inComment = true;
        break;
      }

      if (!inComment) array[j++] = c;
    }
    return new String(array, 0, j);
  }
}
