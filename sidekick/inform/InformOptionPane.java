package sidekick.inform;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;

/**
 * Defines an option pane for the plugin settings window.
 *
 * @author Jesse Carroll
 * @version $Revision: 1.0 $
 */
public class InformOptionPane extends AbstractOptionPane {

  private JCheckBox showClasses;
  private JCheckBox showConstants;
  private JCheckBox showFunctions;
  private JCheckBox showGlobals;
  private JCheckBox showVerbs;
  private JCheckBox analyzeInit;
  private JCheckBox displayErrors;
  private JRadioButton firstRadioButton;
  private JRadioButton lastRadioButton;

  public InformOptionPane()
  {
    super("informsidekick");
  }

  protected void _init()
  {

    /**
     * Set up radio buttons for special node positioning.
     */
    JPanel panel = new JPanel();
    //  panel.setBorder(BorderFactory.createLineBorder(Color.red));
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    firstRadioButton = new JRadioButton("top", true);
    lastRadioButton = new JRadioButton("bottom");
    ButtonGroup bg = new ButtonGroup();
    bg.add(firstRadioButton);
    bg.add(lastRadioButton);
    JLabel l1 = new JLabel(jEdit.getProperty("options.informsidekick.specialNodesOnTop"));
    panel.add(l1);
    panel.add(firstRadioButton);
    panel.add(lastRadioButton);
    if (jEdit.getBooleanProperty("informSidekick.specialNodesOnTop", false))
      firstRadioButton.setSelected(true);
    else
      lastRadioButton.setSelected(true);
    panel.setAlignmentX(Box.LEFT_ALIGNMENT);

    /**
     * Lay out the options page.
     */
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(BorderFactory.createTitledBorder("InformSideKick Settings"));
    add(panel);
    add(showClasses = new JCheckBox(
        jEdit.getProperty("options.informsidekick.showclasses"),
        jEdit.getBooleanProperty("informSidekick.showClasses", false)));
    add(showConstants = new JCheckBox(
        jEdit.getProperty("options.informsidekick.showconstants"),
        jEdit.getBooleanProperty("informSidekick.showConstants", false)));
    add(showFunctions = new JCheckBox(
        jEdit.getProperty("options.informsidekick.showfunctions"),
        jEdit.getBooleanProperty("informSidekick.showFunctions", false)));
    add(showGlobals = new JCheckBox(
        jEdit.getProperty("options.informsidekick.showglobals"),
        jEdit.getBooleanProperty("informSidekick.showGlobals", false)));
    add(showVerbs = new JCheckBox(jEdit.getProperty("options.informsidekick.showverbs"),
        jEdit.getBooleanProperty("informSidekick.showVerbs", false)));
    add(analyzeInit = new JCheckBox(
        jEdit.getProperty("options.informsidekick.analyzeinit"),
        jEdit.getBooleanProperty("informSidekick.analyzeInit", false)));
    add(displayErrors = new JCheckBox(
        jEdit.getProperty("options.informsidekick.displayerrors"),
        jEdit.getBooleanProperty("informSidekick.displayErrors", false)));
  }

  protected void _save()
  {
    jEdit.setBooleanProperty("informSidekick.showClasses", showClasses.isSelected());
    jEdit.setBooleanProperty("informSidekick.showConstants", showConstants.isSelected());
    jEdit.setBooleanProperty("informSidekick.showFunctions", showFunctions.isSelected());
    jEdit.setBooleanProperty("informSidekick.showGlobals", showGlobals.isSelected());
    jEdit.setBooleanProperty("informSidekick.showVerbs", showVerbs.isSelected());
    jEdit.setBooleanProperty("informSidekick.analyzeInit", analyzeInit.isSelected());
    jEdit.setBooleanProperty("informSidekick.displayErrors", displayErrors.isSelected());
    jEdit.setBooleanProperty("informSidekick.specialNodesOnTop", firstRadioButton.isSelected());
  }
}
