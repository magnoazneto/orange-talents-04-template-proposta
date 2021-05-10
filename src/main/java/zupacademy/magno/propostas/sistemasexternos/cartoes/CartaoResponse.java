package zupacademy.magno.propostas.sistemasexternos.cartoes;

import zupacademy.magno.propostas.cartao.Cartao;
import zupacademy.magno.propostas.proposta.Proposta;

import java.time.LocalDateTime;

public class CartaoResponse {

    private String id;
    private String titular;
    private LocalDateTime emitidoEm;
    private Integer limite;
    private String idProposta;

    public CartaoResponse(String id, String titular, LocalDateTime emitidoEm, Integer limite, String idProposta) {
        this.id = id;
        this.titular = titular;
        this.emitidoEm = emitidoEm;
        this.limite = limite;
        this.idProposta = idProposta;
    }

    public Cartao toModel(Proposta proposta){
        return new Cartao(this.id, this.titular, this.emitidoEm, this.limite, proposta);
    }

    public String getId() {
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

    public String getIdProposta() {
        return idProposta;
    }

    @Override
    public String toString() {
        return "CartaoResponse{" +
                "id='" + id + '\'' +
                ", titular='" + titular + '\'' +
                ", emitidoEm=" + emitidoEm +
                ", limite=" + limite +
                ", idProposta='" + idProposta + '\'' +
                '}';
    }
}
