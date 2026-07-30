# Battery Tracker

App Android (Kotlin + Jetpack Compose, tema scuro futuristico) per monitorare in tempo reale
la batteria e l'utilizzo dello schermo.

## Funzionalità

- **Home**: indicatore batteria animato "a onda", stato corrente (in uso / in carica),
  data e ora esatta dell'ultimo distacco del caricabatterie ("Ultima carica"), ore di
  schermo attivo e schermo spento accumulate dall'ultima carica (in pausa durante la ricarica).
- **Storico**: elenco a tendina espandibile di tutte le sessioni di scarica passate, con
  durata, schermo attivo/spento e percentuale di batteria consumata.
- **Giornaliero**: consumo di batteria giorno per giorno (mezzanotte-mezzanotte). Se durante
  il giorno ricarichi il telefono, la giornata mostra automaticamente più segmenti separati
  (schermo attivo/spento e % consumata per ciascun segmento).
- **Cicli**: numero totale di volte in cui hai collegato il caricabatterie.

Tutti i dati sono salvati localmente in un database Room, non serve alcuna connessione internet
né account.

## Come generare l'APK (automatico, senza scrivere nulla)

Questo repository è già pronto per GitHub Actions:

1. Crea un nuovo repository su GitHub (vuoto).
2. Carica **tutto** il contenuto di questa cartella nel repository (branch `main`).
3. Vai nella tab **Actions** del repository: il workflow "Build & Release APK" partirà
   automaticamente ad ogni push.
4. Al termine (2-4 minuti), vai nella tab **Releases**: troverai l'APK pronto da scaricare
   e installare sul telefono (`BatteryTracker-runN.apk`).

Non serve installare Android Studio né Gradle sul tuo PC: tutto avviene sui server di GitHub.

## Come aprire il progetto in Android Studio (opzionale)

Se vuoi modificarlo in locale:

1. Apri Android Studio → **Open** → seleziona la cartella `BatteryTracker`.
2. Se richiesto, lascia che Android Studio rigeneri automaticamente il Gradle Wrapper
   (il progetto non lo include per restare più leggero: Android Studio lo crea da solo al primo sync).
3. Premi **Run** per installare l'app su un dispositivo/emulatore collegato.

## Permessi richiesti

- **Notifiche** (Android 13+): necessario per mostrare la notifica persistente del servizio
  di monitoraggio in background (obbligatoria su Android per poter tracciare schermo/batteria
  anche ad app chiusa).
- Il servizio si riavvia automaticamente al riavvio del telefono.

## Struttura del progetto

```
app/src/main/java/com/alessandro/batterytracker/
├── data/            Entità Room, DAO, database, repository e logica di calcolo
├── service/          Foreground service che intercetta eventi schermo/batteria
├── receiver/         BroadcastReceiver per il riavvio al boot
└── ui/                Compose UI (tema, schermate, componenti animati)
```
