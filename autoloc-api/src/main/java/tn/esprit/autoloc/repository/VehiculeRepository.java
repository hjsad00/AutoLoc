package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.autoloc.domain.Vehicule; // Vérifiez que cet import correspond au package de votre classe Vehicule

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    // Vous n'avez rien à écrire ici !
    // JpaRepository fournit déjà des méthodes comme save(), findAll(), findById(), etc.
}