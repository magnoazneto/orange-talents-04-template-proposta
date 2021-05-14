package zupacademy.magno.propostas.cartao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import zupacademy.magno.propostas.carteira.Carteira;
import zupacademy.magno.propostas.carteira.TipoCarteira;
import zupacademy.magno.propostas.proposta.Proposta;
import zupacademy.magno.propostas.proposta.PropostaRepository;
import zupacademy.magno.propostas.utils.transactions.ExecutorTransacao;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CartaoTest {

    @Autowired
    EntityManager manager;
    private ExecutorTransacao transacao;
    @Autowired
    PropostaRepository propostaRepository;
    @Autowired
    CartaoRepository cartaoRepository;

    @BeforeEach
    public void initSetup(){
        transacao = new ExecutorTransacao(manager);
    }

    @Test
    @DisplayName("DEVE retornar true se o Cartao já possuir uma Carteira especifica")
    void possuiCarteira() {
        Proposta proposta = new Proposta("00000000000", "magno@gmail.com", "Magno", "Beco Diagonal", new BigDecimal(2500));
        Cartao cartao = new Cartao("0000-0000-0000-0000", "Magno", LocalDateTime.now(), 2500,  proposta);
        Carteira carteiraPaypal = new Carteira(TipoCarteira.PAYPAL, cartao, "magno@gmail.com");

        transacao.salvaEComita(proposta);
        transacao.salvaEComita(cartao);
        proposta.setCartao(cartao);
        cartao.addCarteira(carteiraPaypal);
        transacao.atualizaEComita(cartao);

        assertTrue(cartao.possuiCarteira(TipoCarteira.PAYPAL));
    }
}