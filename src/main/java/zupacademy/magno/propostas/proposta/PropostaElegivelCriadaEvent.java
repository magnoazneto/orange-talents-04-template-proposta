package zupacademy.magno.propostas.proposta;

public class PropostaElegivelCriadaEvent {

    Proposta propostaElegivel;

    public PropostaElegivelCriadaEvent(Proposta propostaElegivel) {
        this.propostaElegivel = propostaElegivel;
    }

    public Proposta getPropostaElegivel() {
        return propostaElegivel;
    }
}
