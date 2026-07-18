package edu.upn.proyecto.gruposowad.services;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.upn.proyecto.gruposowad.dtos.auth.LoginRequest;
import edu.upn.proyecto.gruposowad.dtos.auth.LoginResponse;
import edu.upn.proyecto.gruposowad.dtos.microsoftautenticator.MfaSetupResponse;
import edu.upn.proyecto.gruposowad.exceptions.BusinessException;
import edu.upn.proyecto.gruposowad.models.Usuario;
import edu.upn.proyecto.gruposowad.repositories.UsuarioRepository;
import edu.upn.proyecto.gruposowad.security.JwtService;

@Service
public class UsuarioService {
    
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private JwtService jwtService;

    public ArrayList<Usuario> listar(){
        return(ArrayList<Usuario>) usuarioRepository.findAll();
    }
    public Usuario nuevo(Usuario a){
        if (a.getPassword_hash() != null && !isBCryptHash(a.getPassword_hash())) {
            a.setPassword_hash(PASSWORD_ENCODER.encode(a.getPassword_hash()));
        }
        return usuarioRepository.save(a);
    }
    public Optional<Usuario> buscarPorUsername(String username) {
    return usuarioRepository.findByUsername(username); 
}

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Credenciales incorrectas"));

        if (usuario.getActivo() != null && !usuario.getActivo()) {
            throw new BusinessException("El usuario se encuentra inactivo");
        }

        String storedPassword = usuario.getPassword_hash();
        
        boolean storedIsBCrypt = isBCryptHash(storedPassword);
        boolean matchesPlainTextForCurrentData = !storedIsBCrypt
                && storedPassword != null
                && storedPassword.equals(request.getPassword());
        boolean matchesBCrypt = storedIsBCrypt && PASSWORD_ENCODER.matches(request.getPassword(), storedPassword);

        if (!matchesPlainTextForCurrentData && !matchesBCrypt) {
            throw new BusinessException("Credenciales incorrectas");
        }

        if (matchesPlainTextForCurrentData) {
            usuario.setPassword_hash(PASSWORD_ENCODER.encode(request.getPassword()));
            usuarioRepository.save(usuario);
        }

        String rol = usuario.getRol() != null ? usuario.getRol().getNombre_rol().trim() : "";
        String rolFrontend = toFrontendRole(rol);

        if (Boolean.TRUE.equals(usuario.getMfa_enabled())) {
            if (usuario.getMfa_secret() == null || usuario.getMfa_secret().isBlank()) {
                throw new BusinessException("MFA no configurado correctamente");
            }
            return new LoginResponse(
                usuario.getId_usuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getUsername(),
                rolFrontend,
                null,
                true
            );
        }

        String token = jwtService.generateToken(usuario, rolFrontend);
        return new LoginResponse(
            usuario.getId_usuario(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getUsername(),
            rolFrontend,
            token);
    }

    public MfaSetupResponse setupMfa(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (usuario.getActivo() != null && !usuario.getActivo()) {
            throw new BusinessException("El usuario se encuentra inactivo");
        }

        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String secret = secretGenerator.generate();
        usuario.setMfa_secret(secret);
        usuario.setMfa_enabled(true);
        usuarioRepository.save(usuario);

        String label = String.format("Minimarket:%s", usuario.getUsername());
        String issuer = "Minimarket";
        String encodedLabel = URLEncoder.encode(label, StandardCharsets.UTF_8);
        String encodedIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
        String otpAuthUrl = String.format(
                "otpauth://totp/%s?secret=%s&issuer=%s&digits=6&period=30&algorithm=SHA1",
                encodedLabel, secret, encodedIssuer);

        return new MfaSetupResponse(secret, otpAuthUrl);
    }

    private String toFrontendRole(String rol) {
        if ("ADMIN".equalsIgnoreCase(rol) || "Administrador".equalsIgnoreCase(rol)) {
            return "ADMIN";
        }
        if ("CAJERO".equalsIgnoreCase(rol) || "Cajero".equalsIgnoreCase(rol)) {
            return "CAJERO";
        }
        throw new BusinessException("Rol no autorizado");
    }

    private boolean isBCryptHash(String value) {
        return value != null && value.matches("^\\$2[aby]\\$(0[4-9]|[12][0-9]|3[01])\\$[./A-Za-z0-9]{53}$");
    }
}
