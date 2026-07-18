package edu.upn.proyecto.gruposowad.controllers;

import edu.upn.proyecto.gruposowad.dtos.microsoftautenticator.MfaLoginRequest;
import edu.upn.proyecto.gruposowad.dtos.auth.LoginResponse;
import edu.upn.proyecto.gruposowad.services.MfaVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
public class MfaLoginController {

    @Autowired
    private MfaVerificationService mfaVerificationService;

    @PostMapping("/verify-mfa")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<LoginResponse> verificarMfa(@Valid @RequestBody MfaLoginRequest request) {
        return ResponseEntity.ok(mfaVerificationService.verifyMfa(request));
    }
}
