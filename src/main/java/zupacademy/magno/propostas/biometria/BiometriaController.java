package zupacademy.magno.propostas.biometria;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import zupacademy.magno.propostas.cartao.Cartao;
import zupacademy.magno.propostas.cartao.CartaoRepository;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/biometrias")
public class BiometriaController {

    @Autowired CartaoRepository cartaoRepository;
    @Autowired  ExecutorTransacao transacao;

    @PostMapping("/{idCartao}")
    public ResponseEntity<?> criaBiometria(@RequestBody @Valid BiometriaRequest request,
                                           @PathVariable("idCartao") Long idCartao,
                                           UriComponentsBuilder uriComponentsBuilder){
        Optional<Cartao> possivelCartao = cartaoRepository.findById(idCartao);
        return possivelCartao.map(cartaoEncontrado -> {
            Biometria novaBiometria = request.toModel(cartaoEncontrado);
            transacao.salvaEComita(novaBiometria);
            URI uriRetorno = uriComponentsBuilder.path("api/v1/biometrias/{id}").build(novaBiometria.getId());
            return ResponseEntity.created(uriRetorno).build();
        }).orElseGet(() -> ResponseEntity.notFound().build());

    }
}
