package zupacademy.magno.propostas.proposta;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import zupacademy.magno.propostas.sistemasexternos.analises.AnaliseRestricaoRequest;
import zupacademy.magno.propostas.sistemasexternos.analises.AnaliseRestricaoClient;
import zupacademy.magno.propostas.sistemasexternos.analises.AnaliseRestricaoResponse;
import zupacademy.magno.propostas.sistemasexternos.analises.RetornoRestricao;
import zupacademy.magno.propostas.utils.Obfuscator;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PropostaController {

    @Autowired
    PropostaRepository propostaRepository;

    @Autowired
    ExecutorTransacao transacao;

    @Autowired
    AnaliseRestricaoClient analiseRestricaoClient;
    @Autowired
    Obfuscator obfuscator;

    private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

    @GetMapping("/propostas/{id}")
    public ResponseEntity<PropostaResponse> consultaProposta(@PathVariable("id") Long id){
        Optional<Proposta> possivelProposta = transacao.executa(() -> propostaRepository.findById(id));
        return possivelProposta.map(
                proposta -> ResponseEntity.ok(new PropostaResponse(proposta)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/propostas")
    public ResponseEntity<?> criaProposta(@RequestBody @Valid PropostaRequest request,
                                          UriComponentsBuilder uriComponentsBuilder){

        Optional<Proposta> possivelProposta = transacao.executa(() -> propostaRepository.findByDocumento(request.getDocumento()));

        return possivelProposta
                .map(propostaExistente -> {
                    logger.warn("Proposta com documento já existente recebida. Documento={}", obfuscator.hide(request.getDocumento()));
                    return ResponseEntity.status(422).body("Já existe uma proposta para esse solicitante.");
                })
                .orElseGet(() -> {
            Proposta novaProposta = request.toModel();
            transacao.salvaEComita(novaProposta);
            analisaRestricao(novaProposta);
            transacao.atualizaEComita(novaProposta);
            logger.info("Proposta={} salva como={}", novaProposta.getId(), novaProposta.getStatusRestricao());

            URI uriRetorno = uriComponentsBuilder.path("api/v1/propostas/{id}").build(novaProposta.getId());
            return ResponseEntity.created(uriRetorno).build();
        });
    }

    public void analisaRestricao(Proposta novaProposta){
        logger.info("Enviando analise da proposta de id={}", novaProposta.getId());
        StatusRestricao status;
        try{
            AnaliseRestricaoResponse response = analiseRestricaoClient.analisaRestricao(new AnaliseRestricaoRequest(novaProposta));
            logger.info("Resposta positiva para a proposta={}", response.getIdProposta());
            Assert.isTrue(response.getResultadoSolicitacao().equals(RetornoRestricao.SEM_RESTRICAO), "O resultado deveria ser SEM_RESTRICAO");
            status = StatusRestricao.ELEGIVEL;
        } catch (FeignException.UnprocessableEntity e){
            logger.warn("Resposta negativa. Feign={}",  e.getMessage());
            status = StatusRestricao.NAO_ELEGIVEL;
        }
        novaProposta.setStatusRestricao(status);
    }

}
