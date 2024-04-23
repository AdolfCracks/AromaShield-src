package dev.pener.obfuscator.gui.util;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class RoundedBorder extends LineBorder {

    private int radius;
    public RoundedBorder(Color c, int thickness, int radius) {
        super(c, thickness, true);
        this.radius = radius;
    }
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        // adapted code of LineBorder class
        if ((this.thickness > 0) && (g instanceof Graphics2D)) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            Color oldColor = g2d.getColor();
            g2d.setColor(this.lineColor);

            Shape outer;
            Shape inner;

            int offs = this.thickness;
            int size = offs + offs;
            outer = new RoundRectangle2D.Float(x, y, width, height, 0, 0);
            inner = new RoundRectangle2D.Float(x + offs, y + offs, width - size, height - size, radius, radius);
            Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
            path.append(outer, false);
            path.append(inner, false);
            g2d.fill(path);
            g2d.setColor(oldColor);
        }
    }
}