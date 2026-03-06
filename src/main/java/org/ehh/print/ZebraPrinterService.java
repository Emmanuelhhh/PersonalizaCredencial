package org.ehh.print;

import org.ehh.layout.LayoutConfig;

import org.ehh.layout.LayoutConfig;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

public class ZebraPrinterService {

    private final LayoutConfig cfg;

    public ZebraPrinterService(LayoutConfig cfg) {
        this.cfg = cfg;
    }

    public void printBothSides(BufferedImage front, BufferedImage back) throws Exception {
        printOne(front);
        printOne(back);
    }

    private void printOne(BufferedImage image) throws Exception {
        PrinterJob job = PrinterJob.getPrinterJob();

        PrintService service = findPrinter();
        job.setPrintService(service);

        PageFormat pf = job.defaultPage();
        Paper paper = new Paper();

        // CR80 en puntos (IMPORTANTE: base "portrait")
        // ancho real: 2.125"
        // alto real: 3.375"
        double paperWidth = 2.125 * 72.0;   // 153 pts
        double paperHeight = 3.375 * 72.0;  // 243 pts

        paper.setSize(paperWidth, paperHeight);

        // Un pequeño margen de seguridad
        double margin = 0;
        paper.setImageableArea(
                margin,
                margin,
                paperWidth - (margin * 2),
                paperHeight - (margin * 2)
        );

        pf.setPaper(paper);
        pf.setOrientation(PageFormat.LANDSCAPE);

        job.setPrintable(new CardPrintable(image), pf);
        job.print();
    }

    private PrintService findPrinter() {
        String contains = (cfg.print != null && cfg.print.printerNameContains != null)
                ? cfg.print.printerNameContains
                : "Zebra";

        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService s : services) {
            if (s.getName() != null && s.getName().toLowerCase().contains(contains.toLowerCase())) {
                return s;
            }
        }

        throw new IllegalStateException("No encontré impresora que contenga: " + contains);
    }

    private static class CardPrintable implements Printable {

        private final BufferedImage image;

        public CardPrintable(BufferedImage image) {
            this.image = image;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double x = pageFormat.getImageableX();
            double y = pageFormat.getImageableY();
            double w = pageFormat.getImageableWidth();
            double h = pageFormat.getImageableHeight();

            double imgW = image.getWidth();
            double imgH = image.getHeight();

            // Escalar manteniendo proporción
            double scaleX = w / imgW;
            double scaleY = h / imgH;
            double scale = Math.min(scaleX, scaleY);

            double drawW = imgW * scale;
            double drawH = imgH * scale;

            // Centrar dentro del área imprimible
            double drawX = x + (w - drawW) / 2.0;
            double drawY = y + (h - drawH) / 2.0;

            AffineTransform at = new AffineTransform();
            at.translate(drawX, drawY);
            at.scale(scale, scale);

            g2d.drawImage(image, at, null);
            g2d.dispose();

            return PAGE_EXISTS;
        }
    }
}