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
import zupacademy.magno.propostas.clients.AnalisRestricaoRequest;
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
    ExecutorTransacao executor;

    @Autowired
    AnaliseRestricaoClient analiseRestricaoClient;

    private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

    @PostMapping("/propostas")
    public ResponseEntity<?> criaProposta(@RequestBody @Valid PropostaRequest request,
                                          UriComponentsBuilder uriComponentsBuilder){
        logger.info("Request de Proposta do cliente={} com salario={} e email={} recebida.", request.getDocumento(), request.getSalario().toString(), request.getEmail());

        Optional<Proposta> resultado = propostaRepository.findByDocumento(request.getDocumento());

        return resultado
                .map(propostaExistente -> {
                    logger.warn("Proposta com documento já existente recebida. Documento={}", request.getDocumento());
                    return ResponseEntity.status(422).body("Já existe uma proposta para esse solicitante.");
                })
                .orElseGet(() -> {
            Proposta novaProposta = request.toModel();
            executor.salvaEComita(novaProposta);

            logger.info("Proposta criada={}", novaProposta);

            analisaRestricao(novaProposta);
            logger.info("Proposta pós análise={}", novaProposta);

            executor.atualizaEComita(novaProposta);
            URI uriRetorno = uriComponentsBuilder.path("api/v1/propostas/{id}").build(novaProposta.getId());
            return ResponseEntity.created(uriRetorno).build();
        });
    }

    public void analisaRestricao(Proposta novaProposta){
        logger.info("Enviando nova requisição para analise de restrição={}", novaProposta.toString());
        StatusRestricao status;
        try{
            AnaliseRestricaoResponse response = analiseRestricaoClient.analisaRestricao(new AnalisRestricaoRequest(novaProposta));
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
