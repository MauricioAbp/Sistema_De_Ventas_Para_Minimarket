package edu.upn.proyecto.gruposowad.dtos.microsoftautenticator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MfaLoginRequest {

    @NotNull(message = "El id de usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El código MFA es obligatorio")
    private String code;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}