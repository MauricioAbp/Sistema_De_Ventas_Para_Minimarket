package edu.upn.proyecto.gruposowad.dtos.microsoftautenticator;

public class MfaSetupResponse {
    private String secret;
    private String otpAuthUrl;

    public MfaSetupResponse(String secret, String otpAuthUrl) {
        this.secret = secret;
        this.otpAuthUrl = otpAuthUrl;
    }

    public String getSecret() {
        return secret;
    }

    public String getOtpAuthUrl() {
        return otpAuthUrl;
    }
}
