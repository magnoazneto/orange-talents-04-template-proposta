package zupacademy.magno.propostas.proposta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import zupacademy.magno.propostas.sistemasexternos.analises.*;
import zupacademy.magno.propostas.utils.Obfuscator;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PropostaController {

    @Autowired PropostaRepository propostaRepository;
    @Autowired ExecutorTransacao transacao;
    @Autowired AnaliseRestricaoService analiseRestricaoService;
    @Autowired Obfuscator obfuscator;
    private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

    public PropostaController(PropostaRepository propostaRepository, ExecutorTransacao transacao, AnaliseRestricaoService analiseRestricaoService, Obfuscator obfuscator) {
        this.propostaRepository = propostaRepository;
        this.transacao = transacao;
        this.analiseRestricaoService = analiseRestricaoService;
        this.obfuscator = obfuscator;
    }

    @GetMapping("/propostas/{id}")
    public ResponseEntity<PropostaResponse> consultaProposta(@PathVariable("id") Long id){
        Optional<Proposta> possivelProposta = propostaRepository.findById(id);
        return possivelProposta.map(
                proposta -> ResponseEntity.ok(new PropostaResponse(proposta)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/propostas")
    public ResponseEntity<?> criaProposta(@RequestBody @Valid PropostaRequest request,
                                          UriComponentsBuilder uriComponentsBuilder){
        if(propostaRepository.existsByDocumento(request.getDocumento())){
            logger.warn("Proposta com documento já existente recebida. Documento={}", obfuscator.hide(request.getDocumento()));
            return ResponseEntity.status(422).body("Já existe uma proposta em análise para esse solicitante.");
        }
        Proposta novaProposta = request.toModel();
        transacao.salvaEComita(novaProposta);
        analiseRestricaoService.analisaRestricao(novaProposta);
        logger.info("Proposta={} salva como={}", novaProposta.getId(), novaProposta.getStatusRestricao());

        return ResponseEntity.created(
                uriComponentsBuilder.buildAndExpand(novaProposta.getId()).toUri()).build();
    }

}
