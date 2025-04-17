package jamiebalfour;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomTitleBar extends JPanel {
  private Point dragOffset = new Point();

  public CustomTitleBar(JFrame frame, String titleText) {
    setLayout(new BorderLayout());
    setBackground(new Color(40, 75, 99));
    setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    setPreferredSize(new Dimension(frame.getWidth(), 40));

    // Title label
    JLabel titleLabel = new JLabel(titleText, SwingConstants.CENTER);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    add(titleLabel, BorderLayout.CENTER);
    titleLabel.setForeground(Color.WHITE);

    // Close button
    JButton closeBtn = new JButton("✖");
    closeBtn.setFocusPainted(false);
    closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    closeBtn.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
    closeBtn.setBackground(new Color(40, 75, 99));
    closeBtn.setForeground(Color.WHITE);
    closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    closeBtn.addActionListener(e -> frame.dispose()); // or System.exit(0)
    add(closeBtn, BorderLayout.EAST);

    // Drag to move functionality
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        dragOffset.setLocation(e.getPoint());
      }
    });

    addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        Point framePos = frame.getLocation();
        frame.setLocation(framePos.x + e.getX() - dragOffset.x,framePos.y + e.getY() - dragOffset.y);
      }
    });
  }

}

