package edu.upn.proyecto.gruposowad.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.upn.proyecto.gruposowad.dtos.auth.LoginResponse;
import edu.upn.proyecto.gruposowad.dtos.microsoftautenticator.MfaLoginRequest;
import edu.upn.proyecto.gruposowad.exceptions.BusinessException;
import edu.upn.proyecto.gruposowad.models.Usuario;
import edu.upn.proyecto.gruposowad.repositories.UsuarioRepository;
import edu.upn.proyecto.gruposowad.security.JwtService;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;

@Service
public class MfaVerificationService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final CodeVerifier codeVerifier;

    @Autowired
    public MfaVerificationService(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.codeVerifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());
    }

    public LoginResponse verifyMfa(MfaLoginRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (usuario.getActivo() != null && !usuario.getActivo()) {
            throw new BusinessException("El usuario se encuentra inactivo");
        }

        if (!Boolean.TRUE.equals(usuario.getMfa_enabled())) {
            throw new BusinessException("MFA no habilitado");
        }

        String secret = usuario.getMfa_secret();
        if (secret == null || secret.isBlank()) {
            throw new BusinessException("Se requiere un secreto MFA válido");
        }

        if (!codeVerifier.isValidCode(secret, request.getCode())) {
            throw new BusinessException("Código inválido");
        }

        String rol = usuario.getRol() != null ? usuario.getRol().getNombre_rol().trim() : "";
        String rolFrontend = "ADMIN".equalsIgnoreCase(rol) || "Administrador".equalsIgnoreCase(rol)
                ? "ADMIN" : "CAJERO";

        String token = jwtService.generateToken(usuario, rolFrontend);

        return new LoginResponse(
                usuario.getId_usuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getUsername(),
                rolFrontend,
                token
        );
    }
}