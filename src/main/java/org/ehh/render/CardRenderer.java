package org.ehh.render;

import org.ehh.layout.LayoutConfig;
import org.ehh.model.CredencialData;
import org.ehh.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CardRenderer {

    private final LayoutConfig cfg;

    public CardRenderer(LayoutConfig cfg) {
        this.cfg = cfg;
    }

    public BufferedImage renderFront(CredencialData data) {
        LayoutConfig.TypeConfig tc = cfg.types.get(data.getType());
        BufferedImage template = readPng(tc.frontPath);
        return drawTemplateOnly(template);
    }

    public BufferedImage renderBack(CredencialData data) {
        LayoutConfig.TypeConfig tc = cfg.types.get(data.getType());
        BufferedImage template = readPng(tc.backPath);

        BufferedImage img = new BufferedImage(cfg.card.w, cfg.card.h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);

        // base
        g.drawImage(template, 0, 0, null);

        // foto
        if (data.getFoto() != null) {
            LayoutConfig.Rect pr = tc.back.photo;
            BufferedImage cropped = ImageUtils.scaleCenterCrop(data.getFoto(), pr.w, pr.h);
            g.drawImage(cropped, pr.x, pr.y, null);
        }

        // campos (pintamos SOLO el valor donde el template ya trae el label "NOMBRE:", etc.)
        drawFieldIfPresent(g, tc, "nombre", safe(data.getNombre()));
        drawFieldIfPresent(g, tc, "curp", safe(data.getCurp()));
        drawFieldIfPresent(g, tc, "discapacidad", safe(data.getDiscapacidad()));
        drawFieldIfPresent(g, tc, "contacto", safe(data.getContactoEmergencia()));

        g.dispose();

        // rotación opcional del reverso por si el dúplex lo requiere
        int rot = cfg.print != null ? cfg.print.backRotateDeg : 0;
        return rot != 0 ? ImageUtils.rotate(img, rot) : img;
    }

    private void drawFieldIfPresent(Graphics2D g, LayoutConfig.TypeConfig tc, String key, String value) {
        if (value == null || value.isBlank()) return;
        if (tc.back == null || tc.back.fields == null) return;

        LayoutConfig.Field f = tc.back.fields.get(key);
        if (f == null) return;

        drawFittedText(g, value, f.x, f.y, f.maxW, f.fontSize, f.bold);
    }

    private BufferedImage drawTemplateOnly(BufferedImage template) {
        BufferedImage img = new BufferedImage(cfg.card.w, cfg.card.h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupQuality(g);
        g.drawImage(template, 0, 0, null);
        g.dispose();
        return img;
    }

    private void setupQuality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setColor(Color.BLACK);
    }

    private void drawFittedText(Graphics2D g, String text, int x, int y, int maxWidth, int fontSize, boolean bold) {
        int size = fontSize;
        Font font = new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

        while (fm.stringWidth(text) > maxWidth && size > 12) {
            size--;
            font = new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size);
            g.setFont(font);
            fm = g.getFontMetrics();
        }
        g.drawString(text, x, y);
    }

    private BufferedImage readPng(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            if (img == null) throw new IllegalStateException("PNG inválido: " + path);

            // Normalizar al tamaño del layout (por si algún día cambia)
            if (img.getWidth() == cfg.card.w && img.getHeight() == cfg.card.h) return img;

            BufferedImage out = new BufferedImage(cfg.card.w, cfg.card.h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = out.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(img, 0, 0, cfg.card.w, cfg.card.h, null);
            g.dispose();
            return out;
        } catch (Exception e) {
            throw new RuntimeException("No pude leer PNG: " + path + " -> " + e.getMessage(), e);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}