package zupacademy.magno.propostas.config.criptografia;

import org.springframework.security.crypto.encrypt.Encryptors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class EncryptToDatabase implements AttributeConverter<String, String> {


    @Override
    public String convertToDatabaseColumn(String string) {
        try{
            return Encryptors.queryableText("${proposta.criptografia.secret}", "12345678").encrypt(string);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String string) {
        try{
            return Encryptors.queryableText("${proposta.criptografia.secret}", "12345678").decrypt(string);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
