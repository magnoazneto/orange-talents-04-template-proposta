package zupacademy.magno.propostas.viagem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zupacademy.magno.propostas.cartao.Cartao;
import zupacademy.magno.propostas.cartao.CartaoRepository;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartaoFeignHandler;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartoesClient;
import zupacademy.magno.propostas.utils.Obfuscator;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/viagens")
public class AvisoViagemController {

    @Autowired CartaoRepository cartaoRepository;
    private final Logger logger = LoggerFactory.getLogger(AvisoViagemController.class);
    @Autowired Obfuscator obfuscator;
    @Autowired ExecutorTransacao transacao;
    @Autowired CartaoFeignHandler cartaoFeignHandler;
    @Autowired CartoesClient cartoesClient;

    public AvisoViagemController(CartaoRepository cartaoRepository, Obfuscator obfuscator, ExecutorTransacao transacao) {
        this.cartaoRepository = cartaoRepository;
        this.obfuscator = obfuscator;
        this.transacao = transacao;
    }

    @PostMapping("/{idCartao}")
    public ResponseEntity<?> criaAvisoViagem(@RequestBody @Valid AvisoViagemRequest request,
                                          @PathVariable("idCartao") Long idCartao,
                                          HttpServletRequest servletRequest,
                                          @RequestHeader(value = "User-Agent") String userAgent){

        Optional<Cartao> possivelCartao = cartaoRepository.findById(idCartao);
        return possivelCartao.map(cartaoEncontrado -> {
            AvisoViagem novoAvisoViagem = request.toModel(cartaoEncontrado, servletRequest.getRemoteAddr(), userAgent);
            cartaoFeignHandler.executa(() -> {
                cartoesClient.informaAvisoViagem(cartaoEncontrado.getNumero(), new AvisoViagemFeignRequest(novoAvisoViagem));
                transacao.salvaEComita(novoAvisoViagem);
                logger.info("Aviso de viagem criado para o cartão={}", obfuscator.hide(cartaoEncontrado.getNumero()));
                return null;
            }, "Já existe uma viagem encerrando nesta data.");
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());

    }
}
