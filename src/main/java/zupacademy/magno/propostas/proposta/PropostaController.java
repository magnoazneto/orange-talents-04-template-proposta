package zupacademy.magno.propostas.proposta;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import zupacademy.magno.propostas.clients.AnaliseRestricaoRequest;
import zupacademy.magno.propostas.clients.AnaliseRestricaoClient;
import zupacademy.magno.propostas.clients.AnaliseRestricaoResponse;
import zupacademy.magno.propostas.clients.RetornoRestricao;
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

    private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

    @PostMapping("/propostas")
    public ResponseEntity<?> criaProposta(@RequestBody @Valid PropostaRequest request,
                                          UriComponentsBuilder uriComponentsBuilder){

        // a cada chamada dos métodos de transacao, um commit é feito.
        Optional<Proposta> possivelProposta = transacao.executa(() -> {
            return propostaRepository.findByDocumento(request.getDocumento());
        });

        return possivelProposta
                .map(propostaExistente -> {
                    logger.warn("Proposta com documento já existente recebida. Documento={}", request.getDocumento());
                    return ResponseEntity.status(422).body("Já existe uma proposta para esse solicitante.");
                })
                .orElseGet(() -> {
            Proposta novaProposta = request.toModel();
            transacao.salvaEComita(novaProposta);
            analisaRestricao(novaProposta);
            transacao.atualizaEComita(novaProposta);

            logger.info("Proposta salva={}", novaProposta);

            URI uriRetorno = uriComponentsBuilder.path("api/v1/propostas/{id}").build(novaProposta.getId());
            return ResponseEntity.created(uriRetorno).build();
        });
    }

    public void analisaRestricao(Proposta novaProposta){
        logger.info("Enviando nova requisição para analise de restrição={}", novaProposta.toString());
        StatusRestricao status;
        try{
            AnaliseRestricaoResponse response = analiseRestricaoClient.analisaRestricao(new AnaliseRestricaoRequest(novaProposta));
            logger.info("Resposta positiva={}", response.toString());
            assert response.getResultadoSolicitacao().equals(RetornoRestricao.SEM_RESTRICAO);
            status = StatusRestricao.ELEGIVEL;
        } catch (FeignException.UnprocessableEntity e){
            logger.warn("Resposta negativa. Feign={}",  e.getMessage());
            status = StatusRestricao.NAO_ELEGIVEL;
        }
        novaProposta.setEstadoRestricao(status);
    }
}
