package zupacademy.magno.propostas.bloqueio;

import java.time.LocalDateTime;

public class BloqueioResponse {

    private Long id;
    private LocalDateTime bloqueadoEm;
    private String agenteDoUsuario;

    public BloqueioResponse(Bloqueio bloqueio) {
        this.id = bloqueio.getId();
        this.bloqueadoEm = bloqueio.getBloqueadoEm();
        this.agenteDoUsuario = bloqueio.getAgenteDoUsuario();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getBloqueadoEm() {
        return bloqueadoEm;
    }


    public String getAgenteDoUsuario() {
        return agenteDoUsuario;
    }

    @Override
    public String toString() {
        return "BloqueioResponse{" +
                "id=" + id +
                ", bloqueadoEm=" + bloqueadoEm +
                ", agenteDoUsuario='" + agenteDoUsuario + '\'' +
                '}';
    }
}
