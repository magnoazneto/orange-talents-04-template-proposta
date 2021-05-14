package zupacademy.magno.propostas.bloqueio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import zupacademy.magno.propostas.cartao.Cartao;
import zupacademy.magno.propostas.cartao.CartaoRepository;
import zupacademy.magno.propostas.sistemasexternos.cartoes.BloqueioFeignRequest;
import zupacademy.magno.propostas.sistemasexternos.cartoes.BloqueioFeignResponse;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartaoFeignHandler;
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
    @Autowired CartaoFeignHandler cartaoFeignHandler;

    public BloqueioController(CartaoRepository cartaoRepository, ExecutorTransacao transacao, Obfuscator obfuscator, CartoesClient cartoesClient, CartaoFeignHandler cartaoFeignHandler) {
        this.cartaoRepository = cartaoRepository;
        this.transacao = transacao;
        this.obfuscator = obfuscator;
        this.cartoesClient = cartoesClient;
        this.cartaoFeignHandler = cartaoFeignHandler;
    }

    @PostMapping("/{idCartao}")
    public ResponseEntity<?> novoBloqueio(@PathVariable("idCartao") Long idCartao,
                                          HttpServletRequest servletRequest,
                                          @RequestHeader(value = "User-Agent") String userAgent){
        Optional<Cartao> possivelCartao = cartaoRepository.findById(idCartao);

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
        cartaoFeignHandler.executa(() -> {
            BloqueioFeignResponse bloqueioFeignResponse = cartoesClient.bloquearCartao(
                    cartaoEncontrado.getNumero(), new BloqueioFeignRequest());
            Assert.isTrue(bloqueioFeignResponse.bloqueado(), "O resultado deveria ser BLOQUEADO");
            cartaoEncontrado.setBloqueio(novoBloqueio);
            transacao.atualizaEComita(cartaoEncontrado);
            logger.info("Novo bloqueio efetuado no cartão={}", obfuscator.hide(cartaoEncontrado.getNumero()));
            return null;
        }, "Cartão já se encontra bloqueado no serviço externo.");
    }
}
