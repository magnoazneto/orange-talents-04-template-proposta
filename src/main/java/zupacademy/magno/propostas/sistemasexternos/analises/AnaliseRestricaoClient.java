package zupacademy.magno.propostas.sistemasexternos.analises;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "analises", url = "${analises.host}")
@Component
public interface AnaliseRestricaoClient {

    @RequestMapping(method = RequestMethod.POST ,produces = "application/json")
    public AnaliseRestricaoResponse analisaRestricao(@RequestBody AnaliseRestricaoRequest request);
}

