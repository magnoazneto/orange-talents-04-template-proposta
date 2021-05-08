package zupacademy.magno.propostas.sistemasexternos.analises;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(AnaliseScheduler.class);

    @Scheduled(fixedDelayString = "5000")
    public void analisaPropostasNaoAnalisadas(){
        Set<Proposta> propostas = transacao.executa(() -> propostaRepository
                .findPropostaByStatusRestricaoAndIdCartao(StatusRestricao.NAO_ANALISADO, null));

        propostas.forEach(proposta -> {
            analiseRestricaoService.analisaRestricao(proposta);
        });
    }
}
