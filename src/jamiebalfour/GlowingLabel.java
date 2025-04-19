package jamiebalfour;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GlowingLabel extends JComponent {
  private final String text;
  private final Font font;
  private final Color textColor = Color.WHITE;
  private final Color glowColor1 = new Color(173, 122, 255, 40); // Transparent purple
  private final Color glowColor2 = new Color(173, 122, 255, 60); // Slightly more intense

  public GlowingLabel(String text) {
    this.text = text;
    this.font = new Font("SansSerif", Font.BOLD, 18);
    setPreferredSize(new Dimension(120, 40));
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int width = getWidth();
    int height = getHeight();

    // === GLOW: Layered gradients left-to-right and right-to-left ===
    GradientPaint leftToRight = new GradientPaint(0, 0, new Color(0, 0, 0, 0), width / 2f, 0, glowColor1);
    g2.setPaint(leftToRight);
    g2.fillRoundRect(0, 0, width, height, 20, 20);

    GradientPaint rightToLeft = new GradientPaint(width, 0, new Color(0, 0, 0, 0), width / 2f, 0, glowColor2);
    g2.setPaint(rightToLeft);
    g2.fillRoundRect(0, 0, width, height, 20, 20);

    // === Draw the text ===
    g2.setFont(font);
    FontMetrics metrics = g2.getFontMetrics();
    int textX = (width - metrics.stringWidth(text)) / 2;
    int textY = (height - metrics.getHeight()) / 2 + metrics.getAscent();

    g2.setColor(textColor);
    g2.drawString(text, textX, textY);

    g2.dispose();
  }
}