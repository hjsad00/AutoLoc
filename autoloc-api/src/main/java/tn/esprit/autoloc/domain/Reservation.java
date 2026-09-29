package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.autoloc.domain.StatutReservation;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;
    @OneToOne (cascade = CascadeType.ALL)
    private Contrat contrat;
    @ManyToOne (cascade = CascadeType.ALL)
    private Client client;
    @ManyToOne (cascade = CascadeType.ALL)
    private Vehicule vehicule ;


}
