package jamiebalfour;

import javax.swing.*;

public class JBGenerics {

  public static void main(String[] args) {
    System.out.println("This is a library an is not intended to be run from the command line.");
  }

  public static JDialog generateAboutDialog(String message, JButton button) {
    return new AboutDialog(message, button);
  }
}