package com.databit.conectamovil2;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String nombre;
    private String apellido;
    private String usuario;
    private String email;
    private String contrasenia;
    private String urlFotoPerfil;
    private List<Mensajes> mensajesEnviados;
    private List<Mensajes> mensajesRecibidos;

    public User() {

    }

    public User(String id, String nombre, String apellido, String usuario, String email, String contrasenia, String urlFotoPerfil) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.email = email;
        this.contrasenia = contrasenia;
        this.urlFotoPerfil = urlFotoPerfil;
        this.mensajesEnviados = new ArrayList<>();
        this.mensajesRecibidos = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public String getUrlFotoPerfil() {
        return urlFotoPerfil;
    }

    public List<Mensajes> getMensajesEnviados() {
        return mensajesEnviados;
    }

    public List<Mensajes> getMensajesRecibidos() {
        return mensajesRecibidos;
    }
    private List<Conversacion> conversaciones;

    public void enviarMensaje(User destinatario, String contenido) {
        Mensajes mensaje = new Mensajes(this, destinatario, contenido);
        mensajesEnviados.add(mensaje);
        destinatario.recibirMensaje(mensaje);
    }
    public void recibirMensaje(Mensajes mensaje) {
        mensajesRecibidos.add(mensaje);
    }
}