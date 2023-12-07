package com.databit.conectamovil2;

import java.util.List;

public class Conversacion {
    private User usuario1;
    private User usuario2;
    private List<Mensajes> mensajes;

    public Conversacion() {
        // Constructor vacío requerido para Firebase
    }

    public Conversacion(User usuario1, User usuario2, List<Mensajes> mensajes) {
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
        this.mensajes = mensajes;
    }

    public User getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(User usuario1) {
        this.usuario1 = usuario1;
    }

    public User getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(User usuario2) {
        this.usuario2 = usuario2;
    }

    public List<Mensajes> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Mensajes> mensajes) {
        this.mensajes = mensajes;
    }
}