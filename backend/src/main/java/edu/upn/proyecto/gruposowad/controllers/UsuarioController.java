package edu.upn.proyecto.gruposowad.controllers;

import org.springframework.web.bind.annotation.RestController;

import edu.upn.proyecto.gruposowad.dtos.auth.LoginRequest;
import edu.upn.proyecto.gruposowad.dtos.auth.LoginResponse;
import edu.upn.proyecto.gruposowad.dtos.microsoftautenticator.MfaSetupResponse;
import edu.upn.proyecto.gruposowad.models.Usuario;
import edu.upn.proyecto.gruposowad.services.UsuarioService;
import jakarta.validation.Valid;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    
    @Autowired
    UsuarioService usuarioService;
    @GetMapping
    public ArrayList<Usuario> listarTodos(){
        return this.usuarioService.listar();
    }
   @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(usuarioService.login(loginRequest));
    }

   @PutMapping("/{id}/mfa/setup")
   public ResponseEntity<MfaSetupResponse> setupMfa(@PathVariable("id") Long id) {
       return ResponseEntity.ok(usuarioService.setupMfa(id));
   }
}
