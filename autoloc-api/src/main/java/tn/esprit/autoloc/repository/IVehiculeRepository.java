package tn.esprit.autoloc.repository;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Vehicule;

public interface IVehiculeRepository  extends CrudRepository<Vehicule, Long> {
}
