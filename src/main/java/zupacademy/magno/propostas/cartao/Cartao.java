package zupacademy.magno.propostas.cartao;

import zupacademy.magno.propostas.biometria.Biometria;
import zupacademy.magno.propostas.bloqueio.Bloqueio;
import zupacademy.magno.propostas.proposta.Proposta;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "cartao")
public class Cartao {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String numero;
    @NotBlank
    private String titular;
    @NotNull
    private LocalDateTime emitidoEm;
    @NotNull
    private Integer limite;
    @NotNull @OneToOne @JoinColumn(name = "proposta")
    private Proposta proposta;
    @OneToMany(mappedBy = "cartao")
    private Set<Biometria> biometrias;
    @OneToOne(cascade = CascadeType.MERGE) @JoinColumn(name = "bloqueio")
    private Bloqueio bloqueio;

    @Deprecated
    public Cartao() {
    }

    public Cartao(String numero, String titular, LocalDateTime emitidoEm, Integer limite, Proposta proposta) {
        this.numero = numero;
        this.titular = titular;
        this.emitidoEm = emitidoEm;
        this.limite = limite;
        this.proposta = proposta;
    }

    public String getId() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }

    public Integer getLimite() {
        return limite;
    }

    public Proposta getProposta() {
        return proposta;
    }

    public String getNumero() {
        return numero;
    }

    public Set<Biometria> getBiometrias() {
        return biometrias;
    }

    public boolean bloqueado(){
        return this.bloqueio != null;
    }

    public void setBloqueio(Bloqueio bloqueio) {
        this.bloqueio = bloqueio;
    }
}
