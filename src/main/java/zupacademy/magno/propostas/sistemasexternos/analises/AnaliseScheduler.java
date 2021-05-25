package zupacademy.magno.propostas.sistemasexternos.analises;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zupacademy.magno.propostas.proposta.Proposta;
import zupacademy.magno.propostas.proposta.PropostaRepository;
import zupacademy.magno.propostas.proposta.StatusRestricao;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import java.util.Set;

@Component
public class AnaliseScheduler {

    @Autowired ExecutorTransacao transacao;
    @Autowired PropostaRepository propostaRepository;
    @Autowired AnaliseRestricaoService analiseRestricaoService;

    @Scheduled(fixedRateString = "${periodicidade.tentativa-analises}")
    public void analisaPropostasNaoAnalisadas(){
        Set<Proposta> propostas = transacao.executa(() -> propostaRepository
                .findPropostaByStatusRestricaoAndCartaoId(StatusRestricao.NAO_ANALISADO, null));

        propostas.forEach(proposta -> analiseRestricaoService.analisaRestricao(proposta));
    }
}
