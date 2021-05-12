package zupacademy.magno.propostas.viagem;

import java.time.LocalDate;

public class AvisoViagemFeignRequest {

    private String destino;
    private LocalDate validoAte;

    public AvisoViagemFeignRequest(AvisoViagem avisoViagem) {
        this.destino = avisoViagem.getDestino();
        this.validoAte = avisoViagem.getDataTermino();
    }

    public String getDestino() {
        return destino;
    }

    public LocalDate getValidoAte() {
        return validoAte;
    }
}
