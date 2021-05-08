package zupacademy.magno.propostas.proposta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import zupacademy.magno.propostas.sistemasexternos.analises.AnaliseRestricaoService;
import zupacademy.magno.propostas.utils.Obfuscator;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PropostaControllerTest {

    private PropostaRepository propostaRepository;
    private ExecutorTransacao executorTransacao;
    private AnaliseRestricaoService analiseRestricaoService;
    private Obfuscator obsfuscator;
    private PropostaController controller;
    private Proposta propostaSemCartao;

    @BeforeEach
    public void initSetup(){
        propostaRepository = mock(PropostaRepository.class);
        executorTransacao = mock(ExecutorTransacao.class);
        analiseRestricaoService = mock(AnaliseRestricaoService.class);
        obsfuscator = mock(Obfuscator.class);
        controller = new PropostaController(
                propostaRepository,
                executorTransacao,
                analiseRestricaoService,
                obsfuscator
        );

        propostaSemCartao = new Proposta(
                "34412114562",
                "test@gmail.com",
                "Teste",
                "de memoria",
                new BigDecimal(2500)
        );
    }

    @Test
    @DisplayName("NAO deve retornar proposta para id nao existente")
    public void test01(){
        when(executorTransacao.executa(any())).thenReturn(Optional.empty());
        ResponseEntity<PropostaResponse> responseEntity = controller.consultaProposta(1L);
        assertEquals(responseEntity, ResponseEntity.notFound().build());
    }

    @Test
    @DisplayName("DEVE retornar proposta para id existente")
    public void test02(){
        when(executorTransacao.executa(any())).thenReturn(Optional.of(propostaSemCartao));
        ResponseEntity<PropostaResponse> respostaRecebida = controller.consultaProposta(1L);
        PropostaResponse propostaResponse = new PropostaResponse(propostaSemCartao);
        assertEquals(propostaResponse.getDocumento(), respostaRecebida.getBody().getDocumento());
        assertEquals(HttpStatus.OK, respostaRecebida.getStatusCode());
    }

}