package jamiebalfour;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

class CustomTitleBar extends JPanel {

  private final Point dragOffset = new Point();
  JPanel _this = this;
  boolean rounded = true;
  JFrame _frame;
  Runnable maximiseAction;
  WindowsCloseButton windowsCloseButton = null;
  private Color mainColor;

  public CustomTitleBar(JFrame frame, String titleText, Runnable maximiseAction) {
    this.maximiseAction = maximiseAction;
    setOpaque(false); // Important: allows us to paint rounded shape
    setLayout(new BorderLayout());
    mainColor = new Color(40, 75, 99);
    if (!isMac()) {
      mainColor = new Color(240, 245, 249);
      setForeground(Color.BLACK);
    }

    setPreferredSize(new Dimension(frame.getWidth() + 1, 40));

    // Title label
    JLabel titleLabel = new JLabel(titleText, SwingConstants.CENTER);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    add(titleLabel, BorderLayout.CENTER);
    titleLabel.setForeground(Color.WHITE);

    if (!isMac()) {
      titleLabel.setForeground(Color.BLACK);
    }

    if (!isMac()) {
      // === Windows-style Button Panel ===
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
      buttonPanel.setOpaque(false);


// Minimise button
      JButton minBtn = createWindowsTitleButton("\u2013", 24); // Unicode en dash
      minBtn.addActionListener(e -> frame.setState(JFrame.ICONIFIED));
      buttonPanel.add(minBtn);

// Maximise / Restore button
      JButton maxBtn = createWindowsTitleButton("\u25A1", 24); // Unicode square
      maxBtn.addActionListener(e -> {
        maximiseAction.run();

        rounded = (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != JFrame.MAXIMIZED_BOTH;
        if (windowsCloseButton != null) {
          windowsCloseButton.setRounded(rounded);
        }
        this.paintComponent(this.getGraphics());
      });
      buttonPanel.add(maxBtn);

      // Close button
      /*JButton closeBtn = createWindowsTitleButton("\u2715", 16); // Unicode "×"
      closeBtn.setForeground(Color.WHITE);
      closeBtn.setBackground(new Color(220, 80, 80));
      closeBtn.setOpaque(true);
      closeBtn.addActionListener(e -> frame.dispose());
      closeBtn.addMouseListener(new HoverColor(closeBtn, new Color(255, 100, 100), new Color(220, 80, 80)));
      buttonPanel.add(closeBtn);*/

      windowsCloseButton = new WindowsCloseButton();
      windowsCloseButton.addActionListener(e -> System.exit(0));
      windowsCloseButton.setRounded(rounded);

      // Custom rollover behaviour
      windowsCloseButton.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
          windowsCloseButton.setBackground(new Color(255, 102, 102)); // light red
          windowsCloseButton.setForeground(Color.WHITE);
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
          windowsCloseButton.setBackground(null);
          windowsCloseButton.setForeground(Color.BLACK);
        }
      });

      windowsCloseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      buttonPanel.add(windowsCloseButton);

      add(buttonPanel, BorderLayout.EAST);
    } else {
      // === macOS-style Button Panel ===
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 12));
      buttonPanel.setOpaque(false);

// Red: Close
      JButton closeBtn = createMacWindowButton(new Color(255, 95, 86), "⨉");
      closeBtn.setToolTipText("Close");
      closeBtn.addActionListener(e -> frame.dispose());
      buttonPanel.add(closeBtn);

// Yellow: Minimise
      JButton minBtn = createMacWindowButton(new Color(255, 189, 46), "─");
      minBtn.setToolTipText("Minimise");
      minBtn.addActionListener(e -> frame.setState(JFrame.ICONIFIED));
      buttonPanel.add(minBtn);

// Green: Maximise / Restore
      JButton maxBtn = createMacWindowButton(new Color(39, 201, 63), "⛶");
      maxBtn.setToolTipText("Zoom");
      maxBtn.addActionListener(e -> {
        maximiseAction.run();
        rounded = (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != JFrame.MAXIMIZED_BOTH;
        if (windowsCloseButton != null) {
          windowsCloseButton.setRounded(rounded);
        }
        this.paintComponent(this.getGraphics());
      });
      buttonPanel.add(maxBtn);

      add(buttonPanel, BorderLayout.WEST);
    }


    // Close button
    /*JButton closeBtn = new JButton("✖");
    closeBtn.setFocusPainted(false);
    closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    closeBtn.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
    closeBtn.setBackground(new Color(40, 75, 99));
    closeBtn.setForeground(Color.WHITE);
    closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    closeBtn.addActionListener(e -> frame.dispose()); // or System.exit(0)
    add(closeBtn, BorderLayout.EAST);*/

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
        frame.setLocation(framePos.x + e.getX() - dragOffset.x, framePos.y + e.getY() - dragOffset.y);
      }
    });

    enableSnapToTop(frame);

    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
          maximiseAction.run();
          rounded = (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != JFrame.MAXIMIZED_BOTH;
          if (windowsCloseButton != null) {
            windowsCloseButton.setRounded(rounded);
          }
          //_this.paintComponent(_this.getGraphics());
        }
      }
    });
  }

  static boolean isMac() {
    String OS = System.getProperty("os.name").toLowerCase();
    return (OS.contains("mac"));
  }

  private void enableSnapToTop(JFrame frame) {
    _frame = frame;
    final Point dragOffset = new Point();
    final int SNAP_THRESHOLD = 5;
    final Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    final boolean[] wasMaximised = {false};

    // Apply to the title bar or draggable area
    Component dragArea = frame.getRootPane(); // Can also be your CustomTitleBar
    dragArea.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        dragOffset.setLocation(e.getPoint());
      }
    });

    dragArea.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        if ((frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
          // Restore first if currently maximised
          frame.setExtendedState(JFrame.NORMAL);
          // Adjust drag offset to prevent sudden jump
          dragOffset.setLocation(e.getPoint());
        }

        Point screenPoint = e.getLocationOnScreen();
        int newX = screenPoint.x - dragOffset.x;
        int newY = screenPoint.y - dragOffset.y;

        frame.setLocation(newX, newY);

        // Snap to top
        if (newY <= SNAP_THRESHOLD && !wasMaximised[0]) {
          frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
          wasMaximised[0] = true;
        } else if (newY > SNAP_THRESHOLD && wasMaximised[0]) {
          frame.setExtendedState(JFrame.NORMAL);
          wasMaximised[0] = false;
        }
      }
    });
  }

  private JButton createWindowsTitleButton(String text, int size) {
    JButton button = new JButton(text);
    button.setFont(new Font("Segoe UI", Font.PLAIN, size));
    button.setPreferredSize(new Dimension(40, 30));
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setOpaque(true);
    button.setBackground(new Color(240, 240, 240));
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.addMouseListener(new HoverColor(button, new Color(220, 220, 220), new Color(240, 240, 240)));
    return button;
  }

  private JButton createMacWindowButton(Color circleColor, String hoverSymbol) {
    JButton button = new JButton();
    button.setPreferredSize(new Dimension(14, 14));
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setOpaque(false);
    button.setToolTipText(hoverSymbol);

    // Initial blank circle
    button.setIcon(createCircleIcon(circleColor, 14));

    // Hover icon with symbol inside
    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        button.setIcon(createSymbolIcon(circleColor, hoverSymbol, 14));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        button.setIcon(createCircleIcon(circleColor, 14));
      }
    });

    return button;
  }

  /*private JButton createMacButton(Color color) {
    JButton button = new JButton();
    button.setPreferredSize(new Dimension(14, 14));
    button.setBackground(color);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setOpaque(true);
    button.setFocusable(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.setFocusPainted(false);

    // Optional: make it round
    button.setUI(new BasicButtonUI() {
      @Override
      public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, c.getWidth(), c.getHeight());
      }
    });

    return button;
  }*/

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    int arc = 16;

    // Smooth edges
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int width = getWidth();
    int height = getHeight();

    boolean isMaximised = false;

    // Try to detect if the parent window is maximised
    Window window = SwingUtilities.getWindowAncestor(this);
    if (window instanceof JFrame frame) {
      isMaximised = (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
    }

    if (!isMaximised) {
      // Create rounded rectangle shape just for top corners
      Shape topRounded = new RoundRectangle2D.Double(0, 0, width, height + arc, arc, arc);
      Area clip = new Area(new Rectangle(0, 0, width, height));
      clip.intersect(new Area(topRounded));
      g2.setClip(clip);
    }

    // Fill with main color
    g2.setColor(mainColor);
    g2.fillRect(0, 0, width, height);

    // OVERDRAW right edge by 1px with same fill (in case AA leaks a subpixel there)
    g2.fillRect(width - 1, 0, 2, height);

    g2.dispose();

    // Draw children *after* background
    super.paintComponent(g);
  }

  @Override
  public Insets getInsets() {
    return new Insets(0, 0, 0, 0); // No inner padding
  }

  private Icon createCircleIcon(Color color, int size) {
    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(color);
    g.fillOval(0, 0, size, size);
    g.dispose();
    return new ImageIcon(image);
  }

  private Icon createSymbolIcon(Color circleColor, String symbol, int size) {
    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(circleColor);
    g.fillOval(0, 0, size, size);

    // Draw white symbol
    g.setColor(Color.WHITE);
    Font font = new Font("Segoe UI Symbol", Font.PLAIN, size - 6);
    g.setFont(font);
    FontMetrics fm = g.getFontMetrics();
    int x = (size - fm.stringWidth(symbol)) / 2;
    int y = (size + fm.getAscent()) / 2 - 2;
    g.drawString(symbol, x, y);

    g.dispose();
    return new ImageIcon(image);
  }

  private static class HoverColor extends MouseAdapter {
    private final JComponent comp;
    private final Color hoverColor;
    private final Color defaultColor;

    public HoverColor(JComponent comp, Color hoverColor, Color defaultColor) {
      this.comp = comp;
      this.hoverColor = hoverColor;
      this.defaultColor = defaultColor;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      comp.setBackground(hoverColor);
    }

    @Override
    public void mouseExited(MouseEvent e) {
      comp.setBackground(defaultColor);
    }
  }

  public class WindowsCloseButton extends JButton {
    private boolean rounded = true;

    public WindowsCloseButton() {
      setText("<html><center>✕</center></html>");
      setForeground(Color.WHITE);
      setFont(new Font("SansSerif", Font.BOLD, 16));
      setFocusable(false);
      setBorderPainted(false);
      setContentAreaFilled(false);
      setOpaque(false);
      setUI(new BasicButtonUI());
      setMargin(new Insets(0, 0, 0, 0));
      setBorder(BorderFactory.createEmptyBorder());

      setPreferredSize(new Dimension(40, 30));
      setBackground(null); // Windows close red
      setForeground(Color.BLACK);
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setRounded(boolean rounded) {
      this.rounded = rounded;
      repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      int width = getWidth();
      int height = getHeight();
      int arc = 10;

      Shape shape;
      if (rounded) {
        Path2D path = new Path2D.Double();
        path.moveTo(0, 0);
        path.lineTo(width - arc, 0);
        path.quadTo(width, 0, width, arc);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.closePath();
        shape = path;
      } else {
        shape = new Rectangle(0, 0, width, height);
      }

      g2.setColor(getBackground());
      g2.fill(shape);

      // Draw X
      super.paintComponent(g2);
      g2.dispose();
    }
  }
}




