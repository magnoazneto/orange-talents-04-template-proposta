package zupacademy.magno.propostas.proposta;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import zupacademy.magno.propostas.cartao.Cartao;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartaoFeignResponse;
import zupacademy.magno.propostas.sistemasexternos.cartoes.CartoesClient;
import zupacademy.magno.propostas.utils.Obfuscator;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PropostaElegivelCriadaListenerTest {

    private CartoesClient cartoesClientMock;
    private PropostaElegivelCriadaListener listener;
    private Cartao cartaoMockado;
    @Autowired EntityManager manager;

    @BeforeEach
    private void initSetup(){
        Obfuscator obfuscator = new Obfuscator();
        ExecutorTransacao transacao = new ExecutorTransacao(manager);

        cartoesClientMock = mock(CartoesClient.class);
        cartaoMockado = mock(Cartao.class);

        listener = new PropostaElegivelCriadaListener(cartoesClientMock, transacao, obfuscator);

    }

    @Test
    @DisplayName("NAO deve consultar novo cartão se a proposta já tiver cartão associado")
    public void test01(){
        PropostaElegivelCriadaEvent event = mock(PropostaElegivelCriadaEvent.class);
        Proposta proposta = new Proposta(
                "00000000000", "magno@testes.com", "Magno Azevedo", "Beco diagonal", new BigDecimal(2500));
        proposta.setCartao(cartaoMockado);
        when(event.getPropostaElegivel()).thenReturn(proposta);
        assertThrows(IllegalArgumentException.class, () -> {
            listener.consultaCartao(event);
        });
    }

    @Test
    @DisplayName("DEVE atualizar proposta quando o cartão retornar do serviço externo")
    public void test02(){
        PropostaElegivelCriadaEvent event = mock(PropostaElegivelCriadaEvent.class);
        Proposta proposta = new Proposta(
                "00000000000", "magno@testes.com", "Magno Azevedo", "Beco diagonal", new BigDecimal(2500));
        CartaoFeignResponse cartaoResponse = new CartaoFeignResponse(
                "0000-0000-0000-0000",
                "Magno",
                LocalDateTime.now(),
                2500,
                "1"
        );
        when(cartoesClientMock.consultaCartao(any())).thenReturn(cartaoResponse);
        when(event.getPropostaElegivel()).thenReturn(proposta);
        listener.consultaCartao(event);
        Proposta propostaSalva = manager.find(Proposta.class, 1L);
        assertEquals(cartaoResponse.getId(), propostaSalva.getCartao().getNumero());
    }

}