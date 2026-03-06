package org.ehh.iu;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewDialog extends JDialog {

    public PreviewDialog(JFrame owner, BufferedImage front, BufferedImage back) {
        super(owner, "Vista previa", true);

        JPanel content = new JPanel(new GridLayout(1, 2, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        content.add(makePanel("FRONT", front));
        content.add(makePanel("BACK", back));

        setContentPane(new JScrollPane(content));
        setSize(1100, 520);
        setLocationRelativeTo(owner);
    }

    private JPanel makePanel(String title, BufferedImage img) {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);

        Image scaled = img.getScaledInstance(520, -1, Image.SCALE_SMOOTH);
        JLabel lbl = new JLabel(new ImageIcon(scaled));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }
}