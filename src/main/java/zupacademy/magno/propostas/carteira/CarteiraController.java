package zupacademy.magno.propostas.carteira;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import zupacademy.magno.propostas.cartao.Cartao;
import zupacademy.magno.propostas.cartao.CartaoRepository;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartaoFeignHandler;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CarteiraFeignRequest;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartoesClient;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/carteiras")
public class CarteiraController {

    @Autowired CartaoRepository cartaoRepository;
    @Autowired ExecutorTransacao transacao;
    private final Logger logger = LoggerFactory.getLogger(CarteiraController.class);
    @Autowired CartaoFeignHandler cartaoFeignHandler;
    @Autowired CartoesClient cartoesClient;


    @PostMapping("/{idCartao}")
    public ResponseEntity<?> criaCarteira(@PathVariable("idCartao") Long idCartao,
                                          @RequestBody @Valid CarteiraRequest request,
                                          UriComponentsBuilder uriComponentsBuilder){
        Optional<Cartao> possivelCartao = cartaoRepository.findById(idCartao);
        return possivelCartao.map(cartaoEncontrado -> {
            if (cartaoEncontrado.possuiCarteira(request.getCarteira())){
                return ResponseEntity.unprocessableEntity().body("Cartão já possui essa carteira cadastrada.");
            }
            Carteira novaCarteira = request.toModel(cartaoEncontrado);
            cartaoFeignHandler.executa(() -> {
                cartoesClient.associaCarteira(cartaoEncontrado.getNumero(), new CarteiraFeignRequest(novaCarteira));
                cartaoEncontrado.addCarteira(novaCarteira);
//                transacao.salvaEComita(novaCarteira);
                transacao.atualizaEComita(cartaoEncontrado);
                return null;
            }, "Já existe uma carteira com dados semelhantes cadastrada.");
            URI uriRetorno = uriComponentsBuilder.path("api/v1/carteiras/{id}").build(novaCarteira.getId());
            return ResponseEntity.created(uriRetorno).build();
        }).orElseGet(() -> ResponseEntity.notFound().build());

    }
}
