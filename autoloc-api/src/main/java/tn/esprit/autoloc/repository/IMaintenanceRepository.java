package tn.esprit.autoloc.repository;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Maintenance;

public interface IMaintenanceRepository extends CrudRepository<Maintenance, Long> {
}
