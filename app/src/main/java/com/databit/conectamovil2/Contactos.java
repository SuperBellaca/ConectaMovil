package com.databit.conectamovil2;

public class Contactos {
    private String id;
    private String usuario;
    private String correo;

    public Contactos() {
    }

    public Contactos(String id, String usuario, String correo) {
        this.id = id;
        this.usuario = usuario;
        this.correo = correo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}