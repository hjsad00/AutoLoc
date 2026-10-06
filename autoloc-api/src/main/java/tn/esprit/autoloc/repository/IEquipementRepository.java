package tn.esprit.autoloc.repository;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Equipement;

public interface IEquipementRepository extends CrudRepository<Equipement, Long> {
}
