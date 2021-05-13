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
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartaoFeignHandler;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartaoFeignResponse;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartoesClient;
import zupacademy.magno.propostas.utils.Obfuscator;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

@Component
@EnableAsync
public class PropostaElegivelCriadaListener {

    private final Logger logger = LoggerFactory.getLogger(PropostaElegivelCriadaListener.class);
    @Autowired CartoesClient cartoesClient;
    @Autowired ExecutorTransacao transacao;
    @Autowired Obfuscator obfuscator;

    public PropostaElegivelCriadaListener(CartoesClient cartoesClient, ExecutorTransacao transacao, Obfuscator obfuscator) {
        this.cartoesClient = cartoesClient;
        this.transacao = transacao;
        this.obfuscator = obfuscator;
    }

    @Async
    @EventListener
    public void consultaCartao(PropostaElegivelCriadaEvent event){
        Proposta proposta = event.getPropostaElegivel();
        Assert.isTrue(!proposta.existeCartaoAssociado(), "Não deveria existir um cartão associado a essa proposta.");
        try{
            CartaoFeignResponse cartaoResponse = cartoesClient.consultaCartao(proposta.getId());
            proposta.setCartao(cartaoResponse.toModel(proposta));
            transacao.atualizaEComita(proposta);
            logger.info("Novo cartão de numero={} associado a proposta de id={}", obfuscator.hide(cartaoResponse.getId()), proposta.getId());
        } catch (FeignException.UnprocessableEntity e){
            logger.error("Cartão ainda não está disponível. Erro={}", e.toString());
        } catch (RetryableException e){
            logger.error("Erro de conexão com o serviço de cartões: {}", e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Conexão mal sucedida com o serviço de cartões.");
        } catch (Exception e){
            logger.error("Erro ao tentar conectar com o serviço de cartões={}", e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro desconhecido no servidor.");
        }
    }
}
