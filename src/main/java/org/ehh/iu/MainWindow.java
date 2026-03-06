package org.ehh.iu;

import org.ehh.layout.LayoutConfig;
import org.ehh.layout.LayoutLoader;
import org.ehh.model.CredencialData;
import org.ehh.model.CredencialType;
import org.ehh.print.ZebraPrinterService;
import org.ehh.render.CardRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainWindow extends JFrame {

    private final LayoutConfig cfg = LayoutLoader.load("src/assets/layout.json");
    private final CardRenderer renderer = new CardRenderer(cfg);
    private final ZebraPrinterService printer = new ZebraPrinterService(cfg);

    private JComboBox<CredencialType> cmbType = new JComboBox<>(CredencialType.values());

    private JTextField txtNombre = new JTextField(22);
    private JTextField txtCurp = new JTextField(22);
    private JTextField txtDiscapacidad = new JTextField(22);
    private JTextField txtContacto = new JTextField(22);

    private JLabel lblFoto = new JLabel("Foto: (no capturada)");
    private BufferedImage foto;

    private CameraPanel cameraPanel = new CameraPanel();

    private JPanel rowDiscapacidad;
    private JPanel rowContacto;

    public MainWindow() {
        setTitle("Credenciales ZC300 - MVP");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel form = buildFormPanel();
        JPanel actions = buildActionsPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, cameraPanel);
        split.setResizeWeight(0.55);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(split, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);

        cameraPanel.onCapture(() -> {
            BufferedImage captured = cameraPanel.capture();
            if (captured != null) {
                foto = captured;
                lblFoto.setText("Foto: OK (" + foto.getWidth() + "x" + foto.getHeight() + ")");
            }
        });

        cmbType.addActionListener(e -> updateDynamicFields());

        updateDynamicFields();
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int r = 0;

        c.gridx=0; c.gridy=r; p.add(new JLabel("Tipo:"), c);
        c.gridx=1; p.add(cmbType, c); r++;

        c.gridx=0; c.gridy=r; p.add(new JLabel("Nombre:"), c);
        c.gridx=1; p.add(txtNombre, c); r++;

        c.gridx=0; c.gridy=r; p.add(new JLabel("CURP:"), c);
        c.gridx=1; p.add(txtCurp, c); r++;

        rowDiscapacidad = new JPanel(new BorderLayout(6, 0));
        rowDiscapacidad.add(new JLabel("Tipo discapacidad:"), BorderLayout.WEST);
        rowDiscapacidad.add(txtDiscapacidad, BorderLayout.CENTER);

        c.gridx=0; c.gridy=r; c.gridwidth=2; p.add(rowDiscapacidad, c); r++;

        rowContacto = new JPanel(new BorderLayout(6, 0));
        rowContacto.add(new JLabel("Contacto emergencia (tel):"), BorderLayout.WEST);
        rowContacto.add(txtContacto, BorderLayout.CENTER);

        c.gridx=0; c.gridy=r; c.gridwidth=2; p.add(rowContacto, c); r++;

        c.gridx=0; c.gridy=r; c.gridwidth=2; p.add(lblFoto, c); r++;

        // hint
        JLabel hint = new JLabel("<html><body style='width:420px'>"
                + "1) Selecciona tipo y captura datos<br/>"
                + "2) Inicia camara y captura foto<br/>"
                + "3) Vista previa<br/>"
                + "4) Imprimir (driver ZC300 en duplex)<br/>"
                + "</body></html>");
        hint.setForeground(new Color(80,80,80));

        c.gridx=0; c.gridy=r; c.gridwidth=2; p.add(hint, c);

        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        return p;
    }

    private JPanel buildActionsPanel() {

        JButton btnPreview = new JButton("Vista previa");
        JButton btnPrint = new JButton("Imprimir (2 lados)");
        JButton btnClear = new JButton("Limpiar");
        JButton btnStopCam = new JButton("Detener camara");



        btnPreview.addActionListener(e -> doPreview());
        btnPrint.addActionListener(e -> doPrint());
        btnClear.addActionListener(e -> clearForm());
        btnStopCam.addActionListener(e -> cameraPanel.stop());

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(btnStopCam);
        p.add(btnClear);
        p.add(btnPreview);
        p.add(btnPrint);

        return p;
    }

    private void updateDynamicFields() {
        CredencialType t = (CredencialType) cmbType.getSelectedItem();
        if (t == null) return;

        // Estudiante: nombre, curp
        // Adulto mayor: nombre, curp, contacto
        // Discapacidad: nombre, curp, discapacidad, contacto
        rowDiscapacidad.setVisible(t == CredencialType.DISCAPACIDAD);
        rowContacto.setVisible(t == CredencialType.ADULTO_MAYOR || t == CredencialType.DISCAPACIDAD);

        // Limpieza opcional cuando cambias tipo
        if (t != CredencialType.DISCAPACIDAD) txtDiscapacidad.setText("");
        if (t == CredencialType.ESTUDIANTE) txtContacto.setText("");

        revalidate();
        repaint();
    }

    private CredencialData buildData() {
        CredencialData d = new CredencialData();
        d.setType((CredencialType) cmbType.getSelectedItem());
        d.setNombre(txtNombre.getText());
        d.setCurp(txtCurp.getText());
        d.setDiscapacidad(txtDiscapacidad.getText());
        d.setContactoEmergencia(txtContacto.getText());
        d.setFoto(foto);
        return d;
    }

    private void doPreview() {
        try {
            CredencialData d = buildData();
            BufferedImage front = renderer.renderFront(d);
            BufferedImage back = renderer.renderBack(d);
            new PreviewDialog(this, front, back).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error preview: " + ex.getMessage());
        }
    }

    private void doPrint() {
        try {
            CredencialData d = buildData();
            BufferedImage front = renderer.renderFront(d);
            BufferedImage back = renderer.renderBack(d);

            printer.printBothSides(front, back);
            JOptionPane.showMessageDialog(this, "Enviado a impresion (front + back).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error imprimir: " + ex.getMessage()
                    + "\n\nTip: verifica driver Zebra en modo dúplex y el nombre en layout.json");
        }
    }

    private void clearForm() {
        txtNombre.setText("");
        txtCurp.setText("");
        txtDiscapacidad.setText("");
        txtContacto.setText("");
        foto = null;
        lblFoto.setText("Foto: (no capturada)");
    }
}