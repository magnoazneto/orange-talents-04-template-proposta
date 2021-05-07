package zupacademy.magno.propostas.proposta;

import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
public class Proposta {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(unique = true)
    private String documento;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String nome;
    @NotBlank
    private String endereco;
    @NotNull @Positive
    private BigDecimal salario;
    @Enumerated(EnumType.STRING)
    private StatusRestricao statusRestricao = StatusRestricao.NAO_ANALISADO;
    private String idCartao;

    /**
     * para uso do hibernate
     */
    public Proposta() {
    }

    public Proposta(@NotBlank String documento,
                    @NotBlank @Email String email,
                    @NotBlank String nome,
                    @NotBlank String endereco,
                    @NotNull @Positive BigDecimal salario) {
        this.documento = documento;
        this.email = email;
        this.nome = nome;
        this.endereco = endereco;
        this.salario = salario;
    }

    public boolean existeCartaoAssociado(){
        return this.idCartao != null;
    }

    public void setEstadoRestricao(StatusRestricao statusRestricao) {
        this.statusRestricao = statusRestricao;
    }

    public StatusRestricao getEstadoRestricao() {
        return statusRestricao;
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

    public void setIdCartao(String idCartao) {
        Assert.hasLength(idCartao, "Número do cartão não deveria ser vazio.");
        this.idCartao = idCartao;
    }

    @Override
    public String toString() {
        return "Proposta{" +
                "id=" + id +
                ", documento='" + documento + '\'' +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", salario=" + salario +
                ", statusRestricao=" + statusRestricao +
                ", idCartao='" + idCartao + '\'' +
                '}';
    }
}
