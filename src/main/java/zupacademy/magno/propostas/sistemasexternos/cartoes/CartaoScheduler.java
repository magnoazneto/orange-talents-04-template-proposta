package zupacademy.magno.propostas.sistemasexternos.cartoes;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import zupacademy.magno.propostas.proposta.Proposta;
import zupacademy.magno.propostas.proposta.PropostaRepository;
import zupacademy.magno.propostas.proposta.StatusRestricao;
import zupacademy.magno.propostas.utils.Obfuscator;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import java.util.Set;

@Component
public class CartaoScheduler {

    private final Logger logger = LoggerFactory.getLogger(CartaoScheduler.class);

    @Autowired
    CartoesClient cartoesClient;

    @Autowired
    PropostaRepository propostaRepository;

    @Autowired
    ExecutorTransacao transacao;

    @Autowired
    Obfuscator obfuscator;

    public CartaoScheduler(CartoesClient cartoesClient, PropostaRepository propostaRepository) {
        this.cartoesClient = cartoesClient;
        this.propostaRepository = propostaRepository;
    }

    @Scheduled(fixedDelayString = "${periodicidade.tentativa-numero-cartao}")
    public void associaNumeroCartao(){
        try{
            Set<Proposta> propostas = transacao.executa(() -> {
                return propostaRepository.findPropostaByStatusRestricaoAndIdCartao(StatusRestricao.ELEGIVEL, null);
            });

            propostas.forEach(proposta -> {
                Assert.isTrue(!proposta.existeCartaoAssociado(), "Não deveria existir um cartão associado a essa proposta.");
                CartaoResponse cartaoResponse = cartoesClient.consultaCartao(proposta.getId());
                proposta.setIdCartao(cartaoResponse.getId());
                transacao.atualizaEComita(proposta);
                logger.info("Novo cartão de numero={} associado a proposta de id={}", obfuscator.hide(cartaoResponse.getId()), proposta.getId());
            });
        } catch (FeignException e){
            logger.error("Cartão ainda não está disponível. Erro={}", e.toString());
        }
    }

}
