package zupacademy.magno.propostas.biometria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zupacademy.magno.propostas.proposta.PropostaController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/biometrias")
public class BiometriaController {

    private final Logger logger = LoggerFactory.getLogger(PropostaController.class);

    @PostMapping
    public String criaBiometria(@RequestBody @Valid BiometriaRequest request){
        logger.info("Biometria recebida={}", request.toString());
        return "funfando";
    }
}
