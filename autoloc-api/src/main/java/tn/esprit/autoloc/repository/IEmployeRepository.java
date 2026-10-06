package tn.esprit.autoloc.repository;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Employe;

public interface IEmployeRepository extends CrudRepository<Employe, Long> {
}
