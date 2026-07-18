package edu.upn.proyecto.gruposowad.dtos.auth;

public class LoginResponse {
    private String token;
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String username;
    private String rol;
    private boolean mfaRequired;

    public LoginResponse(Long idUsuario, String nombre, String apellido, String username, String rol, String token) {
        this(idUsuario, nombre, apellido, username, rol, token, false);
    }

    public LoginResponse(Long idUsuario, String nombre, String apellido, String username, String rol, String token, boolean mfaRequired) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.rol = rol;
        this.token = token;
        this.mfaRequired = mfaRequired;
    }

    public String getToken(){
        return token;
    }
    public boolean isMfaRequired() {
        return mfaRequired;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getUsername() {
        return username;
    }

    public String getRol() {
        return rol;
    }
}
