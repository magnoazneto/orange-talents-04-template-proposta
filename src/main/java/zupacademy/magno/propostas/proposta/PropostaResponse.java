package zupacademy.magno.propostas.proposta;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PropostaResponse {

    private Long id;
    private String documento;
    private String email;
    private String nome;
    private String endereco;
    private BigDecimal salario;
    private StatusRestricao statusRestricao;
    private String idCartao;
    public PropostaResponse(@NotNull @Valid Proposta proposta) {
        this.id = proposta.getId();
        this.documento = proposta.getDocumento();
        this.email = proposta.getEmail();
        this.nome = proposta.getNome();
        this.endereco = proposta.getEndereco();
        this.salario = proposta.getSalario();
        this.statusRestricao = proposta.getStatusRestricao();
        if(proposta.existeCartaoAssociado()){
            this.idCartao = proposta.getCartao().getId();
        }
    }

    public Long getId() {
        return id;
    }

    public String getDocumento() {
        return documento;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public StatusRestricao getStatusRestricao() {
        return statusRestricao;
    }

    public String getIdCartao() {
        return idCartao;
    }
}
