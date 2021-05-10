package zupacademy.magno.propostas.biometria;

import org.springframework.util.Assert;
import zupacademy.magno.propostas.cartao.Cartao;

import javax.validation.constraints.NotBlank;
import java.util.Base64;

public class BiometriaRequest {

    @NotBlank
    private String impressaoDigital;

    private BiometriaRequest(){}

    public void setImpressaoDigital(String impressaoDigital) {
        this.impressaoDigital = impressaoDigital;
    }

    public String getBiometriaRecebida() {
        return impressaoDigital;
    }


    @Override
    public String toString() {
        return "BiometriaRequest{" +
                "biometria='" + impressaoDigital + '\'' +
                '}';
    }

    public Biometria toModel(Cartao cartao) {
        Assert.notNull(cartao, "Cartão não deveria ser nulo.");
        byte[] impressaoDecodificada = Base64.getDecoder().decode(this.impressaoDigital.getBytes());
        return new Biometria(
                impressaoDecodificada,
                cartao
        );
    }
}
