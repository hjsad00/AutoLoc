package tn.esprit.autoloc.repository;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Client;

public interface IClientRepository extends CrudRepository<Client, Long> {
}
