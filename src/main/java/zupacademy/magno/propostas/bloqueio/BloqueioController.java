package zupacademy.magno.propostas.bloqueio;

import feign.FeignException;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import zupacademy.magno.propostas.cartao.Cartao;
import zupacademy.magno.propostas.cartao.CartaoRepository;
import zupacademy.magno.propostas.sistemasexternos.cartoes.BloqueioFeignRequest;
import zupacademy.magno.propostas.sistemasexternos.cartoes.BloqueioFeignResponse;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartoesClient;
import zupacademy.magno.propostas.utils.Obfuscator;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bloqueios")
public class BloqueioController {

    @Autowired CartaoRepository cartaoRepository;
    @Autowired ExecutorTransacao transacao;
    private final Logger logger = LoggerFactory.getLogger(BloqueioController.class);
    @Autowired Obfuscator obfuscator;
    @Autowired CartoesClient cartoesClient;

    @PostMapping("/{numeroCartao}")
    public ResponseEntity<?> novoBloqueio(@PathVariable("numeroCartao") String numeroCartao,
                                          HttpServletRequest servletRequest,
                                          @RequestHeader(value = "User-Agent") String userAgent){
        Optional<Cartao> possivelCartao = cartaoRepository.findByNumero(numeroCartao);

        return possivelCartao.map(cartaoEncontrado -> {
            if(cartaoEncontrado.bloqueado()){
                return ResponseEntity.unprocessableEntity().body("Cartão já bloqueado.");
            }
            Bloqueio novoBloqueio = new Bloqueio(servletRequest.getRemoteAddr(), userAgent, cartaoEncontrado);
            bloqueiaCartao(novoBloqueio, cartaoEncontrado);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private void bloqueiaCartao(Bloqueio novoBloqueio, Cartao cartaoEncontrado){
        try{
            BloqueioFeignResponse bloqueioFeignResponse = cartoesClient.bloquearCartao(cartaoEncontrado.getNumero(), new BloqueioFeignRequest());
            Assert.isTrue(bloqueioFeignResponse.bloqueado(), "O resultado deveria ser BLOQUEADO");
            cartaoEncontrado.setBloqueio(novoBloqueio);
            transacao.atualizaEComita(cartaoEncontrado);
            logger.info("Novo bloqueio efetuado no cartão={}", obfuscator.hide(cartaoEncontrado.getNumero()));
        } catch (FeignException.UnprocessableEntity e){
            logger.error("Erro 422 ao tentar bloquear o cartao={}", obfuscator.hide(cartaoEncontrado.getNumero()));
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cartão já se encontra bloqueado no sistema externo.");
        } catch (RetryableException e){
            logger.error("Erro de conexão com o serviço de cartões={}", e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Conexão mal sucedida com o serviço de cartões.");
        } catch (Exception e){
            logger.error("Erro ao tentar conectar com o serviço de cartões={}", e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro desconhecido no servidor.");
        }
    }
}
