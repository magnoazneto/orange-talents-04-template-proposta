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

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/v1")
public class PropostaController {

    @Autowired
    PropostaRepository propostaRepository;

    private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

    @PostMapping("/propostas")
    @Transactional
    public ResponseEntity<?> criaProposta(@RequestBody @Valid PropostaRequest request,
                                          UriComponentsBuilder uriComponentsBuilder){
        logger.trace("Request de Proposta do cliente={} com salario={} e email={} recebida.", request.getDocumento(), request.getSalario().toString(), request.getEmail());

        Optional<Proposta> resultado = propostaRepository.findByDocumento(request.getDocumento());

        return resultado
                .map(propostaExistente -> {
                    logger.warn("Proposta com documento já existente recebida. Documento={}", request.getDocumento());
                    return ResponseEntity.status(422).body("Já existe uma proposta para esse solicitante.");
                })
                .orElseGet(() -> {
            Proposta novaProposta = request.toModel();
            propostaRepository.save(novaProposta);
            logger.info("Proposta criada com id={}, salário={}, email={}", novaProposta.getId(), novaProposta.getSalario(), novaProposta.getEmail());

            URI uriRetorno = uriComponentsBuilder.path("/propostas/{id}").build(novaProposta.getId());
            return ResponseEntity.created(uriRetorno).build();
        });
    }
}
