package zupacademy.magno.propostas.sistemasexternos.cartoes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import zupacademy.magno.propostas.viagem.AvisoViagemFeignRequest;

@FeignClient(value = "cartoes", url = "${cartoes.host}")
@Component
public interface CartoesClient {

    @RequestMapping(method = RequestMethod.GET,  produces = "application/json")
    public CartaoFeignResponse consultaCartao(@RequestParam("idProposta") Long idProposta);

    @RequestMapping(method = RequestMethod.POST, value = "{id}/bloqueios", produces = "application/json")
    public BloqueioFeignResponse bloquearCartao(@PathVariable("id") String numeroCartao, @RequestBody BloqueioFeignRequest request);

    @RequestMapping(method = RequestMethod.POST, value = "{id}/avisos", produces = "application/json")
    public void informaAvisoViagem(@PathVariable("id") String numeroCartao, @RequestBody AvisoViagemFeignRequest request);

}
