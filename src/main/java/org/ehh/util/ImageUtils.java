package org.ehh.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage scaleCenterCrop(BufferedImage src, int w, int h) {
        double scale = Math.max((double) w / src.getWidth(), (double) h / src.getHeight());
        int nw = (int) Math.round(src.getWidth() * scale);
        int nh = (int) Math.round(src.getHeight() * scale);

        BufferedImage resized = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();

        int x = (nw - w) / 2;
        int y = (nh - h) / 2;

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = out.createGraphics();
        g2.drawImage(resized, -x, -y, null);
        g2.dispose();
        return out;
    }

    public static BufferedImage rotate(BufferedImage src, int deg) {
        deg = ((deg % 360) + 360) % 360;
        if (deg == 0) return src;

        int w = src.getWidth(), h = src.getHeight();
        int nw = (deg == 90 || deg == 270) ? h : w;
        int nh = (deg == 90 || deg == 270) ? w : h;

        BufferedImage out = new BufferedImage(nw, nh, src.getType());
        Graphics2D g = out.createGraphics();
        g.translate(nw / 2.0, nh / 2.0);
        g.rotate(Math.toRadians(deg));
        g.translate(-w / 2.0, -h / 2.0);
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }
}