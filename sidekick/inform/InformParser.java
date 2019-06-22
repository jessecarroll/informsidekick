package sidekick.inform;

import errorlist.DefaultErrorSource;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import sidekick.SideKickCompletion;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;

import javax.swing.*;
import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * InformParser: parses inform source and builds a sidekick structure tree
 *
 * @author Jesse Carroll
 * @version $Revision: 1.0 $
 */
public class InformParser extends SideKickParser {

  public static final ImageIcon NORMAL_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/gold.gif"));
  public static final ImageIcon MULTIPLEPARENTS_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/gold_empty.gif"));
  public static final ImageIcon ANONYMOUS_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/gray.gif"));
  public static final ImageIcon BROKEN_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/red.gif"));

  public static final ImageIcon CLASS_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/green.gif"));
  public static final ImageIcon CONSTANT_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/green.gif"));
  public static final ImageIcon FUNCTION_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/green.gif"));
  public static final ImageIcon GLOBAL_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/green.gif"));
  public static final ImageIcon VERB_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/green.gif"));
  public static final ImageIcon SPECIAL_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/green.gif"));
  public static final ImageIcon PLAYER_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/blue.gif"));
  public static final ImageIcon THEDARK_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/dark.gif"));
  public static final ImageIcon COMPASS_ICON = new ImageIcon(
      InformSideKickPlugin.class.getResource("/icons/compass.gif"));

  public static final char LINE_COMMENT = '!';

  /**
   * In production use, the parser sets these values by querying jEdit's property registry
   * before parsing the code.  Consequently, there needs to be another way of setting
   * these values for testing purposes, which is the only use <code>setShow()</code> and
   * <code>setViewType()</code> have.
   */
  private boolean showClasses = false;
  private boolean showConstants = false;
  private boolean showFunctions = false;
  private boolean showGlobals = false;
  private boolean showVerbs = false;
  private boolean analyzeInit = false;
  private boolean displayErrors = false;
  private boolean specialNodesOnTop = false;
  public static final int CONSTANTS = 1;
  public static final int FUNCTIONS = 2;
  public static final int GLOBALS = 4;
  public static final int VERBS = 8;
  public static final int CLASSES = 16;
  public static final int ANALYZE_INIT = 32;
  public static final int SPECIAL_NODES_ON_TOP = 64;
  public static final int DISPLAY_ERRORS = 128;

  public void setShow(int bits)
  {
    showClasses = ((bits & CLASSES) == CLASSES);
    showConstants = ((bits & CONSTANTS) == CONSTANTS);
    showFunctions = ((bits & FUNCTIONS) == FUNCTIONS);
    showGlobals = ((bits & GLOBALS) == GLOBALS);
    showVerbs = ((bits & VERBS) == VERBS);
    analyzeInit = ((bits & ANALYZE_INIT) == ANALYZE_INIT);
    specialNodesOnTop = ((bits & SPECIAL_NODES_ON_TOP) == SPECIAL_NODES_ON_TOP);
    displayErrors = ((bits & DISPLAY_ERRORS) == DISPLAY_ERRORS);
  }

  public static final int HIERARCHICAL_VIEW = 0;
  public static final int ALPHABETICAL_VIEW = 1;
  private int viewType = HIERARCHICAL_VIEW;

  public void setViewType(int viewType)
  {
    this.viewType = viewType;
  }

  class ParseException extends RuntimeException {
    public ParseException()
    {
      super();
    }

    public ParseException(String s)
    {
      super(s);
    }
  }

  /**
   * These are filled in before parsing, allowing errors to be reported to the ErrorList
   * plugin at any time during the parse with the <code>reportError()</code> method.
   */
  private DefaultErrorSource errorSource = null;
  private Buffer buffer = null;

  private void reportError(String message, int lineNumber)
  {
    if (!displayErrors)
      return;

    errorSource.addError(new DefaultErrorSource.DefaultError(errorSource,
        DefaultErrorSource.ERROR,
        (buffer != null) ? buffer.getPath() : "/test/file/path",
        lineNumber,
        0,
        0,
        message));
  }

  /**
   * Constructs a new InformParser object
   *
   * @param name See sidekick.SidekickParser.
   */
  public InformParser(String name)
  {
    super(name);
  }

  /**
   * Parses the given text and returns a tree model.
   *
   * @param buffer      The buffer to parse.
   * @param errorSource An error source to add errors to.
   * @return A new instance of the <code>SideKickParsedData</code> class.
   */
  public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource)
  {
    this.buffer = buffer;
    this.errorSource = errorSource;
    return parseWorker(buffer, null);
  }

  /**
   * Does all the real work in parsing the buffer into a tree structure.  To facilitate
   * unit testing, this function must be called in one of two ways.  To use in production,
   * <code>buffer</code> must be an instance of JEdit's <code>Buffer</code> class and
   * <code>mockBuffer</code> must be set to null.  To use in unit tests with a mock
   * object, <code>mockBuffer</code> must be an instance of the <code>MockBuffer</code>
   * class, which emulates all important functionality of JEdit's <code>Buffer</code>
   * class, and <code>buffer</code> must be set to null.
   *
   * @param buffer     The buffer to parse.
   * @param mockBuffer An instance of the mock implementation of JEdit's
   *                   <code>Buffer</code> class.
   * @return A new instance of the <code>SideKickParsedData</code> class.
   */
  public SideKickParsedData parseWorker(Buffer buffer, MockBuffer mockBuffer)
  {
    InformParsedData data = (mockBuffer != null) ?
        new InformParsedData(mockBuffer.getName()) :
        new InformParsedData(buffer.getName());

    Position startOfFile = (mockBuffer != null) ?
        mockBuffer.createPosition(0) :
        buffer.createPosition(0);

    //if running in a production environment, get settings from the jEdit registry
    if (buffer != null)
    {
      showClasses = jEdit.getBooleanProperty("informSidekick.showClasses", false);
      showConstants = jEdit.getBooleanProperty("informSidekick.showConstants", false);
      showFunctions = jEdit.getBooleanProperty("informSidekick.showFunctions", false);
      showGlobals = jEdit.getBooleanProperty("informSidekick.showGlobals", false);
      showVerbs = jEdit.getBooleanProperty("informSidekick.showVerbs", false);
      analyzeInit = jEdit.getBooleanProperty("informSidekick.analyzeInit", false);
      specialNodesOnTop = jEdit.getBooleanProperty("informSidekick.specialNodesOnTop",
          false);
      displayErrors = jEdit.getBooleanProperty("informSidekick.displayErrors", false);
      viewType = jEdit.getIntegerProperty("informSidekick.viewType", HIERARCHICAL_VIEW);
    }

    //analyzeInit needs to be turned off in the alphabetical view to ensure a flat list of objects
    if (viewType == ALPHABETICAL_VIEW) analyzeInit = false;

    //root nodes for each of the special categories; a special node is added to the tree
    // after parsing is done if it has any children and its associated "show x" option is
    // turned on
    SortableTreeNode classesNode = new SortableTreeNode(
        new InformAsset("Classes", startOfFile, CLASS_ICON));
    SortableTreeNode constantsNode = new SortableTreeNode(
        new InformAsset("Constants", startOfFile, CONSTANT_ICON));
    SortableTreeNode functionsNode = new SortableTreeNode(
        new InformAsset("Functions", startOfFile, FUNCTION_ICON));
    SortableTreeNode globalsNode = new SortableTreeNode(
        new InformAsset("Globals", startOfFile, GLOBAL_ICON));
    SortableTreeNode verbsNode = new SortableTreeNode(
        new InformAsset("Verbs", startOfFile, VERB_ICON));

    //maps object identifiers to the tree nodes created to represent them
    Hashtable identifierNodeHashtable = new Hashtable();
    Hashtable verbsNodeHashtable = new Hashtable();

    //TheDark, Player, and Compass objects are implicitly available to the Inform coder,
    // so these are created automatically, but only placed into the tree if they
    // receive any children in the course of parsing
    SortableTreeNode thedarkNode = new SortableTreeNode(
        new InformAsset("TheDark", startOfFile, THEDARK_ICON));
    SortableTreeNode playerNode = new SortableTreeNode(
        new InformAsset("Player", startOfFile, PLAYER_ICON));
    SortableTreeNode compassNode = new SortableTreeNode(
        new InformAsset("Compass", startOfFile, COMPASS_ICON));
    identifierNodeHashtable.put("thedark", thedarkNode);
    identifierNodeHashtable.put("player", playerNode);
    identifierNodeHashtable.put("compass", compassNode);

    //used to keep a list of valid classes that an object may inherit from
    Hashtable classHashtable = new Hashtable();
    classHashtable.put("object", "");

    //create a node that will be displayed under the special Classes hierarchy as a
    // dummy parent (that is, it doesn't actually correspond to a class defintion
    // in the buffer) for newly defined compass directions if there are any
    SortableTreeNode CompassDirectionsClassNode = new SortableTreeNode(
        new InformAsset("CompassDirection", startOfFile, CLASS_ICON));
    classHashtable.put("compassdirection", CompassDirectionsClassNode);

    //for objects using the arrow method of specifying parentage, this
    // list holds the most recently parsed object at each arrow level
    ArrayList mostRecentObjectList = new ArrayList();

    //a handy place to stick a reference to the InformEntity containing
    // the Initialise function when and if it's found, so we don't have
    // to go looking through the entity list to find it again if the
    // "analyze 'Initialise' function" option is turned on
    InformEntity initialiseEntity = null;

    //extract data from the buffer into InformEntity objects, making it easy
    // to retrieve information about the various entities defined in the
    // Inform code
    InformEntity[] informEntities = extractEntities(buffer, mockBuffer);

    //process the Inform entities into a tree
    for (int i = 0; i < informEntities.length; i++)
    {
      InformAsset a = null;
      SortableTreeNode t;
      InformEntity ie = (InformEntity) informEntities[i];

      switch (ie.getType())
      {
      case InformEntity.CLASS_TYPE:

        //represent the item as a child of one of the special root nodes
        a = new InformAsset(ie.getIdentifier(),
            ie.getStartPosition(),
            ie.getEndPosition(),
            CLASS_ICON);
        t = new SortableTreeNode(a);
        if (showClasses)
          classesNode.add(t);

        //add the class name to a hashtable so the parser will know it is a valid
        // object type when parsing future entities that reference this class
        classHashtable.put(ie.getIdentifier().toLowerCase(), t);

        break;

      case InformEntity.CONSTANT_TYPE:
        if (!showConstants) break;

        //represent the item as a child of one of the special root nodes
        a = new InformAsset(ie.getIdentifier(),
            ie.getStartPosition(),
            ie.getEndPosition(),
            CONSTANT_ICON);
        t = new SortableTreeNode(a);
        constantsNode.add(t);

        break;

      case InformEntity.FUNCTION_TYPE:

        //represent the item as a child of one of the special root nodes
        a = new InformAsset(ie.getIdentifier(),
            ie.getStartPosition(),
            ie.getEndPosition(),
            FUNCTION_ICON);
        t = new SortableTreeNode(a);
        functionsNode.add(t);

        //if analysis of the "Initialise" function is turned on, keep an eye out for it and snag a handy reference
        // to it for later use
        if (analyzeInit && ie.getIdentifier().toLowerCase().equals("initialise"))
          initialiseEntity = ie;

        break;

      case InformEntity.GLOBAL_TYPE:
        if (!showGlobals) break;

        //represent the item as a child of one of the special root nodes
        a = new InformAsset(ie.getIdentifier(),
            ie.getStartPosition(),
            ie.getEndPosition(),
            GLOBAL_ICON);
        t = new SortableTreeNode(a);
        if (showGlobals)
          globalsNode.add(t);

        break;

      case InformEntity.OBJECT_TYPE:
        try
        {
          //create a node and associated InformAsset to represent this object in the tree
          a = new InformAsset(ie.toString(),
              ie.getStartPosition(),
              ie.getEndPosition(),
              NORMAL_ICON);
          a.setLongString(ie.getTextualDescription());
          t = new SortableTreeNode(a);

          //new nodes are parented by the root by default
          SortableTreeNode parentNode = (SortableTreeNode) data.root;

          /**
           * It is legal for an object definition to not provide an identifier or a
           * textual description, but if neither is provided, the parser has no
           * meaningful way of representing the object in the tree view of the source;
           * if neither has been provided, no object will be added to the tree.
           */
          if (ie.getIdentifier().length() == 0)
          {
            if (ie.getTextualDescription().length() == 0)
              throw new ParseException(
                  "No identifier or textual description found in entity:\n" + ie.getText());
            else
              a.setIcon(ANONYMOUS_ICON);
          }

          log("Parsing entity '" + ie.toString() + "'");

          //if the object inherits from any unknown classes, mark it as malformed
          String[] classes = ie.getClasses();
          log("lists " + classes.length + " ancestor classes");
          ArrayList invalidClasses = new ArrayList();
          boolean anyUnknownClasses = false;
          boolean allUnknownClasses = true;
          for (int j = 0; j < classes.length; j++)
          {
            if (classHashtable.containsKey(classes[j].toLowerCase()))
            {
              allUnknownClasses = false;

              //show this object as an instance of this class in the special "Classes" node
              // hierarchy if it is enabled
              if (showClasses && !classes[j].toLowerCase().equals("object"))
              {
                SortableTreeNode classNode = (SortableTreeNode) classHashtable.get(
                    classes[j].toLowerCase());
                SortableTreeNode n = new SortableTreeNode(a);
                n.setProperty("ignoreInTreePathSearch");
                classNode.add(n);
              }
            }
            else
            {
              anyUnknownClasses = true;
              invalidClasses.add(classes[j]);
            }
          }
          if (allUnknownClasses)
          {
            throw new ParseException(
                "Entity either does not inherit from any known classes or is not an object.");
          }
          else if (anyUnknownClasses)
          {
            //construct a list of unknown classes and use the asset's long string to
            // report them
            String invalidClassString = "";
            Object[] sa = invalidClasses.toArray();
            for (int j = 0; j < sa.length; j++)
            {
              invalidClassString += sa[j];
              if (j + 1 < sa.length)
                invalidClassString += ", ";
            }
            String errorString = "Invalid ancestor " + (sa.length == 1 ?
                "class" :
                "classes") + ": " + invalidClassString;
            a.setLongString(errorString);
            //a.setIcon(BROKEN_ICON);
            //reportError(errorString, ie.getLineNumber());
            log(errorString);
          }

          //only need to worry about parentage if in hierarchical view; also do not
          // worry about parentage if there is an unknown class, because the asset
          // has been marked as malformed and should hang off of the root for easy
          // visibility
          if (viewType == HIERARCHICAL_VIEW)
          {
            String parent = ie.getParent();

            //if this object's parent has been specified by name
            if (parent.length() > 0)
            {
              //no arrows may be provided, as either naming the parent or using the
              // arrow method is allowed, but both at the same time makes no sense
              // and is prohibited
              if (ie.getArrowLevel() > 0)
              {
                String errorText = "  malformed object -- cannot use both arrows and parent name in determining parentage";
                log(errorText);
                a.setLongString(errorText.trim());
                a.setIcon(BROKEN_ICON);
                reportError(
                    "Both arrows and a parent are present, but having both is illegal",
                    ie.getLineNumber());
              }

              //if the specified parent is not defined at this point in the file,
              // this is a violation of Inform syntax
              else if (identifierNodeHashtable.containsKey(parent.toLowerCase()) ==
                  false)
              {
                String errorText = "  malformed object -- specified parent, " +
                    parent +
                    ", is not defined";
                log(errorText);
                a.setLongString(errorText.trim());
                a.setIcon(BROKEN_ICON);
                reportError("Specified parent \"" + parent + "\" is not defined",
                    ie.getLineNumber());
              }

              /**
               * If there were no parse errors, determine which tree node represents the
               * specified parent object by fetching it from a hashtable that maps the
               * identifiers of all Inform objects that have been parsed thus far to the
               * tree node objects that were created to represent them.
               */
              else
              {
                log("  parent node specified by identifier: " + parent);
                parentNode = (SortableTreeNode) identifierNodeHashtable.get(
                    parent.toLowerCase());
              }
            }

            /**
             * If no parent was specified explicitly by name, then parentage is inferred
             * by the number of arrows preceding the identifier given for this object.  If
             * there is 1 arrow, it is taken to be the parent of the most recently defined
             * object with 0 arrows.  If 2 arrows, it is taken to be the parent of the most
             * recently defined object with 1 arrow, and so on.
             *
             * NOTE: This ONLY applies to objects defined using the arrow method.  Those
             * objects defined by explicitly naming a parent object do not count.  For
             * example, suppose you have already defined 2 objects like so:
             *
             *    Object o1 "object 1";
             *    Object o2 "object 2" o1;
             *
             * o1's definition did not explicitly name a parent, so it was defined using
             * the arrow method (it has zero arrows).  o2 was defined by explicitly naming
             * its parent to be o1, so it was not defined using the arrow method.
             *
             * Then object o3 is defined using the arrow method like so:
             *
             *    Object -> o3 "object 3";
             *
             * o3 becomes the parent of o1 because it has one more arrow than o1 does, and
             * even though o2 has no arrows in its definition, it was not defined using the
             * arrow method, so it is not able to receive children defined using the arrow
             * method.
             *
             * If none of this makes sense, read the Inform Designer's Manual.
             */
            else
            {
              /**
               * If the arrowLevel of this object is too high to make it the child of any
               * already parsed object (for example, if only "Object o1;" has been defined
               * and is then followed by "Object -> -> o2;"), there's an error in the
               * Inform code.
               */
              if (mostRecentObjectList.size() < ie.getArrowLevel())
              {
                String errorText = "  malformed object -- too many arrows";
                log(errorText);
                a.setLongString(errorText);
                a.setIcon(BROKEN_ICON);
                reportError(ie.getArrowLevel() + " " +
                    (ie.getArrowLevel() == 1 ? "arrow" : "arrows") +
                    " provided, but no previous object with " +
                    (ie.getArrowLevel() - 1) + " " +
                    (ie.getArrowLevel() - 1 ==  1 ? "arrow" : "arrows") + " exists",
                    ie.getLineNumber());
              }

              //if there are no arrows, take this to be a root-level object
              else if (ie.getArrowLevel() == 0)
              {
                log("  arrow level is 0, a root-level object");

                //keep the most-recent-parent list up to date
                if (mostRecentObjectList.size() == 0) mostRecentObjectList.add(null);
                mostRecentObjectList.set(0, t);
              }

              //otherwise, this object is the child of the most recently defined object
              // at this object's arrowLevel - 1
              else
              {
                parentNode = (SortableTreeNode) mostRecentObjectList.get(
                    ie.getArrowLevel() - 1);
                log("  arrow level is " +
                    ie.getArrowLevel() +
                    ", so its parent is the last-defined\n" +
                    "    object with arrow level " +
                    (ie.getArrowLevel() - 1) +
                    ": " +
                    ((InformAsset) parentNode.getUserObject()).getShortString());

                //keep the list of most recent nodes up to date
                while (ie.getArrowLevel() > mostRecentObjectList.size() - 1)
                  mostRecentObjectList.add(null);
                mostRecentObjectList.set(ie.getArrowLevel(), t);
              }
            }
          }

          //the node that will be the parent of the node representing this object has
          // now been determined, so add it to the tree
          parentNode.add(t);

          //update the hashtable associating all object identifiers with their tree nodes
          identifierNodeHashtable.put(ie.toString().toLowerCase(), t);
        }
        catch (ParseException e)
        {
          log(e.toString());
        }

        break;

      case InformEntity.VERB_TYPE:
        if (!showVerbs) break;

        //each verb found in a verb entity gets its own tree node
        String[] verbs = ie.getVerbs();
        for (int j = 0; j < verbs.length; j++)
        {
          //use a hashtable to ensure no dublicate verb nodes
          if (!verbsNodeHashtable.containsKey(verbs[j]))
          {
            a = new InformAsset(verbs[j],
                ie.getStartPosition(),
                ie.getEndPosition(),
                VERB_ICON);
            t = new SortableTreeNode(a);
            verbsNode.add(t);
            verbsNodeHashtable.put(verbs[j], t);
          }
        }

        break;

      case InformEntity.UNKNOWN_TYPE:

        //show malformed entities as root nodes with red icons
        a = new InformAsset(ie.getIdentifier(),
            ie.getStartPosition(),
            ie.getEndPosition(),
            BROKEN_ICON);
        t = new SortableTreeNode(a);
        data.root.add(t);

        break;

      }

//      if (a != null)
//        a.setLongString(
//            "Start: " + ie.getStartPosition().getOffset() + ", End: " + ie.getEndPosition().getOffset());
    }

    /**
     * Some objects may have multiple parents as a consequence of the 'found_in' property.
     * Because it is legal for found_in to reference objects that have not yet been
     * defined at that point in the file, processing of objects with multiple parents
     * must be done in a second pass.
     *
     * This should not happen in alphabetic view, as it allows no parenting at all.
     */

    if (viewType == HIERARCHICAL_VIEW)
    {
      for (int i = 0; i < informEntities.length; i++)
      {
        //find the tree node and asset created for this entity
        SortableTreeNode entityNode = (SortableTreeNode) identifierNodeHashtable.get(
            informEntities[i].toString().toLowerCase());

        //only process OBJECTs that specify 2 or more objects in the found_in property
        if (informEntities[i].getType() != InformEntity.OBJECT_TYPE ||
            informEntities[i].getFoundIn().length < 2)
          continue;

        //sanity check -- all objects should have received a tree node in the first pass
        if (entityNode == null)
          msgbox("Couldn't find hash entry for " + informEntities[i].toString());

        InformAsset a = (InformAsset) entityNode.getUserObject();

        //objects found_in multiple locations have a different icon
        a.setIcon(MULTIPLEPARENTS_ICON);

        //for each parent named in this object's found_in property
        String[] found_in = informEntities[i].getFoundIn();
        int validNodes = 0;
        for (int j = 0; j < found_in.length; j++)
        {
          //find the corresponding tree node
          SortableTreeNode parentNode = (SortableTreeNode) identifierNodeHashtable.get(
              found_in[j].toLowerCase());

          //if the parent object's node couldn't be found, it won't be possible to add a
          // child node to the tree for this found_in parent; this doesn't necessarily
          // mean there's an error in the source file, because the parent may have been
          // defined in a separate source file and included (but the parser does not
          // examined included files at this time), so just go on to the next parent
          if (parentNode == null)
            continue;

          //first valid node:
          if (validNodes == 0)
          {
            //simply reparent
            parentNode.add(entityNode);
          }

          //subsequent valid nodes:
          else
          {
            //clone the node
            SortableTreeNode n = entityNode.recursiveClone();
            parentNode.add(n);
          }

          //keep track of how many parents named in the found_in property have
          // referred to existant nodes in the tree; this is important because the first
          // time a valid node is found, the existing node of the found_in object is
          // simply reparented, but all subsequent valid nodes require the found_in
          // object's node structure to be cloned so it can exist in multiple locations
          // in the tree
          validNodes++;
        }
      }
    }

    /**
     * Process the "Initialise" function if the associated option is turned on and the
     * function was found in the buffer.
     */
    if (analyzeInit && initialiseEntity != null)
    {
      log("Analyzing initialise function");

      try
      {
        /**
         * Search the function's text for "move" and assignment statements.
         */
        Matcher m = Pattern.compile(
            "(\\s+move\\s+(\\w+)\\s+to\\s+(\\w+)\\s*;)|(\\s+(\\w+)\\s*=\\s*(\\w+)\\s*;)",
            Pattern.CASE_INSENSITIVE).matcher(initialiseEntity.getText());
        while (m.find())
        {
          /**
           * Juggle the tree around if processing a move assignment.
           */
          if (m.group(1) != null)
          {
            log("  processing move statement: " + m.group(0));

            //get the names of the objects involved
            String identifier1 = m.group(2).toLowerCase();
            String identifier2 = m.group(3).toLowerCase();

            //during the Initialise function, "self" is an alias for "player"
            if (identifier2.equals("self")) identifier2 = "player";

            //can't parent an object to itself
            if (identifier1.equals(identifier2))
            {
              log("  error: identical object names, so skipping");
              continue;
            }

            //use the object hashtable to quickly find the nodes with those names
            SortableTreeNode n1 = (SortableTreeNode) identifierNodeHashtable.get(
                identifier1);
            SortableTreeNode n2 = (SortableTreeNode) identifierNodeHashtable.get(
                identifier2);

            if (n1 == null)
            {
              log("  couldn't find '" + identifier1 + "' in the hash, so skipping");
              continue;
            }

            if (n2 == null)
            {
              log("  couldn't find '" + identifier2 + "' in the hash, so skipping");
              continue;
            }

            //parent the second node to the first
            n2.add(n1);

            log("  moved " + identifier1 + " to " + identifier2);
          }

          /**
           * If TheDark or the Player node is replaced with a custom object, replace the
           * automatically generated placeholder nodes with the nodes of the custom objects.
           */
          else if (m.group(4) != null)
          {
            log("processing assignment statement: " + m.group(0));

            String lhs = m.group(5).toLowerCase();
            String rhs = m.group(6).toLowerCase();
            SortableTreeNode rhsNode = (SortableTreeNode) identifierNodeHashtable.get(
                rhs);

            //if the player or thedark is being reassigned
            if (rhsNode != null && (lhs.equals("player") || lhs.equals("thedark")))
            {
              //update the hashtable to indicate that the LHS identifier now refers to
              // the RHS node
              identifierNodeHashtable.put(lhs, rhsNode);

              //point the special node reference over to the RHS node
              if (lhs.equals("player"))
              {
                playerNode = rhsNode;
                ((InformAsset) playerNode.getUserObject()).setIcon(PLAYER_ICON);
              }
              else if (lhs.equals("thedark"))
              {
                thedarkNode = rhsNode;
                ((InformAsset) thedarkNode.getUserObject()).setIcon(THEDARK_ICON);
              }
            }
          }
        }
      }
      catch (ParseException e)
      {
        log(e.toString());
      }
    }

    /**
     * TheDark. compass, and player nodes should be mixed in with the rest of the objects,
     * so add them *before* doing the sort.
     */
    if (thedarkNode != null && thedarkNode.getChildCount() > 0)
      data.root.add(thedarkNode);
    if (playerNode != null && playerNode.getChildCount() > 0)
      data.root.add(playerNode);
    if (compassNode != null && compassNode.getChildCount() > 0)
      data.root.add(compassNode);

    /**
     * Sort the tree.
     */
    SortableTreeNode stn = (SortableTreeNode) data.root;
    stn.sort();

    /**
     * Add in special nodes if the right options are turned on.  Also, they only show up
     * if they have any children, because they don't represent anything themselves.  They
     * may be added at the start or end of the tree, as the user prefers.
     */

    //a nested special node for showing which objects are instantiated from the
    // CompassDirections class
    if (CompassDirectionsClassNode.getChildCount() > 0)
      classesNode.add(CompassDirectionsClassNode);

    //adding the root-level nodes is slightly more complex...
    int specialNodesAdded = 0;
    if (showClasses && classesNode != null && classesNode.getChildCount() > 0)
    {
      classesNode.sort();
      if (specialNodesOnTop)
        ((SortableTreeNode) data.root).add(specialNodesAdded++,
            classesNode);
      else
        data.root.add(classesNode);
    }
    if (showConstants && constantsNode != null && constantsNode.getChildCount() > 0)
    {
      constantsNode.sort();

      if (specialNodesOnTop)
        ((SortableTreeNode) data.root).add(specialNodesAdded++,
            constantsNode);
      else
        data.root.add(constantsNode);
    }
    if (showFunctions && functionsNode != null && functionsNode.getChildCount() > 0)
    {
      functionsNode.sort();

      if (specialNodesOnTop)
        ((SortableTreeNode) data.root).add(specialNodesAdded++,
            functionsNode);
      else
        data.root.add(functionsNode);
    }
    if (showGlobals && globalsNode != null && globalsNode.getChildCount() > 0)
    {
      globalsNode.sort();

      if (specialNodesOnTop)
        ((SortableTreeNode) data.root).add(specialNodesAdded++,
            globalsNode);
      else
        data.root.add(globalsNode);
    }
    if (showVerbs && verbsNode != null && verbsNode.getChildCount() > 0)
    {
      verbsNode.sort();

      if (specialNodesOnTop)
        ((SortableTreeNode) data.root).add(specialNodesAdded,
            verbsNode);
      else
        data.root.add(verbsNode);
    }

    return data;
  }

  /**
   * Extracts text from the buffer and attempts to separate it into discrete entities
   * (objects, globals, classes, verbs, etc). These entities are represented by
   * InformEntity objects, which simplify the task of gleaning various pieces of
   * information about the entities.
   *
   * @param buffer     Used in production, this must be a reference to jEdit's buffer.  If
   *                   not using this method in production, this must be null.
   * @param mockBuffer Used in testing, this must be a reference to a
   *                   <code>MockBuffer</code> object.  If not using this method in
   *                   production, this must be null.
   * @return An array of InformEntity objects representing all entities found in the
   *         buffer.
   */
  public InformEntity[] extractEntities(Buffer buffer, MockBuffer mockBuffer)
  {
    final int bufferLineCount = (mockBuffer != null) ?
        mockBuffer.getLineCount() :
        buffer.getLineCount();

    //jEdit uses \n internally regardless of which newline format is used to save the file
    final String lineSeparator = "\n";
    final int lineSeparatorLength = lineSeparator.length();

    /**
     * Process the buffer line by line, accumulating text until the end of an entity
     * is reached.  Each character is examined sequentially until a semicolon is found,
     * with the contents of brackets, quotes, and comments ignored.
     * At the end of each entity, an instance of the InformEntity class is
     * used to help extract all information relevant to the creation and placement of a
     * tree node representative of the entity.
     */
    int entityTextStart = -1;
    int entityTextLength = 0;
    ArrayList informEntities = new ArrayList();
    int bracketLevel = 0;
    boolean inSingleQuotes = false;
    boolean inDoubleQuotes = false;
    boolean inComment = false;
    for (int lineNumber = 0; lineNumber < bufferLineCount; lineNumber++)
    {
      //a new line always ends a comment
      inComment = false;

      //note the line start offset
      int lineStartOffset = (mockBuffer != null) ?
          mockBuffer.getLineStartOffset(lineNumber) :
          buffer.getLineStartOffset(lineNumber);

      /**
       * Buffer's getLineEndOffset() and getLineEndOffset() might not do exactly what you
       * expect!
       *
       * Suppose this is the total contents of jEdit's buffer:
       * 0: \n
       * 1: \n
       * 2: blah blah\n
       * 3: fdsgsg\n
       * 4: \n
       * 5: fdsfds
       *
       * Put together, it forms a 26-character array
       * 0 1 2345678901 2345678 9 012345
       * \n\nblah blah\nfdsgsg\n\nfdsfds
       *
       * getLineStartOffset() and getLineEndOffset() report the following:
       * 0: 0, 1
       * 1: 1, 2
       * 2: 2, 12
       * 3: 12, 19
       * 4: 19, 20
       * 5: 20, 27
       *
       * getLineStartOffset() reports the 0-based index of the start of the line.
       *
       * getLineEndOffset() reports the 0-based starting offset of the NEXT LINE AFTER
       * even if there is no next line!
       *
       * This is why you need to subtract 1 from the offset reported by getLineEndOffset()
       * in order to use it to create a buffer position, because if you don't, then at best
       * the position is located on the next line, and at worst it doesn't exist (at the end
       * of the buffer) and you'll get an ArrayIndexOutOfBounds exception.
       */
      Position endPosition = (buffer != null) ?
          buffer.createPosition(buffer.getLineEndOffset(lineNumber) - 1) :
          mockBuffer.createPosition(mockBuffer.getLineEndOffset(lineNumber) - 1);

      //fetch the line into a string
      final String line = (mockBuffer != null) ?
          mockBuffer.getLineText(lineNumber) :
          buffer.getLineText(lineNumber);

      //skip comments and empty lines
      if (line.length() == 0 || line.charAt(0) == LINE_COMMENT)
      {
        if (entityTextStart != -1)
          entityTextLength += line.length() + lineSeparatorLength;
        continue;
      }

      //begin character by character line examination
      byte[] lineChars = line.getBytes();
      for (int i = 0; i < lineChars.length; i++)
      {
        //if we're not currently inside of an entity
        if (entityTextStart == -1)
        {
          //a LINE_COMMENT character makes the parser ignore the rest of the line
          if (lineChars[i] == LINE_COMMENT)
            inComment = true;
          if (lineChars[i] == '\r' || lineChars[i] == '\n')
            inComment = false;
          if (inComment)
            continue;

          //if the next char is whitespace, skip over it
          if (lineChars[i] == '\n' ||
              lineChars[i] == '\r' ||
              lineChars[i] == ' ' ||
              lineChars[i] == '\t' ||
              lineChars[i] == '\f')
            continue;

          //otherwise, mark the start of the next entity
          else
          {
            entityTextStart = lineStartOffset + i;
          }
        }

        //keep track of how long the current entity is
        entityTextLength++;

        switch (lineChars[i])
        {
        case '[':
          if (!inSingleQuotes && !inDoubleQuotes && !inComment)
            bracketLevel++;
          break;

        case ']':
          if (!inSingleQuotes && !inDoubleQuotes && !inComment)
            bracketLevel--;
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

        case ';':
          if (bracketLevel == 0 && !inSingleQuotes && !inDoubleQuotes && !inComment)
          {
            /**
             * The end of an entity has been reached.  Extract the complete text of the
             * entity from the buffer and use it to make an InformEntity object, which
             * helps parse the entity and provides a number of accessor methods for
             * extracting the important bits of data from the entity.
             */
            String entityText = (buffer != null) ?
                buffer.getText(entityTextStart, entityTextLength) :
                mockBuffer.getText(entityTextStart, entityTextLength);
//            if (!entityText.endsWith(";"))
//            {
//              msgbox(
//                  "Entity starting '" + entityText.substring(0,
//                      Math.min(entityText.length(), 30)) + "' ends with '" + entityText.substring(
//                          Math.max(0, entityText.length()-30)) + "'");
//            }
            Position startPosition = (buffer != null) ?
                buffer.createPosition(entityTextStart) :
                mockBuffer.createPosition(entityTextStart);
            informEntities.add(
                new InformEntity(entityText, startPosition, endPosition, lineNumber));
            entityTextStart = -1;
            entityTextLength = 0;
          }
          break;
        }
      }

      //in keeping track of the length of the current entity, newline characters must be
      // taken into account
      if (entityTextStart != -1)
        entityTextLength += lineSeparatorLength;
    }

//    String error = null;
//    int errorLine = 0;
//    if (inSingleQuotes || inDoubleQuotes)
//    {
//      error = "Mismatched quotes";
//      errorLine = lastQuoteLine;
//    }
//    else if (bracketLevel > 0)
//    {
//      error = "Mismatched brackets";
//      errorLine = lastBracketLine;
//    }
//    else if (entityTextLength > 0 && !inComment)
//    {
//      error = "Unfinished entity";
//      errorLine = lastEntityStartLine;
//    }
//    if (errorSource != null)
//    {
//    errorSource.clear();
//    if (error != null)
//      errorSource.addError(new DefaultErrorSource.DefaultError(errorSource,
//          DefaultErrorSource.ERROR,
//          (buffer != null) ? buffer.getPath() : "/test/file/path",
//          errorLine,
//          0,
//          0,
//          error));
//    }

    return (InformEntity[]) informEntities.toArray(new InformEntity[0]);
  }

  void msgbox(String s)
  {
    JOptionPane.showConfirmDialog(null, s);
  }

  public boolean supportsCompletion()
  {
    return true;
  }

  public boolean canHandleBackspace()
  {
    return true;
  }

  private static final Pattern hasCompletionPattern = Pattern.compile(
      "^.*has(\\s+\\w+)*\\s+(\\w*)$");

  public SideKickCompletion complete(EditPane editPane, int caret)
  {
    Buffer b = editPane.getBuffer();
    int line = b.getLineOfOffset(caret);
    int startOffset = b.getLineStartOffset(line);
    int lineOffset = caret - startOffset;
    String s = b.getLineText(line).substring(0, lineOffset);
    //JOptionPane.showConfirmDialog(null, "caret: " + caret + "\nline: " + line + "\nstartOffset: " + startOffset, "fyi", 0);
    //JOptionPane.showConfirmDialog(null, "string is '" + s + "'...", "fyi", 0);
    Matcher m = hasCompletionPattern.matcher(s);
    InformSideKickCompletion c = null;
    if (m.find())
    {
      //JOptionPane.showConfirmDialog(null, "...and it matches!", "fyi", 0);
      String partialWord = (m.group(2) != null) ? m.group(2) : "";
      c = new InformSideKickCompletion(editPane.getView(), partialWord);
    }
    return c;
  }

  void log(String s)
  {
//    System.out.println(s);
  }
}