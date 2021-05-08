package zupacademy.magno.propostas.biometria;

import javax.validation.constraints.NotBlank;

public class BiometriaRequest {

    @NotBlank
    private String fingerprint;

    private BiometriaRequest(){}

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getBiometriaRecebida() {
        return fingerprint;
    }


    @Override
    public String toString() {
        return "BiometriaRequest{" +
                "biometria='" + fingerprint + '\'' +
                '}';
    }
}
