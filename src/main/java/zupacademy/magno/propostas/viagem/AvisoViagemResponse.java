package zupacademy.magno.propostas.viagem;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AvisoViagemResponse {

    private String destino;
    private LocalDateTime criadoEm;
    private LocalDate dataTermino;

    public AvisoViagemResponse(AvisoViagem avisoViagem) {
        this.destino = avisoViagem.getDestino();
        this.criadoEm = avisoViagem.getCriadoEm();
        this.dataTermino = avisoViagem.getDataTermino();
    }

    public String getDestino() {
        return destino;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }
}
