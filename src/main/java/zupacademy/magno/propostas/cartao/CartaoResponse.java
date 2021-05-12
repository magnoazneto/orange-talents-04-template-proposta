package zupacademy.magno.propostas.cartao;

import zupacademy.magno.propostas.bloqueio.Bloqueio;
import zupacademy.magno.propostas.bloqueio.BloqueioResponse;
import zupacademy.magno.propostas.viagem.AvisoViagemResponse;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class CartaoResponse {

    private Long id;
    private String numero;
    private String titular;
    private LocalDateTime emitidoEm;
    private Integer limite;
    private BloqueioResponse bloqueio;
    private StatusCartao status;
    private Set<AvisoViagemResponse> avisosDeViagem = new HashSet<>();

    public CartaoResponse(Cartao cartao) {
        this.id = cartao.getId();
        this.numero = cartao.getNumero();
        this.titular = cartao.getTitular();
        this.emitidoEm = cartao.getEmitidoEm();
        this.limite = cartao.getLimite();
        this.status = cartao.getStatus();
        if(cartao.bloqueado()){
            this.bloqueio = new BloqueioResponse(cartao.getBloqueio());
        }
        if(!cartao.getAvisosDeViagem().isEmpty()){
            avisosDeViagem.addAll(cartao.getAvisosDeViagem().stream().map(AvisoViagemResponse::new).collect(Collectors.toSet()));
        }
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
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

    public BloqueioResponse getBloqueio() {
        return bloqueio;
    }

    public StatusCartao getStatus() {
        return status;
    }

    public Set<AvisoViagemResponse> getAvisosDeViagem() {
        return avisosDeViagem;
    }
}
