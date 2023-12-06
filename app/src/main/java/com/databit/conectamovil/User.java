package com.databit.conectamovil;

// User.java

public class User {
    private String id;
    private String nombre;
    private String apellido;
    private String usuario;
    private String email;
    private String contrasenia;
    private String urlFotoPerfil;
    // Constructor vacío requerido para Firebase
    public User() {
    }

    // Constructor con parámetros
    public User(String id, String nombre, String apellido, String usuario, String email, String contrasenia, String urlFotoPerfil) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.email = email;
        this.contrasenia = contrasenia;
        this.urlFotoPerfil = urlFotoPerfil;
    }

    // Métodos getter y setter (puedes generarlos automáticamente en Android Studio)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getUrlFotoPerfil() {
        return urlFotoPerfil;
    }

    public void setUrlFotoPerfil(String urlFotoPerfil) {
        this.urlFotoPerfil = urlFotoPerfil;
    }
}

