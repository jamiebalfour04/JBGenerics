package jamiebalfour;

import javax.swing.*;

public class JBGenerics {
  public static JDialog generateAboutDialog(String message, JButton button) {
    return new AboutDialog(message, button);
  }
}