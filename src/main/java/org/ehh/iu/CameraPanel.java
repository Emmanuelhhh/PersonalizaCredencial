package org.ehh.iu;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class CameraPanel extends JPanel {

    private JComboBox<Webcam> cmbCams = new JComboBox<>();
    private JButton btnStart = new JButton("Iniciar camara");
    private JButton btnCapture = new JButton("Capturar");
    private JLabel lblInfo = new JLabel(" ");

    private Webcam current;
    private WebcamPanel livePanel;

    public CameraPanel() {
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Camara:"));
        top.add(cmbCams);
        top.add(btnStart);
        top.add(btnCapture);
        top.add(lblInfo);

        add(top, BorderLayout.NORTH);

        btnCapture.setEnabled(false);

        loadCams();

        btnStart.addActionListener(e -> startSelected());
    }

    private void loadCams() {
        List<Webcam> cams = Webcam.getWebcams();
        DefaultComboBoxModel<Webcam> model = new DefaultComboBoxModel<>();
        for (Webcam c : cams) model.addElement(c);
        cmbCams.setModel(model);
        lblInfo.setText("Camaras: " + cams.size());
    }

    private void startSelected() {
        stop();

        current = (Webcam) cmbCams.getSelectedItem();
        if (current == null) {
            JOptionPane.showMessageDialog(this, "No hay camara seleccionada.");
            return;
        }

        current.open();
        livePanel = new WebcamPanel(current);
        livePanel.setFPSDisplayed(true);
        livePanel.setPreferredSize(new Dimension(420, 280));

        add(livePanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        btnCapture.setEnabled(true);
    }

    public BufferedImage capture() {
        if (current == null || !current.isOpen()) {
            JOptionPane.showMessageDialog(this, "Primero inicia la camara.");
            return null;
        }
        return current.getImage();
    }

    public void onCapture(Runnable handler) {
        btnCapture.addActionListener(e -> handler.run());
    }

    public void stop() {
        btnCapture.setEnabled(false);

        if (livePanel != null) {
            remove(livePanel);
            livePanel = null;
        }

        if (current != null) {
            try { current.close(); } catch (Exception ignored) {}
            current = null;
        }

        revalidate();
        repaint();
    }
}