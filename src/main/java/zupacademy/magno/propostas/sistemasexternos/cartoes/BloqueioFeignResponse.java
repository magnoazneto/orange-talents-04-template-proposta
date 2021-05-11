package zupacademy.magno.propostas.sistemasexternos.cartoes;

import zupacademy.magno.propostas.cartao.StatusCartao;

public class BloqueioFeignResponse {

    private StatusCartao resultado;

    public BloqueioFeignResponse() {
    }

    public StatusCartao getResultado() {
        return resultado;
    }

    public void setResultado(StatusCartao resultado) {
        this.resultado = resultado;
    }

    public boolean bloqueado(){
        return this.resultado.equals(StatusCartao.BLOQUEADO);
    }
}
