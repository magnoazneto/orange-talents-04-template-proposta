package zupacademy.magno.propostas.cartao;

import zupacademy.magno.propostas.biometria.Biometria;
import zupacademy.magno.propostas.bloqueio.Bloqueio;
import zupacademy.magno.propostas.carteira.Carteira;
import zupacademy.magno.propostas.carteira.TipoCarteira;
import zupacademy.magno.propostas.config.criptografia.EncryptToDatabase;
import zupacademy.magno.propostas.proposta.Proposta;
import zupacademy.magno.propostas.viagem.AvisoViagem;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "cartao")
public class Cartao {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Convert(converter = EncryptToDatabase.class)
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
    @Enumerated(value = EnumType.STRING)
    private StatusCartao status = StatusCartao.ATIVO;
    @OneToMany(mappedBy = "cartao")
    private Set<AvisoViagem> avisosDeViagem;
    @OneToMany(mappedBy = "cartao", cascade = CascadeType.MERGE)
    private Set<Carteira> carteiras = new HashSet<>();

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

    public Long getId() {
        return id;
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
        return this.status.equals(StatusCartao.BLOQUEADO);
    }

    public void setBloqueio(Bloqueio bloqueio) {
        this.bloqueio = bloqueio;
        this.status = StatusCartao.BLOQUEADO;
    }

    public Bloqueio getBloqueio() {
        return bloqueio;
    }

    public StatusCartao getStatus() {
        return status;
    }

    public Set<AvisoViagem> getAvisosDeViagem() {
        return avisosDeViagem;
    }

    public Set<Carteira> getCarteiras() {
        return carteiras;
    }

    public boolean possuiCarteira(TipoCarteira carteiraRecebida){
        Set<Carteira> carteirasIguais = this.carteiras.stream()
                .filter(carteira -> carteira
                        .getTipoCarteira().equals(carteiraRecebida)).collect(Collectors.toSet());
        return !carteirasIguais.isEmpty();
    }

    public void addCarteira(Carteira novaCarteira) {
        this.carteiras.add(novaCarteira);
    }
}
