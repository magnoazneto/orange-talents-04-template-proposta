package zupacademy.magno.propostas.sistemasexternos.cartoes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "cartoes", url = "${cartoes.host}")
@Component
public interface CartoesClient {

    @RequestMapping(method = RequestMethod.GET,  produces = "application/json")
    public CartaoResponse consultaCartao(@RequestParam("idProposta") Long idProposta);

    @RequestMapping(method = RequestMethod.POST, value = "{id}/bloqueios", produces = "application/json")
    public BloqueioFeignResponse bloquearCartao(@PathVariable("id") String numeroCartao, @RequestBody BloqueioFeignRequest request);

}
