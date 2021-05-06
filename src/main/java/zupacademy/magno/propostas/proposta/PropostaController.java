package zupacademy.magno.propostas.proposta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

    @PostMapping("/propostas")
    @Transactional
    public ResponseEntity<?> criaProposta(@RequestBody @Valid PropostaRequest request,
                                          UriComponentsBuilder uriComponentsBuilder){

        logger.info(String.format("Request de Proposta do cliente=%s com salario=%s e email=%s recebida.", request.getDocumento(), request.getSalario().toString(), request.getEmail()));
        Proposta novaProposta = request.toModel();
        manager.persist(novaProposta);
        logger.info(String.format("Proposta criada com id=%s, salário=%s, email=%s", novaProposta.getId(), novaProposta.getSalario(), novaProposta.getEmail()));
        URI uriRetorno = uriComponentsBuilder.path("/propostas/{id}").build(novaProposta.getId());
        return ResponseEntity.created(uriRetorno).build();
    }
}
