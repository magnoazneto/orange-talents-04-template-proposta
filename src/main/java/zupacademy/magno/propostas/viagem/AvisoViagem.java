package zupacademy.magno.propostas.viagem;

import zupacademy.magno.propostas.cartao.Cartao;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aviso_viagem")
public class AvisoViagem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String destino;
    private LocalDate dataTermino;
    private LocalDateTime criadoEm = LocalDateTime.now();
    private String ipCliente;
    private String agenteDoUsuario;
    @ManyToOne(cascade = CascadeType.MERGE)
    private Cartao cartao;

    /**
     * Para uso da JPA
     */
    @Deprecated
    public AvisoViagem() {
    }

    public AvisoViagem(String destino, LocalDate dataTermino, Cartao cartao, String ipCliente, String agenteDoUsuario) {
        this.destino = destino;
        this.dataTermino = dataTermino;
        this.cartao = cartao;
        this.ipCliente = ipCliente;
        this.agenteDoUsuario = agenteDoUsuario;
    }

    public Long getId() {
        return id;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public String getIpCliente() {
        return ipCliente;
    }

    public String getAgenteDoUsuario() {
        return agenteDoUsuario;
    }
}
