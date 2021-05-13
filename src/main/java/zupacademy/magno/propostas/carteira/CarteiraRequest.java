package zupacademy.magno.propostas.carteira;

import zupacademy.magno.propostas.cartao.Cartao;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CarteiraRequest {

    @Email @NotBlank
    private String email;
    @NotNull private TipoCarteira carteira;

    public CarteiraRequest(@Email @NotBlank String email, @NotNull TipoCarteira carteira) {
        this.email = email;
        this.carteira = carteira;
    }

    public String getEmail() {
        return email;
    }

    public TipoCarteira getCarteira() {
        return carteira;
    }

    public Carteira toModel(Cartao cartao) {
        return new Carteira(this.carteira, cartao, this.email);
    }
}
