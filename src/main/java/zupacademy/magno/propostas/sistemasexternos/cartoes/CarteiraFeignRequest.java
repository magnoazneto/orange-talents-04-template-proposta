package zupacademy.magno.propostas.sistemasexternos.cartoes;

import zupacademy.magno.propostas.carteira.Carteira;

public class CarteiraFeignRequest {
    private String email;
    private String carteira;

    public CarteiraFeignRequest(Carteira carteira) {
        this.email = carteira.getEmail();
        this.carteira = carteira.getTipoCarteira().toString();
    }

    public String getEmail() {
        return email;
    }

    public String getCarteira() {
        return carteira;
    }
}
