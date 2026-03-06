package org.ehh.model;

import java.awt.image.BufferedImage;

public class CredencialData {
    private CredencialType type;
    private String nombre;
    private String curp;

    // Opcionales por tipo
    private String discapacidad;
    private String contactoEmergencia;

    private BufferedImage foto;

    public CredencialType getType() { return type; }
    public void setType(CredencialType type) { this.type = type; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCurp() { return curp; }
    public void setCurp(String curp) { this.curp = curp; }

    public String getDiscapacidad() { return discapacidad; }
    public void setDiscapacidad(String discapacidad) { this.discapacidad = discapacidad; }

    public String getContactoEmergencia() { return contactoEmergencia; }
    public void setContactoEmergencia(String contactoEmergencia) { this.contactoEmergencia = contactoEmergencia; }

    public BufferedImage getFoto() { return foto; }
    public void setFoto(BufferedImage foto) { this.foto = foto; }
}