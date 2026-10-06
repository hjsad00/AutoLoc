package tn.esprit.autoloc.repository;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Agence;
public interface IAgenceRepository  extends CrudRepository<Agence, Long> {
}
