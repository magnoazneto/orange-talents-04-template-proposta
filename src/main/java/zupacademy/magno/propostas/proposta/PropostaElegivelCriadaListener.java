package zupacademy.magno.propostas.proposta;

import feign.FeignException;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartaoResponse;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartoesClient;
import zupacademy.magno.propostas.utils.Obfuscator;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

@Component
@EnableAsync
public class PropostaElegivelCriadaListener {

    private final Logger logger = LoggerFactory.getLogger(PropostaElegivelCriadaListener.class);

    @Autowired
    CartoesClient cartoesClient;

    @Autowired
    ExecutorTransacao transacao;

    @Autowired
    Obfuscator obfuscator;

    @Async
    @EventListener
    public void consultaCartao(PropostaElegivelCriadaEvent event) throws InterruptedException {
        Thread.sleep(3000);
        logger.info("Evento escutado: nova proposta criada.");
        Proposta proposta = event.getPropostaElegivel();
        try{
            Assert.isTrue(!proposta.existeCartaoAssociado(), "Não deveria existir um cartão associado a essa proposta.");
            CartaoResponse cartaoResponse = cartoesClient.consultaCartao(proposta.getId());
            proposta.setIdCartao(cartaoResponse.getId());
            transacao.atualizaEComita(proposta);
            logger.info("Novo cartão de numero={} associado a proposta de id={}", obfuscator.hide(cartaoResponse.getId()), proposta.getId());
        } catch (FeignException.UnprocessableEntity e){
            logger.error("Cartão ainda não está disponível. Erro={}", e.toString());
        } catch (RetryableException e){
            logger.error("Erro de conexão com o serviço de cartões: {}", e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Conexão mal sucedida com o serviço de cartões.");
        } catch (Exception e){
            logger.error("Erro ao tentar conectar com o serviço de cartões={}", e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro desconhecido no servidor.");
        }
    }
}
