package zupacademy.magno.propostas.proposta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PropostaTest {

    private Proposta propostaSemCartao;

    @BeforeEach
    public void initSetup(){
        propostaSemCartao = new Proposta(
                "34412114562",
                "test@gmail.com",
                "Teste",
                "de memoria",
                new BigDecimal(2500)
        );
    }

    @Test
    @DisplayName("DEVE retornar false se nao houver cartao associado a proposta")
    public void test01(){
        assertFalse(propostaSemCartao.existeCartaoAssociado());
    }

    @Test
    @DisplayName("DEVE retornar true se houver cartao associado a proposta")
    public void test02(){
        propostaSemCartao.setIdCartao("0000-0000-0000-0000");
        assertTrue(propostaSemCartao.existeCartaoAssociado());
    }

    @Test
    @DisplayName("DEVE aceitar salario maior que 0")
    public void test03(){
        Proposta novaProposta = new Proposta(
                "34412114562",
                "test@gmail.com",
                "Teste",
                "de memoria",
                new BigDecimal(1)
        );
        assertEquals(novaProposta.getSalario(), new BigDecimal(1));
    }

    @Test
    @DisplayName("NAO deve aceitar salario menor que 0")
    public void test04(){
        assertThrows(IllegalArgumentException.class, () ->{
            new Proposta(
                    "34412114562",
                    "test@gmail.com",
                    "Teste",
                    "de memoria",
                    new BigDecimal(-1)
            );
        });
    }

    @Test
    @DisplayName("NAO deve aceitar salario igual que 0")
    public void test05(){
        assertThrows(IllegalArgumentException.class, () ->{
            new Proposta(
                    "34412114562",
                    "test@gmail.com",
                    "Teste",
                    "de memoria",
                    new BigDecimal(0)
            );
        });
    }

}