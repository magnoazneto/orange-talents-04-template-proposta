package zupacademy.magno.propostas.sistemasexternos.cartoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zupacademy.magno.propostas.proposta.Proposta;
import zupacademy.magno.propostas.proposta.PropostaElegivelCriadaEvent;
import zupacademy.magno.propostas.proposta.PropostaRepository;
import zupacademy.magno.propostas.proposta.StatusRestricao;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import java.util.Set;

@Component
public class CartaoScheduler {

    @Autowired ApplicationEventPublisher publisher;
    @Autowired ExecutorTransacao transacao;
    @Autowired PropostaRepository propostaRepository;
    private final Logger logger = LoggerFactory.getLogger(CartaoScheduler.class);

    @Deprecated public CartaoScheduler(){}

    public CartaoScheduler(PropostaRepository propostaRepository) {
        this.propostaRepository = propostaRepository;
    }

    @Scheduled(fixedRateString = "${periodicidade.tentativa-numero-cartao}")
    public void verificaPropostasElegiveis() {
        logger.info("scheduling: verificando propostas elegiveis");
        Set<Proposta> propostas = transacao.executa(() -> propostaRepository
                .findPropostaByStatusRestricaoAndIdCartao(StatusRestricao.ELEGIVEL, null));
        logger.info("propostas encontradas={}", propostas.size());
        propostas.forEach(proposta -> publisher.publishEvent(new PropostaElegivelCriadaEvent(proposta)));
    }

}
