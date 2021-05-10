package zupacademy.magno.propostas.biometria;

import org.springframework.util.Assert;
import zupacademy.magno.propostas.cartao.Cartao;

import javax.persistence.*;

@Entity
public class Biometria {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private byte[] impressaoDigital;
    @ManyToOne(cascade = CascadeType.MERGE)
    private Cartao cartao;

    public Biometria(byte[] impressaoDigital, Cartao cartao) {
        Assert.notNull(impressaoDigital, "Impressão digital não deve ser nula");
        Assert.notNull(cartao, "Cartão não deve ser nulo");

        this.impressaoDigital = impressaoDigital;
        this.cartao = cartao;
    }

    public Long getId() {
        return id;
    }

    public byte[] getImpressaoDigital() {
        return impressaoDigital;
    }

    public Cartao getCartao() {
        return cartao;
    }
}
