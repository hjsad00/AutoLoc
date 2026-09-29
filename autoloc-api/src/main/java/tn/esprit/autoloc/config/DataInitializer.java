package tn.esprit.autoloc.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.repository.VehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initVehicules(VehiculeRepository vehiculeRepository) {
        return args -> {
            if (vehiculeRepository.count() == 0) {
                Vehicule v1 = new Vehicule();
                v1.setMarque("Peugeot");
                v1.setModele("208");
                v1.setImmatriculation("123-TU-4567");
                v1.setCategorie(CategorieVehicule.CITADINE);
                v1.setStatut(StatutVehicule.DISPONIBLE);
                v1.setTarifJournalier(new BigDecimal("50.00"));

                Vehicule v2 = new Vehicule();
                v2.setMarque("Renault");
                v2.setModele("Clio 5");
                v2.setImmatriculation("987-TU-6543");
                v2.setCategorie(CategorieVehicule.CITADINE);
                v2.setStatut(StatutVehicule.DISPONIBLE);
                v2.setTarifJournalier(new BigDecimal("55.00"));

                Vehicule v3 = new Vehicule();
                v3.setMarque("Volkswagen");
                v3.setModele("Tiguan");
                v3.setImmatriculation("456-TU-1234");
                v3.setCategorie(CategorieVehicule.SUV);
                v3.setStatut(StatutVehicule.DISPONIBLE);
                v3.setTarifJournalier(new BigDecimal("120.00"));

                vehiculeRepository.saveAll(List.of(v1, v2, v3));
                System.out.println("✅ Véhicules de démonstration insérés avec succès !");
            }
        };
    }
}