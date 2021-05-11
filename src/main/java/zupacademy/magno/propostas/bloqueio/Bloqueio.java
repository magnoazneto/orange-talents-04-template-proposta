package zupacademy.magno.propostas.bloqueio;

import org.springframework.util.Assert;
import zupacademy.magno.propostas.cartao.Cartao;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Bloqueio {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime instanteBloqueio = LocalDateTime.now();
    @NotBlank private String ipSolicitante;
    @NotBlank private String agenteDoUsuario;
    @NotNull @OneToOne @JoinColumn(name = "cartao_id")
    private Cartao cartao;

    /**
     * Para uso do Hibernate
     */
    @Deprecated
    public Bloqueio() {
    }

    public Bloqueio(@NotBlank String ipSolicitante, @NotBlank String agenteDoUsuario, @NotNull @Valid Cartao cartao) {
        Assert.hasLength(ipSolicitante, "IP do solicitante não deveria ser nulo");
        Assert.hasLength(agenteDoUsuario, "User Agent não deveria ser nulo");
        Assert.notNull(cartao, "Cartão não deveria ser nulo");
        this.ipSolicitante = ipSolicitante;
        this.agenteDoUsuario = agenteDoUsuario;
        this.cartao = cartao;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getInstanteBloqueio() {
        return instanteBloqueio;
    }

    public String getIpSolicitante() {
        return ipSolicitante;
    }

    public String getAgenteDoUsuario() {
        return agenteDoUsuario;
    }

    public Cartao getCartao() {
        return cartao;
    }
}
