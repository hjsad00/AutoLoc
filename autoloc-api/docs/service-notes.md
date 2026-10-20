# Atelier 4 : Couche Service, Inversion de Contrôle et Injection de Dépendances

## Section A. Modes d'injection comparés

| Critère | Injection par constructeur | Injection par attribut (`@Autowired`) |
| :--- | :--- | :--- |
| **Champ `final` possible ?** | **Oui** (garantit l'immuabilité et la sécurité en environnement multithread) | **Non** (le conteneur doit injecter la valeur après instanciation) |
| **Dépendance visible dans la signature de la classe ?** | **Oui** (explicite dans les paramètres du constructeur) | **Non** (dépendance cachée, invisible à la lecture des constructeurs) |
| **Classe utilisable avec `new` hors conteneur sans dépendance null ?** | **Oui** (le constructeur impose de fournir le collaborateur, empêchant tout état incomplet) | **Non** (l'instanciation manuelle laisse le champ à `null`, risquant des `NullPointerException`) |
| **Signalé par SonarQube for IDE ?** | **Non** (bonne pratique recommandée) | **Oui** (règle **S6813** : *"Field dependency injection should not be used"*) |

---

## Section B. Services

| Service | Dépendances injectées | Mode | Justification |
| :--- | :--- | :--- | :--- |
| `ContratServiceImpl` | `IContratRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire, champ `final` immuable, testabilité sans conteneur, respect de la règle S6813. |
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire, champ `final` immuable, garantie d'un composant toujours valide à l'instanciation. |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire, champ `final` immuable, couplage faible via l'interface du repository. |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire, champ `final` immuable, isolation et intégrité des opérations métier. |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire, champ `final` immuable, injection explicite et transparente. |
| `ClientServiceImpl` | `IClientRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire, champ `final` immuable, prévention de dépendances non résolues. |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire, champ `final` immuable, gestion propre du cycle de vie par Spring IoC. |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire, champ `final` immuable, service en lecture seule accédant aux données financières. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire, champ `final` immuable, respect strict des conventions AutoLoc. |

---

## Section C. Messages d'erreur du conteneur

### Message 1
```
APPLICATION FAILED TO START
Description:
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.ContratController
required a bean of type 'tn.esprit.autoloc.service.IContratService'
that could not be found.
Action:
Consider defining a bean of type 'tn.esprit.autoloc.service.IContratService'
in your configuration.
```

- **Exception sous-jacente** : `NoSuchBeanDefinitionException` (encapsulée dans une `UnsatisfiedDependencyException`).
- **Bean concerné** : `ContratController` qui requiert un bean implémentant `IContratService`.
- **Deux causes les plus probables côté `ContratServiceImpl`** :
  1. **Annotation manquante** : L'annotation de stéréotype `@Service` (ou `@Component`) a été oubliée sur la classe `ContratServiceImpl`.
  2. **Hors package de scan** : La classe `ContratServiceImpl` est située dans un package non couvert par le scan de composants de Spring Boot (en dehors du sous-arbre de `tn.esprit.autoloc`).
- **Correction** :
  1. Ajouter l'annotation `@Service` sur `ContratServiceImpl`.
  2. Déplacer la classe dans le package `tn.esprit.autoloc.service` pour qu'elle soit détectée automatiquement par `@SpringBootApplication`.

---

### Message 2
```
Description:
Parameter 0 of constructor in 
tn.esprit.autoloc.web.controller.NotificationController
required a single bean, but 2 were found:
 - emailNotificateur
 - smsNotificateur
Action:
Consider marking one of the beans as @Primary, updating the consumer to accept
multiple beans, or using @Qualifier to identify the bean that should be consumed
```

- **Exception sous-jacente** : `NoUniqueBeanDefinitionException` (encapsulée dans une `UnsatisfiedDependencyException`).
- **Bean concerné** : `NotificationController` qui a deux candidats disponibles pour la même interface de notification.
- **Deux propositions de corrections différentes** :
  1. **Utiliser `@Primary`** :
     - *Emplacement* : Sur la classe d'implémentation par défaut (ex. `@Component @Primary public class EmailNotificateur ...`).
     - *Effet* : Indique au conteneur Spring de sélectionner prioritairement cette implémentation sans modifier la classe consommatrice (`NotificationController`).
  2. **Utiliser `@Qualifier`** :
     - *Emplacement* : Sur le paramètre du constructeur du consommateur (ex. `public NotificationController(@Qualifier("emailNotificateur") INotificateur notificateur)`).
     - *Effet* : Lève explicitement l'ambiguïté au point d'injection en désignant nommément le bean requis.

---

### Message 3
```
The dependencies of some of the beans in the application context form a cycle:
 clientServiceImpl
 | ^
 v |
 reservationServiceImpl
```

- **Exception sous-jacente** : `BeanCurrentlyInCreationException`.
- **Pourquoi l'injection par constructeur rend-elle ce cycle impossible à contourner ?**
  L'injection par constructeur exige que toutes les dépendances d'un bean soient complètement construites avant de pouvoir appeler son constructeur. Pour construire `ClientServiceImpl`, Spring a besoin d'une instance de `ReservationServiceImpl`, mais pour construire cette dernière, il a impérativement besoin d'une instance de `ClientServiceImpl`. Aucun des deux beans ne pouvant être instancié en premier, le conteneur ne peut pas résoudre le graphe d'instanciation et s'arrête en erreur.
- **Piste de conception pour supprimer ce cycle** :
  Appliquer le principe de responsabilité unique (SRP) :
  - Extraire la logique ou responsabilité croisée dans un troisième service médiateur ou transverse (ex. `ReservationClientService` ou `ReservationWorkflowService`).
  - Ou faire en sorte que l'un des deux services fasse appel au repository de l'autre entité plutôt qu'au service lui-même, ou encore utiliser la publication d'événements applicatifs (`ApplicationEventPublisher`).

---

## Section D. Anomalies SonarQube for IDE

| Règle SonarQube | Intitulé / Explication | Correction apportée |
| :--- | :--- | :--- |
| **S6813** | **Field dependency injection should not be used**<br>L'injection par champ (`@Autowired private IContratRepository contratRepository;`) masque les dépendances réelles, empêche l'immuabilité (`final`), et peut provoquer des `NullPointerException` hors conteneur. | Remplacement par l'injection par constructeur via Lombok `@RequiredArgsConstructor` sur des champs déclarés `private final`. |
| **S112** | **Generic exceptions should never be thrown**<br>Lancer des exceptions génériques (`RuntimeException`, `Exception`) nuit à la lisibilité et empêche un traitement ciblé des erreurs par les couches supérieures. | Création et utilisation d'une exception métier explicite `ResourceNotFoundException extends RuntimeException` contextualisant l'entité et l'identifiant. |
| **S1128** | **Unused imports should be removed**<br>Les imports non utilisés encombrent le code source, augmentent le couplage apparent et nuisent à la maintenabilité. | Nettoyage des imports superflus dans l'ensemble des interfaces et classes d'implémentation de la couche `service`. |
| **S125** | **Sections of code should not be commented out**<br>Des commentaires de pseudo-code ou d'instructions laissés dans les méthodes constituent de la dette technique. | Suppression des commentaires temporaires et implémentation concrète de la copie de tous les attributs modifiables lors des opérations de mise à jour (`update`). |

---

## Section E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**
   Il n'y a aucun opérateur `new ContratServiceImpl(...)` dans notre code applicatif. C'est le conteneur Spring IoC (`ApplicationContext`) qui instancie la classe par réflexion au démarrage du projet, lors du scan des composants annotés par `@Service`.

2. **Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?**
   Pour respecter le principe d'Inversion des Dépendances (DIP de SOLID) et maintenir un couplage faible. Le contrôleur s'appuie sur un contrat abstrait (l'interface), ce qui permet de modifier ou remplacer l'implémentation (ex. ajouter un cache, des proxys transactionnels ou de sécurité) et d'injecter des mocks lors des tests unitaires sans altérer le contrôleur.

3. **Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.**
   Un bean singleton est unique et partagé simultanément par l'ensemble des threads traitant les requêtes HTTP concurrentes. S'il conserve un état modifiable (ex: un champ d'instance `private Contrat contratEnCours;`), une requête A traitée sur un thread modifiera la variable partagée en plein milieu du traitement d'une requête B sur un autre thread, provoquant des conditions de course (*race conditions*) et une corruption critique des données.

4. **Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?**
   Passer directement l'objet reçu à `save(...)` effectuerait une fusion (*merge*) aveugle qui écraserait toutes les colonnes par les valeurs transmises (mettant à `null` les champs omis dans la requête) et pourrait supprimer par cascade (`orphanRemoval`) les collections associées. Charger l'existant permet de s'assurer de l'existence de la ressource, de contrôler les règles métier et de recopier uniquement les attributs modifiables tout en protégeant les identifiants et les relations.

5. **Quelle différence entre `@Component` et `@Bean` ? Entre `@Primary` et `@Qualifier` ? Pourquoi `IPaiementService` n'expose-t-il ni création, ni modification, ni suppression ?**
   - **`@Component` vs `@Bean`** : `@Component` est une annotation au niveau de la classe pour les composants de l'application scannés automatiquement par Spring ; `@Bean` est une annotation au niveau d'une méthode au sein d'une classe `@Configuration`, servant à enregistrer manuellement des objets (notamment issus de bibliothèques tierces ou nécessitant une instanciation sur mesure).
   - **`@Primary` vs `@Qualifier`** : `@Primary` qualifie un bean comme candidat par défaut au niveau de sa déclaration ; `@Qualifier("nom")` spécifie explicitement au point d'injection quel bean précis doit être injecté pour trancher une ambiguïté.
   - **Pourquoi `IPaiementService` est en lecture seule ?** : Un paiement est une opération transactionnelle financièrement sensible qui dépend du cycle de vie du Contrat. Métierement, un paiement ne doit pas pouvoir être créé, édité ou supprimé de façon autonome ; sa gestion est orchestrée au travers du Contrat associé.
