package edu.upn.proyecto.gruposowad.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="usuario")

public class Usuario {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id_usuario;
    @ManyToOne 
    @JoinColumn(name = "id_rol") 
    private Rol rol;
    @Column
    private String nombre;
    @Column
    private String apellido;
    @Column
    private String username;
    @JsonIgnore
    @Column
    private String password_hash;
    @Column
    private Boolean activo;
    @Column
    private Boolean mfa_enabled;
    @JsonIgnore
    @Column
    private String mfa_secret;
    @Column
    private LocalDateTime created_at;
    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }
   public Long getId_usuario() {
    return id_usuario;
   }
   public void setId_usuario(Long id_usuario) {
    this.id_usuario = id_usuario;
   }
   public Rol getRol() {
    return rol;
   }
   public void setRol(Rol rol) {
    this.rol = rol;
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
   public String getUsername() {
    return username;
   }
   public void setUsername(String username) {
    this.username = username;
   }
   public String getPassword_hash() {
    return password_hash;
   }
   public void setPassword_hash(String password_hash) {
    this.password_hash = password_hash;
   }
   public Boolean getActivo() {
    return activo;
   }
   public void setActivo(Boolean activo) {
    this.activo = activo;
   }
   public Boolean getMfa_enabled() {
    return mfa_enabled;
   }
   public void setMfa_enabled(Boolean mfa_enabled) {
    this.mfa_enabled = mfa_enabled;
   }
   public String getMfa_secret() {
    return mfa_secret;
   }
   public void setMfa_secret(String mfa_secret) {
    this.mfa_secret = mfa_secret;
   }
   public LocalDateTime getCreated_at() {
    return created_at;
   }
   public void setCreated_at(LocalDateTime created_at) {
    this.created_at = created_at;
   }
    
    
}
