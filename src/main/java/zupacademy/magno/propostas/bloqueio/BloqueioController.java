package zupacademy.magno.propostas.bloqueio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            BloqueioFeignResponse resultado = cartoesClient.bloquearCartao(numeroCartao, new BloqueioFeignRequest());
            if(resultado.bloqueado()){
                Bloqueio novoBloqueio = new Bloqueio(servletRequest.getRemoteAddr(), userAgent, cartaoEncontrado);
                cartaoEncontrado.setBloqueio(novoBloqueio);
                transacao.atualizaEComita(cartaoEncontrado);
                logger.info("Novo bloqueio efetuado no cartão={}", obfuscator.hide(cartaoEncontrado.getNumero()));
            }
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
