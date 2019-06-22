package sidekick.inform;

import javax.swing.text.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import sidekick.*;

/**
 * InformAsset: extends sidekick.Asset, used as a data object for tree nodes
 *
 * @author Jesse Carroll
 * @version $Revision: 1.0 $
 */
public class InformAsset extends Asset {
  private String shortString;
  private String longString;
  private Icon icon;

  public InformAsset()
  {
    super("InformAsset");
  }

  public InformAsset(String name, Position start)
  {
    super(name);
    this.start = start;
    this.end = start;
    this.shortString = name;
  }

  public InformAsset(String name, Position start, Position end)
  {
    this(name, start);
    this.end = end;
  }

  public InformAsset(String name, Position start, Icon icon)
  {
    this(name, start);
    this.icon = icon;
  }

  public InformAsset(String name, Position start, Position end, Icon icon)
  {
    this(name, start, end);
    this.icon = icon;
  }

  public Icon getIcon()
  {
    return icon;
  }

  public void setIcon(ImageIcon icon)
  {
    this.icon = icon;
  }

  public String getShortString()
  {
    return shortString;
  }

  public String getLongString()
  {
    return longString;
  }

  public void setShortString(String s)
  {
    this.shortString = s;
  }

  public void setLongString(String s)
  {
    this.longString = s;
  }

  public String toString()
  {
    return shortString;
  }
}
