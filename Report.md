# Sommario

- [Descrizione del progetto e funzionalita'](#descrizione-del-progetto-e-funzionalita)
- [Scelte implementative](#scelte-implementative)
- [Esecuzione dell'applicativo](#esecuzione-dellapplicativo)
- [Difficolta' riscontrate](#difficolta-riscontrate)
- Tecnologie utilizzate e build

# Descrizione del progetto e funzionalita'

Lo scopo del progetto e' quella di creare una piu' che semplificata applicazione manageriale per la gestione della spedizione dei prodotti da parte di utenti. Si pensi ai singoli dipendenti di Amazon ad esempio quando devono registrare l'invio di un pacco da una sede verso una certa destinazione (cliente).

L'applicativo presenta due `view` gestite completamente in moniera modulare tra di loro, dove l'unica interazione e' lo switch tra la prima finistra (quella del login) e la seconda finestra (quella della gestione dei prodotti), sotto responsabilita' dalla prima finestra. Notare, che i junit test, testano effettivamente lo switch dalla finestra di login ad un altra finestra attraverso l'implementazione di una finestra `FakePanel` di mock, nulla a che vedere con l'implementazione reale mostrata dall'applicativo ovvero `ProductSwingView`.

#### LoginView

<img width="716" height="436" alt="Image" src="https://github.com/user-attachments/assets/64cd6f93-9bfc-4a66-9e2c-ac7e914d9539" />

#### ProductView

<img width="986" height="460" alt="Image" src="https://github.com/user-attachments/assets/35fb08a9-059c-4da8-a73d-e9d1d980d9fd" />

# Scelte implementative

Per il progetto e' stata utilizzata un'architettura di tipo `Model-View-Controller`. Ogni dominio del model view controller e' contenuto in un package separato.

- Model  \
Contiene due classi core che rappresentano le entita' utilizzate nell'applicazione: `User` e `Product`
  - User \
        La classe user contiene i campi: `Username`, `Password` ed `id`. Sebbene in applicativi reali l'id venga spesso generato ed assegnato in modo automatizzato, ad esempio, attraverso l'utilizzo di `INCREMENT` in database relazionali, in questo progetto e' stato scelto l'inserimento di un `id` manuale per non creare troppe differenze concettuali tra l'implementazione con `MongoDB` e quella con `MariaDB`. Certo, anche con `MongoDB` sarebbe stato possibile automatizzare l'inserimento dell'id sia che per l'utente che per i prodotti ma la semplificazione e' stata voluta anche per eseguire qualche controllo in piu' nel controller al fine di esercitarsi il piu' possibile con il `TDD`. Inoltre da notare le password conservate in un database non sono `hashate` prima di essere salvate. Di nuovo, una mera semplificazione di quella che dovrebbe essere un applicativo reale.
  - Product \
        La classe product contiene per ogni prodotto, l'utente associato che sta manipolando il prodotto (utile in fase di salvataggio/lettura) e le informazioni reali del pacchetto da spedire. L'utente passato al prodotto, non e' da confondere con l'instanza dell'utente passata solitamente ai metodi del controller, in quanto quest'ultima effettua controlli sulla `sessione` di chi sta compiendo l'azione. Anche qui, il concetto di user session e' semplificato e mancano i controlli per effettuare il logout dopo un certo perieodo di tempo e altri cotrolli di sicurezza.
- Controller \
Il controller e' il core dell'applicativo il quale effettua diversi controlli logici attuando da "`logica di backend`".
  - UserController \
        La classe `UserController` viene utilizzata dalla view che si occupa della registrazione e del login degli utenti. Quando un login va a buon fine, il controlla richiama il metodo per chiudere il pannello attuale ed aprire il prossima panel, il tutto gestito internamente dalla view dedicata al `Login`. \
        Tra i controlli implementati si puo' notare
    - La lunghezza della password
    - Validazione del token
    - Presenza di un utente con lo stesso id o username
    - Controllo validita' credenziali fornite per il login
  - ProductController \
            La classe `ProductController` viene utilizzata dalla view che si occupa dell'inserimento e della rimozione dei vari prodotti. Nella classe si possono notare alcuni controlli quali
    - Esistenza del prodotto con lo stesso ID
    - Se il pacchetto e' gia stato inviato allo stesso client da parte dell'utente attualmente loggato
    - Inserimento di pacchi per conto di altri utenti
    - Rimozione di pacchi per conto di altri utenti
    - Rimozione di pacchi che non esistono
- Repository \
Siccome sono stati implementati due database: `MariaDB` e `MongoDB` tutti i controlli sono duplicati per entrambi i database. Infatti si puo' notare che l'interfaccia `UserRepository` (idem per i product) e' implmentata sia dal repository che si interfaccia con `MongoDB` che con `MariaDB`. Le classi dei test sono separate e testano:
  - UserMongoDB
  - ProductMongoDB
  - UserMariaDB
  - ProductMariaDB

La logica e' fondamentalmente la stessa con differenze sintattiche nella vera implementazione delle due tecnologie.

C'e' una cosa da far notare. Le tabelle, nel database relazionale, `User` e `Product` sono collegate tramite `REFERENCE KEY` sull'id dell'utente. Questo risulta comodo per effettuare queries con join e ottenere i dati in maniera semplice riguardo un utente. Dunque, ad un utente possono essere associati N prodotti spediti ma non vale il contrario, ovvero un prodotto risultera' spedito da un utente particolare. Anche se dovesse essere presente un prodotto nel database con cliente uguale ma id differente, il prodotto viene considerato diverso. Questo ha senso in realta', si pensi a questo esempio: per qualche ragione il pacco spedito da un primo utente viene perso per strada e viene rispedito da un altro utente al medesimo cliente nelle medesime condizioni. In `MongoDB` non ho notato la necessita' di dover utilizzare operazioni relazionali, in quanto tale storage di dati non e' esattamente pensato per un approccio di tale tipo.

- View \
Le view come anticipato sono 2.
`LoginView` e `ProductView`. Entrambe implementano l'interfaccia `PanelSwitcher` perche' la vista del login effettua lo switch vero e proprio (usando `switchPanel`) e la vista del product riceve l'utente loggato in precedenza. Inoltre, la vista dei prodotti inizializza la visualizzazione della lista dei prodotti gia' inviati dall'utente corrente.

# Esecuzione dell'applicativo

### Requisiti per la configurazione Docker

Per esecuzione app su container:

- Utility xhost
  - Di solito è già installato ma disponibile nel pacchetto `x11-xserver-utils` oppure in `x11-utils`
- Paccketto `build-essentials` per l'utility `Make`
- Docker e Docker-compose
  - Se si vuole eseguire i test in locale, ricordarsi che la versione del test container utilizzata (1.X) è compatibile con un docker daemon < 28.

```
make build-and-run
```

Questo compilerà l’applicazione all’interno di un container Docker ed eseguirà il tutto in un ambiente sicuro.

Al termine:

```
make docker-stop
```

Tuttavia, a causa di una diversa configurazione grafica, possono verificarsi problemi; infatti, il pannello avviato può risultare "vuoto", senza grafica, quando si esegue l’applicazione nel container.

Avendo i container dei database `up and running`, si puo' avviare l’applicazione manualmente con:

```
make package && java -jar com.rosa.angelo.progetto.ast/target/ast-1.0.0-SNAPSHOT-jar-with-dependencies.jar --db=mariadb
```

Oppure con mongodb:

```
make package && java -jar com.rosa.angelo.progetto.ast/target/ast-1.0.0-SNAPSHOT-jar-with-dependencies.jar --db=mongodb
```

> [!WARNING]
> Hai bisogno della token phrase "validToken" per poter effettuare registrazioni.

È necessario avere `>= Java 17` per eseguirla.

In alternativa, e' possibile scaricare ed eseguire l’ultima release già precompilata su Github Releases:
<https://github.com/Virgula0/ProgettoAST/releases/>

### Esecuzione dei test

I test sono pronti per essere eseguiti da eclipse, tuttavia nel `Makefile` sono presenti i comandi per eseguire varie azioni comodamente da terminale


- Run Junit Tests (Coverage inclusa e generazione report)

```
make junit
```

- Run Junit + Integration tests (Coverage inclusa e generazione report)

```
make integration-test
```

- Run junit + pit mutation testing (Coverage inclusa e generazione report)

```
make run-pit
```

- Run Junit + Integration tests + PIT + E2E (Coverage inclusa e generazione report)

```
make test
```

# Difficolta' riscontrate

Prima di proseguire con la spiegazione di due importanti problematiche riscontrate vorrei sconsigliare ai prossimi l'utilizzo di Eclipse su ambienti grafici di tipo `window-tiling managers` come ad esempio `Hyperland` (basato su Wayland) oppure `i3` (basato su xserver) entrambi utilizzati da me. Ho riscontrato diversi bug tra cui: scomparsa improvvisa del cursore senza motivo, freeze della grafica che richiedono necessariamente un restart di Eclipse, content assist che presenta vari bug nelle suggestions quando lo si invoca, finestre a comparsa che non compaiono e altri vari bug.

La prima difficolta' principale e' stato un bug di `Jacoco` che ha impiegato da parte mia ore di debugging per capirne il problema.

Inizialmente pensavo fosse un bug di sonarqube sulla coverage. Infatti tutti i test passavano ma la coverage non era al 100%. Molto strano dato che il threshold e' settato al 100%. Pensando fosse un allucinazione da parte di sonarqube, ho inizialmente disattivato la coverage sui repository inerenti `MariaDB` sia per lo user che per il product: https://github.com/Virgula0/ProgettoAST/pull/18/files#diff-473abd12905d7a47aa6eac3dab88e3b341719553f1c7bf143fb849cd5d827ebc

Sonarqube infatti sottolineava un mancato branch coverage nel catch dell'eccezione quando un `RuleSet` viene usato.
Inizialmente ho chiesto informazioni sul forum di sonarqube: https://community.sonarsource.com/t/sonarqube-98-2-coverage-while-jacoco-gives-100/152027/3

Mi e' stato fatto pero' notare, che sonarqube si basa sulla coverage di jacoco. Dopo aver ricevuto questo suggerimento ho investigato piu' a fondo, fino ad arrivare a capire che era un bug del parser di jacoco. Infatti Jacoco dava problemi perche' non riusciva a capire che il return statement all'interno del `try-with-resource` chiudesse in automatico le risorse. Ho aperto una issue sul repo di Jacoco: https://github.com/jacoco/jacoco/issues/1993#issuecomment-3544012735 e mi e' stato riferito gentilmente di essere gia' a conoscenza dei problemi legati all'utilizzo del `try-with-resource` e che vi era gia' una issue aperta sul tema, da circa un anno. A questo punto non ho fatto altro che semplificare la vita a jacoco con il fix mostrato come esempio nella issue stessa, ma il bug nel parser ovviamente permane.

Un altro problema ugualmente subdolo e' legato al recente upgrade del daemon di docker alla versione 29.

Come dichiarato dalla documentazione della release stessa https://docs.docker.com/engine/release-notes/29/#2900 la versione 29 del daemon introduce numerosi breaking changes. Questo porta tutte le versioni di test containers disponibili attualmente a non riuscire a comunicare correttamente con il daemon.

I maintainers di `test-container` hanno aggiornato qualche giorno fa la versione `2.0.3` alla versione `2.0.4` che risolve il problema, ma hanno detto esplicitamente che non introdurrano un fix per le versioni precedenti `1.X`: https://github.com/testcontainers/testcontainers-java/issues/11212#issuecomment-3538510070

Questo solleva una problematica: la versione di testcontainers da me utilizzata nel progetto fin dall'inizio e' basata sull'`1.X`. Passare ad una major version superiore comporterebbe problemi di compatibilita' sulle annotations e toccherebbe cambiare le inizializzazioni dei test all'interno delle classi che usano attivamente test containers.

Ho preferito continuare ad usare la versione `1.X`, facendo notare pero', la presenza di questa problematica e si consiglia di aggiornare i riferimenti nel libro utilizzando la versione `2.0.4` di `test-containers` (o superiore) compatibile con docker <=29.