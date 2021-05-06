package zupacademy.magno.propostas.clients;

import zupacademy.magno.propostas.proposta.Proposta;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AnaliseRestricaoRequest {

    @NotBlank
    private String documento;
    @NotBlank
    private String nome;
    @NotNull
    private Long idProposta;

    public AnaliseRestricaoRequest(Proposta proposta) {
        this.documento = proposta.getDocumento();
        this.nome = proposta.getNome();
        this.idProposta = proposta.getId();
    }

    public String getDocumento() {
        return documento;
    }

    public String getNome() {
        return nome;
    }

    public Long getIdProposta() {
        return idProposta;
    }
}

