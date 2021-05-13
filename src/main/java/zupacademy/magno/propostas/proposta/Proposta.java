package zupacademy.magno.propostas.proposta;

import org.springframework.util.Assert;
import zupacademy.magno.propostas.cartao.Cartao;
import zupacademy.magno.propostas.config.criptografia.EncryptToDatabase;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "proposta")
public class Proposta {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(unique = true) @Convert(converter = EncryptToDatabase.class)
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
    @OneToOne(cascade = CascadeType.MERGE) @JoinColumn(name = "cartao_id")
    private Cartao cartao;
    private LocalDateTime criadaEm = LocalDateTime.now();

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
        Assert.hasLength(documento, "Documento não pode ser em branco.");
        Assert.hasLength(email, "email não pode ser em branco.");
        Assert.hasLength(nome, "nome não pode ser em branco.");
        Assert.hasLength(endereco, "endereco não pode ser em branco.");
        Assert.notNull(salario, "Salário não deveria ser nulo.");
        Assert.isTrue(salario.compareTo(new BigDecimal(0)) > 0, "Salário tem que ser positivo");
        this.documento = documento;
        this.email = email;
        this.nome = nome;
        this.endereco = endereco;
        this.salario = salario;
    }

    public boolean existeCartaoAssociado(){
        return this.cartao != null;
    }

    public void setStatusRestricao(StatusRestricao statusRestricao) {
        this.statusRestricao = statusRestricao;
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

    public Cartao getCartao() {
        return cartao;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public void setCartao(Cartao cartao) {
        Assert.notNull(cartao, "Cartão não deveria ser nulo.");
        this.cartao = cartao;
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
                ", cartao='" + cartao + '\'' +
                '}';
    }
}
