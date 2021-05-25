package zupacademy.magno.propostas.proposta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PropostaRepository extends JpaRepository<Proposta, Long> {
    Set<Proposta> findPropostaByStatusRestricaoAndCartaoId(StatusRestricao status, Long idCartao);

    boolean existsByDocumento(String documento);
}
