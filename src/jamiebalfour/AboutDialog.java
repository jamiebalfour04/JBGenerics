package jamiebalfour;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;


class AboutDialog extends JDialog {

  public AboutDialog(String msg, JButton button) {

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setTitle("About ZPE");
    getContentPane().setLayout(new BorderLayout(0, 0));

    this.setMaximumSize(new Dimension(300, 300));

    JLabel lblNewLabel = new JLabel("About jbTAR");
    getContentPane().add(lblNewLabel, BorderLayout.NORTH);
    lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setBackground(getContentPane().getBackground());
    scrollPane.setMaximumSize(new Dimension(300, 9999));

    JTextArea lblMainInformation = new JTextArea("");
    lblMainInformation.setAlignmentX(SwingConstants.CENTER);
    scrollPane.setViewportView(lblMainInformation);
    lblMainInformation.setEditable(false);
    lblMainInformation.setBackground(getContentPane().getBackground());



    lblMainInformation.setText(msg);

    JPanel panel = new JPanel();
    getContentPane().add(panel, BorderLayout.SOUTH);

    if(button != null){
      panel.add(button);
      button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          close(button);
        }
      });
    }



    final JButton btnDoneButton = new JButton("Done");
    btnDoneButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        close(btnDoneButton);
      }
    });
    panel.add(btnDoneButton);
  }

  private static final long serialVersionUID = -5246323084901677773L;

  private void close(Component c) {

    Window w = SwingUtilities.getWindowAncestor(c);

    if (w != null) {
      w.setVisible(false);
    }
  }
}



