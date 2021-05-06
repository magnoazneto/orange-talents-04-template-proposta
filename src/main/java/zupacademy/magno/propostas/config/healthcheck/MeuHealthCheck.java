package zupacademy.magno.propostas.config.healthcheck;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MeuHealthCheck implements HealthIndicator {

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        details.put("versão", "1.2.3");
        details.put("descrição", "Aqui pode vir alguma informação customizada caso solicitado");
        details.put("endereço", "127.0.0.1");
        return Health.up().withDetails(details).build();
    }
}
