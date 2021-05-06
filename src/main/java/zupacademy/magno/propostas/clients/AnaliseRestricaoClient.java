package zupacademy.magno.propostas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "solicitacao", url = "http://127.0.0.1:9999/api")
@Component
public interface AnaliseRestricaoClient {

    @RequestMapping(method = RequestMethod.POST, value = "/solicitacao", produces = "application/json")
    public AnaliseRestricaoResponse analisaRestricao(AnalisRestricaoRequest request);

}