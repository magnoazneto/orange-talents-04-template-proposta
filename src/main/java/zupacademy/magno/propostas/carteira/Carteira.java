package zupacademy.magno.propostas.carteira;

import org.springframework.util.Assert;
import zupacademy.magno.propostas.cartao.Cartao;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Carteira {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @Enumerated(EnumType.STRING)
    private TipoCarteira tipoCarteira;
    @Email @NotBlank
    private String email;
    @NotNull @ManyToOne
    private Cartao cartao;
    private LocalDateTime criadaEm = LocalDateTime.now();

    /**
     * Para uso da JPA
     */
    @Deprecated
    public Carteira() {
    }

    public Carteira(@NotNull TipoCarteira tipoCarteira,
                    @Valid @NotNull Cartao cartao,
                    @Email @NotBlank String email) {
        Assert.notNull(tipoCarteira, "Tipo da Carteira não deveria ser nulo.");
        Assert.notNull(cartao, "Cartão não deveria ser nulo.");
        Assert.hasLength(email, "Email não deveria ser vazio.");
        this.tipoCarteira = tipoCarteira;
        this.cartao = cartao;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public TipoCarteira getTipoCarteira() {
        return tipoCarteira;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public String getEmail() {
        return email;
    }
}
