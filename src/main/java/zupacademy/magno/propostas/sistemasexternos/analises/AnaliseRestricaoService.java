package zupacademy.magno.propostas.sistemasexternos.analises;

import feign.FeignException;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;
import zupacademy.magno.propostas.proposta.Proposta;
import zupacademy.magno.propostas.proposta.PropostaElegivelCriadaEvent;
import zupacademy.magno.propostas.proposta.StatusRestricao;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

@Service
public class AnaliseRestricaoService {

    private final Logger logger = LoggerFactory.getLogger(AnaliseRestricaoService.class);
    @Autowired AnaliseRestricaoClient analiseRestricaoClient;
    @Autowired ExecutorTransacao transacao;
    @Autowired ApplicationEventPublisher publisher;

    /**
     *  Envia a proposta recebida para análise e atualiza seu StatusRestricao
     *  de acordo com a resposta.
     * @param novaProposta a nova proposta recebida para analise
     */
    public void analisaRestricao(Proposta novaProposta){
        logger.info("Enviando analise da proposta de id={}", novaProposta.getId());
        try{
            AnaliseRestricaoResponse response = analiseRestricaoClient.analisaRestricao(new AnaliseRestricaoRequest(novaProposta));
            logger.info("Resposta positiva para a proposta={}", response.getIdProposta());
            Assert.isTrue(response.getResultadoSolicitacao().equals(RetornoRestricao.SEM_RESTRICAO), "O resultado deveria ser SEM_RESTRICAO");
            novaProposta.setStatusRestricao(StatusRestricao.ELEGIVEL);
            transacao.atualizaEComita(novaProposta);
            publisher.publishEvent(new PropostaElegivelCriadaEvent(novaProposta));
        } catch (FeignException.UnprocessableEntity e){
            logger.warn("Resposta negativa. Feign={}",  e.getMessage());
            novaProposta.setStatusRestricao(StatusRestricao.NAO_ELEGIVEL);
            transacao.atualizaEComita(novaProposta);
        } catch (RetryableException e){
            logger.error("Erro de conexão com o serviço de análises: {}", e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível estabelecer conexão com o serviço de análise.");
        } catch (Exception e){
            logger.error("Erro desconhecido={}", e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro desconhecido no servidor.");
        }

    }
}
