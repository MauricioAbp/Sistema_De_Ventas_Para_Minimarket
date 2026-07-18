package edu.upn.proyecto.gruposowad.bootstrap;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import edu.upn.proyecto.gruposowad.models.Rol;
import edu.upn.proyecto.gruposowad.models.Usuario;
import edu.upn.proyecto.gruposowad.repositories.RolRepository;
import edu.upn.proyecto.gruposowad.repositories.UsuarioRepository;

@Component
public class DefaultUserBootstrap implements ApplicationRunner {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Value("${app.bootstrap.admin.username:admin}")
    private String defaultUsername;

    @Value("${app.bootstrap.admin.password:admin}")
    private String defaultPassword;

    @Override
    public void run(ApplicationArguments args) {
        Optional<Usuario> existingUser = usuarioRepository.findByUsername(defaultUsername);
        if (existingUser.isPresent()) {
            return;
        }

        Rol adminRole = rolRepository.findAll().stream()
                .filter(rol -> "ADMIN".equalsIgnoreCase(rol.getNombre_rol())
                        || "Administrador".equalsIgnoreCase(rol.getNombre_rol()))
                .findFirst()
                .orElseGet(() -> {
                    Rol newRole = new Rol();
                    newRole.setNombre_rol("ADMIN");
                    newRole.setDescripcion("Administrador del sistema");
                    return rolRepository.save(newRole);
                });

        Usuario admin = new Usuario();
        admin.setNombre("Administrador");
        admin.setApellido("Sistema");
        admin.setUsername(defaultUsername);
        admin.setPassword_hash(PASSWORD_ENCODER.encode(defaultPassword));
        admin.setRol(adminRole);
        admin.setActivo(true);
        admin.setMfa_enabled(false);
        admin.setMfa_secret(null);

        usuarioRepository.save(admin);
    }
}
