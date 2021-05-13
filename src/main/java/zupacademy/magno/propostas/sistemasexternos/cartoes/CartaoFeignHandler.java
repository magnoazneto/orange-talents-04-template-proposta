package zupacademy.magno.propostas.sistemasexternos.cartoes;

import feign.FeignException;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.concurrent.Callable;

@Component
public class CartaoFeignHandler {

    private final Logger logger = LoggerFactory.getLogger(CartaoFeignHandler.class);

    public void executa(Callable<Void> codigoDeRequisicao, String mensagemErro){
        try{
            codigoDeRequisicao.call();
        } catch (FeignException.UnprocessableEntity e){
            logger.error("Erro 422 ao tentar chamada para o serviço de cartões.");
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, mensagemErro);
        } catch (RetryableException e){
            logger.error("Erro de conexão com o serviço de cartões={}", e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Conexão mal sucedida com o serviço de cartões.");
        } catch (Exception e){
            logger.error("Erro ao tentar conectar com o serviço de cartões={}", e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro desconhecido no serviço externo.");
        }
    }
}
