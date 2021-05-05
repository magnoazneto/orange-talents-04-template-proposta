package zupacademy.magno.propostas.proposta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/v1")
public class PropostaController {

    @Autowired
    EntityManager manager;

    @PostMapping("/propostas")
    @Transactional
    public ResponseEntity<?> criaProposta(@RequestBody @Valid PropostaRequest request,
                                          UriComponentsBuilder uriComponentsBuilder){
        Proposta novaProposta = request.toModel();
        manager.persist(novaProposta);
        URI uriRetorno = uriComponentsBuilder.path("/propostas/{id}").build(novaProposta.getId());
        return ResponseEntity.created(uriRetorno).build();
    }
}
