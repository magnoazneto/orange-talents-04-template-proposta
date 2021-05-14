package zupacademy.magno.propostas.bloqueio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import zupacademy.magno.propostas.cartao.Cartao;
import zupacademy.magno.propostas.cartao.CartaoRepository;
import zupacademy.magno.propostas.proposta.Proposta;
import zupacademy.magno.propostas.proposta.PropostaRepository;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartaoFeignHandler;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartoesClient;
import zupacademy.magno.propostas.utils.Obfuscator;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BloqueioControllerTest {

    @Autowired CartaoRepository cartaoRepository;
    @Autowired PropostaRepository propostaRepository;
    @Autowired EntityManager manager;
    ExecutorTransacao transacao;
    Obfuscator obfuscator = new Obfuscator();
    private CartoesClient cartoesClientMock;
    private CartaoFeignHandler cartaoFeignHandlerMock;
    private BloqueioController controller;
    private Proposta propostaPadrao;


    @BeforeEach
    public void initSetup(){
        cartoesClientMock = mock(CartoesClient.class);
        cartaoFeignHandlerMock = mock(CartaoFeignHandler.class);
        transacao = new ExecutorTransacao(manager);
        controller = new BloqueioController(cartaoRepository, transacao, obfuscator, cartoesClientMock, cartaoFeignHandlerMock);
        propostaPadrao = new Proposta("00000000000", "magno@gmail.com", "Magno",  "Beco diagonal", new BigDecimal(2500));
    }

    @Test
    @DisplayName("NAO deve criar bloqueio quando o Cartão já estiver bloqueado")
    public void test01(){
        HttpServletRequest servletRequestMock = mock(HttpServletRequest.class);
        Cartao novoCartao = new Cartao("0000-0000-0000-0000", "Magno", LocalDateTime.now(), 2500, propostaPadrao);

        transacao.salvaEComita(propostaPadrao);
        transacao.salvaEComita(novoCartao);
        propostaPadrao.setCartao(novoCartao);
        transacao.atualizaEComita(propostaPadrao);

        Bloqueio bloqueio = new Bloqueio("0.0.0.0.0.0:1", "userAgent", novoCartao);
        novoCartao.setBloqueio(bloqueio);
        transacao.atualizaEComita(novoCartao);

        when(servletRequestMock.getRemoteAddr()).thenReturn("0.0.0.0.0.0:1");
        ResponseEntity<?> responseEntity = controller.novoBloqueio(1L, servletRequestMock, "userAgent");
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("DEVE criar bloqueio quando o Cartão estiver sem bloqueio")
    public void test02(){
        HttpServletRequest servletRequestMock = mock(HttpServletRequest.class);
        Cartao novoCartao = new Cartao("0000-0000-0000-0000", "Magno", LocalDateTime.now(), 2500, propostaPadrao);

        transacao.salvaEComita(propostaPadrao);
        transacao.salvaEComita(novoCartao);
        propostaPadrao.setCartao(novoCartao);
        transacao.atualizaEComita(propostaPadrao);

        when(servletRequestMock.getRemoteAddr()).thenReturn("0.0.0.0.0.0:1");
        ResponseEntity<?> responseEntity = controller.novoBloqueio(1L, servletRequestMock, "userAgent");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("DEVE retornar 404 se não houver cartão cadastrado")
    public void test03(){
        HttpServletRequest servletRequestMock = mock(HttpServletRequest.class);
        when(servletRequestMock.getRemoteAddr()).thenReturn("0.0.0.0.0.0:1");

        ResponseEntity<?> responseEntity = controller.novoBloqueio(1L, servletRequestMock, "userAgent");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

}