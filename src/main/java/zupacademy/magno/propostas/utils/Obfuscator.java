package zupacademy.magno.propostas.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class Obfuscator {

    /**
     * Ofusca uma String se ela tiver tamanho maior que 6.
     * Se for menor, retorna a mesma String
     * @param string String limpa
     * @return String ofuscada com *
     */
    public String hide(String string){
        Assert.notNull(string, "String a ser ofuscada não deveria ser nula");
        if(string.length() <= 6) return string;
        return string.replace(
                string.substring(
                        3, string.length()-3),
                "***"
        );
    }
}
