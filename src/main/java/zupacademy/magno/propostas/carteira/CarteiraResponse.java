package zupacademy.magno.propostas.carteira;

import java.time.LocalDateTime;

public class CarteiraResponse {

    private Long id;
    private TipoCarteira tipoCarteira;
    private String email;
    private LocalDateTime criadaEm;

    public CarteiraResponse(Carteira carteira) {
        this.id = carteira.getId();
        this.tipoCarteira = carteira.getTipoCarteira();
        this.email = carteira.getEmail();
        this.criadaEm = carteira.getCriadaEm();
    }

    public Long getId() {
        return id;
    }

    public TipoCarteira getTipoCarteira() {
        return tipoCarteira;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }
}
