package tn.esprit.autoloc.repository;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Paiement;

public interface IPaiementRepository extends CrudRepository<Paiement, Long> {
}
