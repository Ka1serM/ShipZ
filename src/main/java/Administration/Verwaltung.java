package Administration;

import Administration.Enums.EAction;
import Administration.Enums.ESchiffsTyp;
import Administration.Nachrichten.SpielerKonfiguration;
import Administration.Nachrichten.SpielfeldKonfiguration;
import GUI.*;
import Networking.Netzwerk;
import SLURPH.GameStateManager;
import SLURPH.HighscoreItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.stage.Stage;
import ArtificialIntelligence.*;

import java.util.*;

public class Verwaltung {
    private static final int POLLING_RATE = 100;
    private static final int MAX_ATTEMPTS = 3000;
    private boolean isProcessing;
    private final Gson gson;
    private final EventListener listener;
    private final Timer timer;
    private static Verwaltung instance;
    private final ShipzGui gui;
    private final Random random;
    private ESpielfeld currentESpielfeldEnum;
    private Spieler spielerLinks;
    private Spieler spielerRechts;
    private Spielfeld spielfeldLinks;
    private Spielfeld spielfeldRechts;
    private SpielfeldKonfiguration spielfeldKonfiguration;

    private Netzwerk netzwerk;
    private boolean istServer;
    private boolean istNetzwerkSpiel;
    //TODO irgendwie cleaner?
    private boolean istSpielBeendet;
    private GameStateManager slurph;

    private Verwaltung(Stage stage) {
        this.slurph = new GameStateManager();

        this.listener = new EventListener();
        this.gui = ShipzGui.getInstance(listener, stage);
        this.gui.debugModus(true);

        this.random = new Random();
        this.gson = new Gson();

        this.timer = new Timer();
        this.startPolling();
    }

    public static Verwaltung getInstance(Stage stage) {
        if (instance == null)
            instance = new Verwaltung(stage);
        return instance;
    }

    /**
     * Startet einen Timer, der in regelmäßigen Abständen Aktionen aus der Warteschlange überprüft
     * und verarbeitet, sofern nicht bereits eine Aktion verarbeitet wird.
     */
    private void startPolling() {
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Verwaltung.this.isProcessing)
                    return;
                Event nextAction = Verwaltung.this.listener.getNextAction();
                if (nextAction == null)
                    return; //queue ist leer

                Verwaltung.this.isProcessing = true; //flag auf processing
                Platform.runLater(() -> {
                    Verwaltung.this.processAction(nextAction);
                    Verwaltung.this.isProcessing = false; //flag auf idle
                });
            }
        }, 0, POLLING_RATE); //aufruf every 100ms
    }

    /**
     * Verarbeitet eine übergebene Aktion, indem die passende Methode je nach Aktionstyp aufgerufen wird.
     *
     * @param action Die auszuführende Aktion.
     */
    private void processAction(Event action) {
        EAction actionType = EAction.values()[action.getID()];
        switch (actionType) {
            case ANSICHT_GEWAEHLT -> this.ansichtGewaehlt();
            case SPIELZUG_GEMACHT -> this.spielzugGemacht(action.getData());
            case SCHLACHT_BEGINNEN -> this.schlachtBeginnen();
            case SCHIFF_ROTIERT -> this.schiffRoetiert();
            case SCHIFF_BEWEGT -> this.schiffBewegt();
            case SCHIFFE_AUTOMATISCH_PLATZIEREN -> this.schiffeAutomatischPlatzieren();

            case NETZWERK_TIMEOUT -> this.netzwerkTimeout();
            case NETZWERK_VERBUNDEN -> this.netzwerkVerbunden();
            case NETZWERKDATEN_CLIENT_ABRUFBBAR -> this.netzwerkdatenClientAbrufbar();
            case NETZWERK_HOST_DATEN_ABRUFBBAR -> this.netzwerkdatenHostAbrufbar();
            case NETZWERK_MODUS_ABRUFBAR -> this.netzwerkmodusAbrufbar();
            case NETZWERK_SPIEL_ABBRECHEN -> this.netzwerkAbbrechenKnopf();

            case NETZWERK_NACHRICHT_SPIEL_ABBRECHEN -> this.netzwerkNachrichtSpielrundeAbbrechen();
            case NETZWERK_NACHRICHT_SPIELER_KONFIGURATION -> this.netzwerkNachrichtSpielerKonfiguration(action.getData());
            case NETZWERK_NACHRICHT_SPIELFELD_KONFIGURATION -> this.netzwerkSpielfeldKonfiguration(action.getData());
            case NETZWERK_NACHRICHT_BEREIT -> this.netzwerkNachrichtBereit(action.getData());
            case NETZWERK_NACHRICHT_SPIELZUG_SERVER -> this.netzwerkNachrichtSpielzugServerGemacht(action.getData());
            case NETZWERK_NACHRICHT_SPIELZUG_CLIENT -> this.netzwerkNachrichtSpielzugClientGemacht(action.getData());
            case NETZWERK_NACHRICHT_SPIELZUG_CLIENT_RETURN_FROM_SERVER -> this.netzwerkNachrichtSpielzugClientReturnFromServer(action.getData());
            case NETZWERK_NACHRICHT_SPIEL_STARTEN -> this.netzwerkNachrichtSpielStarten();
            case NETZWERK_NACHRICHT_GEWONNEN -> this.netzwerkNachrichtGewonnen(action.getData());
            case NETZWERK_NACHRICHT_SCORES_AKTUALISIEREN -> this.netzwerkNachrichtScoresAktualisieren(action.getData());
            case NETZWERK_NACHRICHT_AI_SPIELZUG_ANFRAGE_CLIENT -> this.netzwerkNachrichtAiSpielzugAnfrageClient();
            case NETZWERK_NACHRICHT_AI_LETZTER_TREFFER_CLIENT -> this.netzwerkNachrichtAiLetzterTrefferClient(action.getData());
            case NETZWERK_NACHRICHT_AI_SCHIFF_ZERSTOERT_CLIENT -> this.netzwerkNachrichtAiSchiffZerstoertClient(action.getData());
            
            case MENUE_ZURUECK -> this.zurueck();
            case UNDO -> this.undo();
            case REDO -> this.redo();
            case SPIELRUNDE_SPEICHERN -> this.spielrundeSpeichern();
            case SPIELRUNDE_ABBRECHEN -> this.spielrundeAbbrechen();
            case BESTENLISTE_LEEREN -> this.bestenlisteLeeren();
            
            case SPIEL_LADEN -> this.spielLaden();
            case SPIEL_LOESCHEN -> this.spielLoeschen();
            case SPIELFELD_KONFIGURATION_ABRUFBAR -> this.spielfeldKonfigurationAbrufbar();
            case LOKALE_SPIELER_ABRUFBAR -> this.lokaleSpielerAbrufbar();
            case PROGRAMM_BEENDEN -> this.programmBeenden();
        }
    }

    private void netzwerkTimeout() {
        this.netzwerk.stop();
        this.istNetzwerkSpiel = false;
        this.gui.zeigeAnsicht(Ansicht.NETZWERK_ABBRUCH);
    }

    /**
     * Führt das Zurücknavigieren aus, je nach aktuellem Ansichtszustand der GUI.
     */
    private void zurueck() {
        switch (this.gui.getGewaelteAnsicht()) {
            case SPIELKONFIGURATION:
                this.gui.zeigeAnsicht(Ansicht.LOKALE_SPIELER_WAEHLEN);
                break;
            case NETZWERKSPIEL_ERSTELLEN:
            case NETZWERKSPIEL_VERBINDEN_ZU_HOST:
            case NETZWERKSPIEL_WARTE_AUF_CLIENT:
                this.netzwerkAbbrechen();
                this.gui.zeigeAnsicht(Ansicht.NETZWERKMODUS_WAEHLEN);
                break;
            case LOKALE_SPIELER_WAEHLEN:
            case BESTENLISTE:
            case EINSTELLUNGEN:
            case SPIEL_LADEN:
            case NETZWERKMODUS_WAEHLEN:
                this.gui.zeigeAnsicht(Ansicht.HAUPTMENUE);
                break;
        }
    }

    /**
     * Beginnt die Schlacht, entweder im Netzwerkspielmodus oder im lokalen Modus.
     */
    private void schlachtBeginnen() {
        if (this.istNetzwerkSpiel)
            this.netzwerkSchlachtBeginnen();
        else
            this.lokalSchlachtBeginnen();
    }

    /**
     * Initialisiert den Beginn der Schlacht im Netzwerkmodus und informiert den Gegner, wenn beide bereit sind.
     */
    public void netzwerkSchlachtBeginnen() {
        if (!this.spielerLinks.istBereit()) {
            //setze unsere Schiffe auf unveraenderbar und sperre den automatisch platzieren button
            this.gui.setzeAutomatischPlatzierenButtonAktiviertLinks(false);
            for (Schiff schiff : this.getCurrentSpielfeld().getSchiffe().values())
                this.gui.setzeSchiffszustand(this.currentESpielfeldEnum, schiff.getId(), ESchiffsZustand.UNGETROFFEN);

            //nur schiffe schicken, wenn wir client sind
            String schiffeJson = this.istServer ? "" : this.gson.toJson(this.getCurrentSpielfeld().getSchiffe());
            this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_BEREIT, schiffeJson);

            this.spielerLinks.setBereit(true);
        }
        
        if (this.spielerRechts.istBereit()) {
            this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_SPIEL_STARTEN, "");
            this.netzwerkNachrichtSpielStarten();
        }
    }
    
    /**
     * Wird aufgerufen, wenn das Netzwerk eine Nachricht zum Starten des Spiels empfängt, und auf dem server, wenn ein Netzwerkspiel beginnt.
     *
     */
    private void netzwerkNachrichtSpielStarten() {
        this.currentESpielfeldEnum = ESpielfeld.RECHTS;

        //reset score
        this.gui.aktualisiereScore(ESpielfeld.LINKS, 0);
        this.gui.aktualisiereScore(ESpielfeld.RECHTS, 0);
        this.istSpielBeendet = false;

        //nur menschlicher spieler kann klicken
        if (this.spielerLinks.getSpielertyp() == ESpielertyp.MENSCHLICHER_SPIELER)
            this.gui.spielfeldKlickEventsRegistieren(ESpielfeld.RECHTS);

        this.gui.zeigeAnsicht(Ansicht.SPIEL_SPIELEN);
        
        //undo und redo bei netzwerkspiel deaktiviert
        this.gui.setzeUndoButtonAktiviert(false);
        this.gui.setzeRedoButtonAktiviert(false);
        
        if(this.istServer)
            this.netzwerkServerNaechsterSpielzug();
    }

    /**
     * Führt den nächsten Spielzug des Servers im Netzwerkspielmodus aus.
     */
    private void netzwerkServerNaechsterSpielzug() { //wir sind server und machen einen spielzug
        this.gui.spielfeldUmrandungSetzen(ESpielfeld.RECHTS);
        this.spielerLinks.requestShot(); //nur ai
    }

    /**
     * Verarbeitet einen gemachten Spielzug, unabhängig davon, ob er von einem menschlichen oder KI-Spieler stammt.
     *
     * @param json JSON-Daten des Spielzugs.
     */
    private void spielzugGemacht(String json) { //SpielZug von lokalem spieler oder lokaler ki gemacht
        SpielZug spielZug = this.gson.fromJson(json, SpielZug.class);
        if (this.istNetzwerkSpiel)
            this.netzwerkSpielzugGemacht(spielZug);
        else
            this.lokalSpielzugGemacht(spielZug);
    }

    /**
     * Verarbeitet einen Spielzug, der von einem Server-Spieler im Netzwerkmodus gemacht wurde.
     *
     * @param spielZug Der gemachte Spielzug.
     */
    private void netzwerkSpielzugGemachtVomServer(SpielZug spielZug) { //lokaler schuss vom server
        //set in Verwaltung
        var schussTypReturn = this.spielfeldRechts.schuss(spielZug);
        spielZug.setSchussTyp(schussTypReturn);

        //set in GUI
        this.gui.setzeSchuss(ESpielfeld.RECHTS, spielZug.getX(), spielZug.getY(), schussTypReturn);

        //score aktualisieren
        this.netzwerkServerScoresAktualisieren(this.spielerLinks, ESpielfeld.LINKS, schussTypReturn);

        //ki bescheid sagen, falls hit
        if (schussTypReturn == Schusstyp.TREFFER || schussTypReturn == Schusstyp.TREFFER_VERSENKT)
            this.spielerLinks.setLastHit(spielZug);

        //schick zu client
        spielZug.setSchussTyp(schussTypReturn);
        String spielZugJson = this.gson.toJson(spielZug);
        this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_SPIELZUG_SERVER, spielZugJson);

        if (schussTypReturn == Schusstyp.TREFFER_VERSENKT) {
            //in ui schiffsliste auf versenkt
            var schiff = this.spielfeldRechts.getErstesSchiff(spielZug.getX(), spielZug.getY());
            this.gui.setzeSchiff(ESpielfeld.RECHTS, schiff.getId(), schiff.getX(), schiff.getY(), schiff.getSchiffsTyp(), schiff.getRotation(), ESchiffsZustand.VERSENKT);
            this.gui.setzeSchiffszustand(ESpielfeld.RECHTS, schiff.getId(), ESchiffsZustand.VERSENKT);
            //alle felder auf versenkt updaten
            for (int[] pos : schiff.getPositionen())
                this.gui.aendereSchusstyp(ESpielfeld.RECHTS, pos[0], pos[1], Schusstyp.TREFFER_VERSENKT);

            //ki bescheid sagen, falls destroyed
            this.spielerLinks.removeDestroyedShip(schiff.getSchiffsTyp());

            this.netzwerkServerUeberpruefeGewinn(this.spielerLinks, this.spielfeldRechts);
        }
    }

    /**
     * Verarbeitet einen Spielzug, der von einem Client-Spieler im Netzwerkmodus gemacht wurde.
     *
     * @param spielZug Der gemachte Spielzug.
     */
    private void netzwerkSpielzugGemachtVomClient(SpielZug spielZug) { //lokaler schuss vom client
        //set in Verwaltung
        this.spielfeldRechts.schuss(spielZug);
        //set in GUI
        this.gui.setzeSchuss(ESpielfeld.RECHTS, spielZug.getX(), spielZug.getY(), Schusstyp.KEIN_TREFFER); //wir wissen noch nicht, ob hit

        //nachricht mit spielzug position an den server schicken
        String spielZugJson = this.gson.toJson(spielZug);
        this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_SPIELZUG_CLIENT, spielZugJson);
    }

 
    private void netzwerkSpielzugGemacht(SpielZug spielZug) { //SpielZug von lokalem spieler oder lokaler ki gemacht, im Netzwerkmodus
        if (this.istServer)
            this.netzwerkSpielzugGemachtVomServer(spielZug);
        else //if netzwerk modus und wir sind client und haben geschossen
            this.netzwerkSpielzugGemachtVomClient(spielZug);

        //direkt nach schuss wird das ui gesperrt/ umrandung auf links wo kein binding ist
        this.gui.spielfeldUmrandungSetzen(ESpielfeld.LINKS);
    }

    private void netzwerkNachrichtSpielzugServerGemacht(String json) { //wir sind client und empfangen einen spielzug vom Server
        SpielZug spielZug = this.gson.fromJson(json, SpielZug.class);

        //set in Verwaltung
        this.spielfeldLinks.schuss(spielZug);
        //setze in GUI
        this.gui.setzeSchuss(ESpielfeld.LINKS, spielZug.getX(), spielZug.getY(), spielZug.getSchussTyp());

        if (spielZug.getSchussTyp() == Schusstyp.TREFFER_VERSENKT) {
            //in ui schiff auf versenkt
            var schiff = this.spielfeldLinks.getErstesSchiff(spielZug.getX(), spielZug.getY());
            this.gui.setzeSchiffszustand(ESpielfeld.LINKS, schiff.getId(), ESchiffsZustand.VERSENKT);
            //alle felder auf versenkt updaten
            for (int[] pos : schiff.getPositionen())
                this.gui.aendereSchusstyp(ESpielfeld.LINKS, pos[0], pos[1], Schusstyp.TREFFER_VERSENKT);
        }

        this.netzwerkClientNaechsterSpielzug();
    }

    /**
     * Führt den nächsten Spielzug des Clients im Netzwerkspielmodus aus.
     */
    private void netzwerkClientNaechsterSpielzug() { //wir sind client und machen einen spielzug
        this.gui.spielfeldUmrandungSetzen(ESpielfeld.RECHTS);

        //client fragt ki schuss vom server an, falls ki spieler
        if (this.spielerLinks.getSpielertyp() != ESpielertyp.MENSCHLICHER_SPIELER)
            this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_AI_SPIELZUG_ANFRAGE_CLIENT, "");
    }

    /**
     * Verarbeitet die Antwort des Servers auf einen gemachten Spielzug des Clients.
     *
     * @param json JSON-Daten des Spielzugs und ggf. des versenkten Schiffs.
     */
    private void netzwerkNachrichtSpielzugClientGemacht(String json) { //wir sind server und haben client position empfangen
        SpielZug spielZug = this.gson.fromJson(json, SpielZug.class);

        //set in Verwaltung
        var schussTypReturn = this.spielfeldLinks.schuss(spielZug);
        spielZug.setSchussTyp(schussTypReturn);

        //set in GUI
        this.gui.setzeSchuss(ESpielfeld.LINKS, spielZug.getX(), spielZug.getY(), schussTypReturn);

        //schicke zurueck an den client, ob hit oder nicht
        spielZug.setSchussTyp(schussTypReturn);
        String spielZugJson = this.gson.toJson(spielZug);

        //score aktualisieren
        this.netzwerkServerScoresAktualisieren(this.spielerRechts, ESpielfeld.RECHTS, schussTypReturn);

        //ki bescheid sagen, falls hit
        if (schussTypReturn == Schusstyp.TREFFER || schussTypReturn == Schusstyp.TREFFER_VERSENKT)
            this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_AI_LETZTER_TREFFER_CLIENT, spielZugJson); //client ki, die auf server laeuft, bescheid sagen

        if (schussTypReturn == Schusstyp.TREFFER_VERSENKT) {
            //in ui schiffsliste auf versenkt
            var schiff = this.spielfeldLinks.getErstesSchiff(spielZug.getX(), spielZug.getY());
            this.gui.setzeSchiffszustand(ESpielfeld.LINKS, schiff.getId(), ESchiffsZustand.VERSENKT);
            //alle felder auf versenkt updaten
            for (int[] pos : schiff.getPositionen())
                this.gui.aendereSchusstyp(ESpielfeld.LINKS, pos[0], pos[1], Schusstyp.TREFFER_VERSENKT);


            //client ki, die auf server laeuft, bescheid sagen
            String schiffsTypJson = this.gson.toJson(schiff.getSchiffsTyp());
            this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_AI_SCHIFF_ZERSTOERT_CLIENT, schiffsTypJson);

            //schiff an json string anhaengen
            spielZugJson += this.gson.toJson(schiff);
        }

        //schicke zurueck an den client, ob hit oder nicht
        this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_SPIELZUG_CLIENT_RETURN_FROM_SERVER, spielZugJson);

        if (!this.istSpielBeendet && !this.netzwerkServerUeberpruefeGewinn(this.spielerRechts, this.spielfeldLinks)) //falls nicht gewonnen, naechster zug
            this.netzwerkServerNaechsterSpielzug(); //jetzt ist der server dran
    }

    
    private void netzwerkNachrichtSpielzugClientReturnFromServer(String json) { //wir sind client und kriegen vom server antwort, ob hit oder nicht
        String[] spielzugSchiff = json.split("(?=\\{)|(?<=\\})"); //regex um ein string von zwei json objekten "{..}{..}" in ein string array zu konvertieren
        SpielZug spielZug = this.gson.fromJson(spielzugSchiff[0], SpielZug.class);

        //gui aktualisieren mit info vom Server
        this.gui.setzeSchuss(ESpielfeld.RECHTS, spielZug.getX(), spielZug.getY(), spielZug.getSchussTyp());

        if (spielZug.getSchussTyp() == Schusstyp.TREFFER_VERSENKT) {
            Schiff schiff = this.gson.fromJson(spielzugSchiff[1], Schiff.class); //json hat spielzug in [0] und schiff in [1], falls versenkt
            //in ui schiffsliste auf versenkt
            this.gui.setzeSchiff(ESpielfeld.RECHTS, schiff.getId(), schiff.getX(), schiff.getY(), schiff.getSchiffsTyp(), schiff.getRotation(), ESchiffsZustand.VERSENKT);
            this.gui.setzeSchiffszustand(ESpielfeld.RECHTS, schiff.getId(), ESchiffsZustand.VERSENKT);
            //alle felder auf versenkt updaten
            for (int[] pos : schiff.getPositionen())
                this.gui.aendereSchusstyp(ESpielfeld.RECHTS, pos[0], pos[1], Schusstyp.TREFFER_VERSENKT);
        }
    }

    /**
     * Überprüft, ob ein Spieler das Spiel gewonnen hat, basierend auf den verbleibenden Schiffen.
     *
     * @param spieler Der Spieler, dessen Sieg überprüft wird.
     * @param spielfeld Das Spielfeld des Spielers.
     * @return true, wenn der Spieler gewonnen hat, sonst false.
     */
    private boolean netzwerkServerUeberpruefeGewinn(Spieler spieler, Spielfeld spielfeld) {
        int gesunken = 0;
        for (Schiff schiff : spielfeld.getSchiffe().values())
            if (schiff.getSchiffsZustand() == ESchiffsZustand.VERSENKT)
                gesunken++;
        
        if (gesunken != spielfeld.getSchiffe().size())
            return false;

        String name = spieler.getName();
        int score = spieler.getScore();
        this.highscoreHandeling(name, score);

        //client bescheid sagen, dass gewonnen wurde
        String highscoreItemJson = this.gson.toJson(new HighscoreItem(name, score));
        this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_GEWONNEN, highscoreItemJson);
        this.netzwerk.stop();

        this.istNetzwerkSpiel = false;
        this.istSpielBeendet = true;

        this.gui.zeigeAnsicht(Ansicht.SPIELRUNDE_BEENDET);

        this.spielerLinks.setBereit(false);
        this.spielerRechts.setBereit(false);

        return true;
    }

    /**
     * Verarbeitet eine Netzwerknachricht, die signalisiert, dass das Spiel gewonnen wurde.
     *
     * @param json JSON-Daten des Highscore-Eintrags des Gewinners.
     */
    private void netzwerkNachrichtGewonnen(String json) {
        this.netzwerk.stop();

        HighscoreItem highscoreItem = this.gson.fromJson(json, HighscoreItem.class);

        this.gui.setGewinner(highscoreItem.getName(), highscoreItem.getScore());
        this.slurph.saveHighscore(highscoreItem.getName(), highscoreItem.getScore());
        this.gui.zeigeAnsicht(Ansicht.SPIELRUNDE_BEENDET);
        this.istNetzwerkSpiel = false;
        this.istSpielBeendet = true;
        this.spielerLinks.setBereit(false);
        this.spielerRechts.setBereit(false);
    }
    
    /**
     * Aktualisiert den lokalen Score des Spielers und zeigt den neuen Score in der GUI an.
     *
     * @param currentSpieler Der Spieler, dessen Score aktualisiert wird.
     * @param currentSpielfeld Das Spielfeld, auf dem der Spieler spielt.
     * @param schussTyp Der Typ des Schusses.
     */
    private void lokalScoreAktualisieren(Spieler currentSpieler, ESpielfeld currentSpielfeld, Schusstyp schussTyp) {
        //TODO EIGENTLICH VON SLURPH
        int neuerScore = currentSpieler.getScore();
        switch (schussTyp) {
            case TREFFER_VERSENKT -> neuerScore += 5;
            case TREFFER -> neuerScore++;
        }
        currentSpieler.setScore(neuerScore);
        this.gui.aktualisiereScore(currentSpielfeld, neuerScore);
    }

    /**
     * Aktualisiert die Scores sowohl lokal als auch im Netzwerk und sendet die
     * aktualisierten Scores an den Client.
     *
     * @param currentSpieler Der Spieler, dessen Score aktualisiert wird.
     * @param currentSpielfeld Das Spielfeld, auf dem der Spieler spielt.
     * @param schussTyp Der Typ des Schusses.
     */
    private void netzwerkServerScoresAktualisieren(Spieler currentSpieler, ESpielfeld currentSpielfeld, Schusstyp schussTyp) {
        this.lokalScoreAktualisieren(currentSpieler, currentSpielfeld, schussTyp);

        //client bescheid sagen, dass scores aktualisiert sind
        int[] scores = new int[]{this.spielerLinks.getScore(), this.spielerRechts.getScore()};
        String scoreJson = this.gson.toJson(scores);
        this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_SCORES_AKTUALISIEREN, scoreJson);
    }

    /**
     * Verarbeitet eine Netzwerk-Nachricht, die aktualisierte Scores enthält.
     *
     * @param json Die JSON-Darstellung der Scores.
     */
    private void netzwerkNachrichtScoresAktualisieren(String json) {
        int[] scores = this.gson.fromJson(json, new TypeToken<int[]>() {}.getType()); //[0] links, [1] rechts

        this.spielerLinks.setScore(scores[1]);
        this.gui.aktualisiereScore(ESpielfeld.LINKS, scores[1]);
        this.spielerRechts.setScore(scores[0]);
        this.gui.aktualisiereScore(ESpielfeld.RECHTS, scores[0]);
    }

    /**
     * Verarbeitet einen Spielzug, indem der Schuss auf dem aktuellen Spielfeld überprüft und in der GUI angezeigt wird.
     * Aktualisiert den Score des aktuellen Spielers, prüft, ob ein Schiff versenkt wurde, und überprüft, ob jemand gewonnen hat.
     *
     * @param spielZug Der aktuelle Spielzug, der verarbeitet werden soll.
     */
    private void lokalSpielzugGemacht(SpielZug spielZug) {
        //jeweils anderer Spieler hat den Schuss gemacht
        
        //set in Verwaltung
        var schussTypReturn = this.getCurrentSpielfeld().schuss(spielZug);
        spielZug.setSchussTyp(schussTypReturn);
        
        //set in GUI
        this.gui.setzeSchuss(this.currentESpielfeldEnum, spielZug.getX(), spielZug.getY(), schussTypReturn);

        //score aktualisieren & spieler bescheid sagen, falls hit
        if (schussTypReturn == Schusstyp.TREFFER || schussTypReturn == Schusstyp.TREFFER_VERSENKT)
            if (this.getCurrentPlayerOpposite().getSpielertyp() != ESpielertyp.MENSCHLICHER_SPIELER && this.getCurrentPlayerOpposite() instanceof AiSpieler aiSpieler)
                aiSpieler.setLastHit(spielZug);

        if (schussTypReturn == Schusstyp.TREFFER_VERSENKT) {
            //in ui schiffsliste auf versenkt
            var schiff = this.getCurrentSpielfeld().getErstesSchiff(spielZug.getX(), spielZug.getY());
            this.gui.setzeSchiff(this.currentESpielfeldEnum, schiff.getId(), schiff.getX(), schiff.getY(), schiff.getSchiffsTyp(), schiff.getRotation(), ESchiffsZustand.VERSENKT);
            this.gui.setzeSchiffszustand(this.currentESpielfeldEnum, schiff.getId(), ESchiffsZustand.VERSENKT);
            //alle felder auf versenkt updaten
            for (int[] pos : schiff.getPositionen())
                this.gui.aendereSchusstyp(this.currentESpielfeldEnum, pos[0], pos[1], Schusstyp.TREFFER_VERSENKT);

            if (this.getCurrentPlayerOpposite().getSpielertyp() != ESpielertyp.MENSCHLICHER_SPIELER && this.getCurrentPlayerOpposite() instanceof AiSpieler aiSpieler)
                aiSpieler.removeDestroyedShip(schiff.getSchiffsTyp());
        }

        ESpielfeld spielfeld;
        if(this.currentESpielfeldEnum == ESpielfeld.LINKS)
            spielfeld = ESpielfeld.RECHTS;
        else
            spielfeld = ESpielfeld.LINKS;
        
        //score aktualisieren
        this.lokalScoreAktualisieren(this.getCurrentPlayerOpposite(), spielfeld, schussTypReturn);
        
        if (!istSpielBeendet && !this.ueberpruefeGewinn())
            this.naechsterSpielzug();
    }

    /**
     * Überprüft, ob alle Schiffe auf dem aktuellen Spielfeld versenkt sind. Wenn ja, wird der Gewinner ermittelt, der Highscore aktualisiert und die entsprechende Ansicht angezeigt.
     */
    private boolean ueberpruefeGewinn() {
        int gesunken = 0;
        for (Schiff schiff : getCurrentSpielfeld().getSchiffe().values())
            if (schiff.getSchiffsZustand() == ESchiffsZustand.VERSENKT)
                gesunken++;
        
        if (gesunken != this.getCurrentSpielfeld().getSchiffe().size())
            return false;
        
        //gewinner und highscore
        String name = this.getCurrentPlayerOpposite().getName();
        int score = this.getCurrentPlayerOpposite().getScore();
        this.highscoreHandeling(name, score);

        this.gui.zeigeAnsicht(Ansicht.SPIELRUNDE_BEENDET);
        
        return true;
    }

    /**
     * Verarbeitet das Highscore-Handling, indem der Gewinner in der GUI angezeigt und der Highscore in der Datenbank gespeichert wird.
     *
     * @param name Der Name des Gewinners.
     * @param score Der Score des Gewinners.
     */
    private void highscoreHandeling(String name, int score) {
        //in gui speichern
        this.gui.setGewinner(name, score);
        //in slurph speichern
        this.slurph.saveHighscore(name, score);
    }

    /**
     * Wechselt zum nächsten Spielzug, indem das aktuelle Spielfeld gewechselt wird und der nächste Spieler aufgefordert wird, einen Schuss abzugeben.
     */
    private void naechsterSpielzug() {
        this.currentESpielfeldEnum = currentESpielfeldEnum == ESpielfeld.RECHTS ? ESpielfeld.LINKS : ESpielfeld.RECHTS;
        this.gui.spielfeldUmrandungSetzen(this.currentESpielfeldEnum);
        //links ist markiert, aber rechter schiesst auf links
        if(this.currentESpielfeldEnum == ESpielfeld.LINKS)
            this.spielerRechts.requestShot();
        else 
            this.spielerLinks.requestShot();
    }

    /**
     * Verarbeitet eine Netzwerk-Nachricht, um einen AI-Spielzug anzufordern, wenn der Server die Anfrage vom Client erhält.
     */
    private void netzwerkNachrichtAiSpielzugAnfrageClient() { //wir sind server und der client fragt eine ai schuss position
        SpielZug spielZug = this.spielerRechts.shoot();
        String spielZugJson = this.gson.toJson(spielZug);
        this.netzwerk.sendActionEvent(EAction.SPIELZUG_GEMACHT, spielZugJson);
    }

    /**
     * Verarbeitet eine Netzwerk-Nachricht, um den letzten Treffer der AI vom Client zu empfangen.
     *
     * @param json Die JSON-Daten, die den letzten Treffer der AI enthalten.
     */
    private void netzwerkNachrichtAiLetzterTrefferClient(String json) { //wir sind server und der client sagt seiner ki bescheid
        SpielZug spielZug = this.gson.fromJson(json, SpielZug.class);
        this.spielerRechts.setLastHit(spielZug);
    }

    /**
     * Verarbeitet eine Netzwerk-Nachricht, um zu erfahren, dass ein Schiff von der AI auf dem Client zerstört wurde.
     *
     * @param json Die JSON-Daten, die den Typ des zerstörten Schiffs enthalten.
     */
    private void netzwerkNachrichtAiSchiffZerstoertClient(String json) { //wir sind server und der client sagt seiner ki bescheid
        ESchiffsTyp schiffsTyp = this.gson.fromJson(json, ESchiffsTyp.class);
        this.spielerRechts.removeDestroyedShip(schiffsTyp);
    }

    /**
     * Verarbeitet eine Netzwerk-Nachricht, um die Spieler-Konfiguration vom Client zu empfangen.
     *
     * @param json Die JSON-Daten, die die Spieler-Konfiguration enthalten.
     */
    private void netzwerkNachrichtSpielerKonfiguration(String json) {
        SpielerKonfiguration konfiguration = this.gson.fromJson(json, SpielerKonfiguration.class);
        if (konfiguration.getSpielertyp() == ESpielertyp.MENSCHLICHER_SPIELER)
            this.spielerRechts = new Spieler(konfiguration.getName(), konfiguration.getSpielertyp());
        else
            this.spielerRechts = new AiSpieler(this.listener, konfiguration.getName(), konfiguration.getSpielertyp());
    }

    /**
     * Verarbeitet die Spielfeld-Konfiguration vom Client und aktualisiert die GUI entsprechend.
     *
     * @param json Die JSON-Daten, die die Spielfeld-Konfiguration enthalten.
     */
    private void netzwerkSpielfeldKonfiguration(String json) {
        SpielfeldKonfiguration konfiguration = this.gson.fromJson(json, SpielfeldKonfiguration.class);
        this.setzeSpielfeldKonfiguration(konfiguration.getSpielfeldBreite(), konfiguration.getSpielfeldHoehe(), konfiguration.isSchiffsberuehrungErlaubt(), konfiguration.getSchiffsZusammensetzung());

        this.currentESpielfeldEnum = ESpielfeld.LINKS;
        this.gui.zeigeAnsicht(Ansicht.SCHIFFE_ANORDNEN_LINKS);
        this.schiffeAutomatischPlatzieren();
    }

    /**
     * Verarbeitet eine Netzwerk-Nachricht, um den aktuellen Status der Bereitschaft des Gegners zu aktualisieren.
     */
    private void netzwerkNachrichtBereit(String json) {
        if(this.spielerRechts.istBereit())
            return;

        this.spielerRechts.setBereit(true);

        if(this.istServer) { //wenn wir Server sind, nehmen wir die Schiffe vom gegner an
            HashMap<Integer, Schiff> schiffe = this.gson.fromJson(json, new TypeToken<HashMap<Integer, Schiff>>() {}.getType());
            for (var schiff : schiffe.values())
                this.spielfeldRechts.setzeSchiff(schiff);
        }

    }


    /**
     * Verarbeitet eine Netzwerk-Verbindung, indem die Spieler-Konfiguration an den Gegner gesendet wird und die entsprechende Ansicht in der GUI angezeigt wird.
     */
    private void netzwerkVerbunden() {
        this.istNetzwerkSpiel = true;
        //schicke unsere spieler konfig an den gegner
        String spielerKonfigurationJson = this.gson.toJson(new SpielerKonfiguration(this.spielerLinks.getName(), this.spielerLinks.getSpielertyp()));
        this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_SPIELER_KONFIGURATION, spielerKonfigurationJson);

        if (this.istServer)
            this.gui.zeigeAnsicht(Ansicht.SPIELKONFIGURATION);
        else
            this.gui.zeigeAnsicht(Ansicht.WARTE_AUF_HOST_SPIELKONFIGURATION);
    }

    /**
     * Initialisiert die Netzwerkverbindung als Client und startet den Netzwerk-Thread.
     */
    private void netzwerkdatenClientAbrufbar() { //rechts
        this.spielerLinksAbrufbar(); //eigenen Spieler erstellen
        this.netzwerk = new Netzwerk(this.listener, this.gui.getIp(), this.gui.getPort(), 4000, 12000);
        new Thread(this.netzwerk).start();
    }

    /**
     * Initialisiert die Netzwerkverbindung als Host und startet den Netzwerk-Thread. Zeigt die entsprechende Ansicht in der GUI an.
     */
    private void netzwerkdatenHostAbrufbar() { //links
        this.spielerLinksAbrufbar(); //eigenen Spieler erstellen
        this.netzwerk = new Netzwerk(this.listener, this.gui.getPort(), 4000, 12000);
        new Thread(this.netzwerk).start();
        this.gui.zeigeAnsicht(Ansicht.NETZWERKSPIEL_WARTE_AUF_CLIENT);
    }

    /**
     * Verarbeitet die Auswahl des Netzwerkmodus und zeigt die entsprechende Ansicht in der GUI an.
     */
    private void netzwerkmodusAbrufbar() {
        this.istServer = this.gui.getIstHost();
        if (this.istServer)
            this.gui.zeigeAnsicht(Ansicht.NETZWERKSPIEL_ERSTELLEN);
        else
            this.gui.zeigeAnsicht(Ansicht.NETZWERKSPIEL_VERBINDEN_ZU_HOST);
    }

    /**
     * Verarbeitet die Auswahl des Netzwerkmodus und zeigt die entsprechende Ansicht in der GUI an.
     */
    private void spielrundeAbbrechen() {
        this.istSpielBeendet = true;
        if (this.istNetzwerkSpiel)
            this.netzwerkAbbrechenKnopf();
        else
            this.gui.zeigeAnsicht(Ansicht.HAUPTMENUE);
    }


    /**
     * Bricht ein Netzwerkspiel ab, indem eine Nachricht an den Gegner gesendet und die Netzwerkverbindung geschlossen wird. Zeigt die entsprechende Ansicht in der GUI an.
     */
    private void netzwerkAbbrechenKnopf() { //knopf im UI gedrueckt
        this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_SPIEL_ABBRECHEN, "");
        this.netzwerk.stop();
        this.istNetzwerkSpiel = false;
        this.gui.zeigeAnsicht(Ansicht.NETZWERK_ABBRUCH);
    }

    /**
     * Bricht die Netzwerkverbindung ab, indem eine Nachricht an den Gegner gesendet und die Verbindung geschlossen wird.
     */
    private void netzwerkAbbrechen() {
        if(!this.istNetzwerkSpiel)
            return;
        this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_SPIEL_ABBRECHEN, "");
        this.netzwerk.stop();
        this.istNetzwerkSpiel = false;
    }

    /**
     * Verarbeitet eine Netzwerk-Nachricht, um das Abbrechen der Spielrunde vom Gegner zu empfangen. Schließt die Netzwerkverbindung und zeigt die entsprechende Ansicht an.
     */
    private void netzwerkNachrichtSpielrundeAbbrechen() {
        this.netzwerk.stop();
        this.istNetzwerkSpiel = false;
        this.gui.zeigeAnsicht(Ansicht.NETZWERK_ABBRUCH);
    }

    /**
     * Ruft die Spielfeld-Konfiguration ab, je nachdem, ob es sich um ein Netzwerkspiel oder ein lokales Spiel handelt.
     */
    public void spielfeldKonfigurationAbrufbar() {
        if (this.istNetzwerkSpiel)
            this.netzwerkSpielfeldKonfigurationAbrufbar();
        else
            this.lokalSpielfeldKonfigurationAbrufbar();
    }

    /**
     * Verarbeitet die Spielfeld-Konfiguration als Server, wenn die Konfiguration abgeschlossen ist. Setzt die Spielfeld-Konfiguration, sendet die Konfiguration an den Gegner und zeigt die entsprechende Ansicht in der GUI an.
     */
    private void netzwerkSpielfeldKonfigurationAbrufbar() { //wir sind server und unsere spielkonfiguration ist fertig eingestellt
        this.setzeSpielfeldKonfiguration(this.gui.getSpielfeldbreite(), this.gui.getSpielfeldhoehe(), this.gui.getSchiffsberuehrungErlaubt(), this.gui.getSchiffszusammensetzung());

        String spielKonfigurationJson = this.gson.toJson(this.spielfeldKonfiguration);
        this.netzwerk.sendActionEvent(EAction.NETZWERK_NACHRICHT_SPIELFELD_KONFIGURATION, spielKonfigurationJson);

        this.spielerRechts.setKonfiguration(this.spielfeldKonfiguration);
        this.spielerLinks.setKonfiguration(this.spielfeldKonfiguration);

        this.currentESpielfeldEnum = ESpielfeld.LINKS;
        this.gui.zeigeAnsicht(Ansicht.SCHIFFE_ANORDNEN_LINKS);
        this.schiffeAutomatischPlatzieren();
    }

    /**
     * Verarbeitet die Spielfeld-Konfiguration als Client, wenn die Konfiguration abgeschlossen ist. Setzt die Spielfeld-Konfiguration und zeigt die entsprechende Ansicht in der GUI an.
     */
    private void lokalSpielfeldKonfigurationAbrufbar() {
        this.setzeSpielfeldKonfiguration(this.gui.getSpielfeldbreite(), this.gui.getSpielfeldhoehe(), this.gui.getSchiffsberuehrungErlaubt(), this.gui.getSchiffszusammensetzung());

        this.spielerRechts.setKonfiguration(this.spielfeldKonfiguration);
        this.spielerLinks.setKonfiguration(this.spielfeldKonfiguration);

        this.currentESpielfeldEnum = ESpielfeld.LINKS;
        this.gui.zeigeAnsicht(Ansicht.SCHIFFE_ANORDNEN_LINKS);
        this.schiffeAutomatischPlatzieren();
    }

    /**
     * Setzt die Spielfeld-Konfiguration und aktualisiert die GUI entsprechend.
     *
     * @param spielfeldBreite Die Breite des Spielfelds.
     * @param spielfeldHoehe Die Höhe des Spielfelds.
     * @param schiffsberuehrungErlaubt Gibt an, ob Schiffberührungen erlaubt sind.
     * @param schiffsZusammensetzung Die Zusammensetzung der Schiffe auf dem Spielfeld.
     */
    private void setzeSpielfeldKonfiguration(int spielfeldBreite, int spielfeldHoehe, boolean schiffsberuehrungErlaubt, HashMap<ESchiffsTyp, Integer> schiffsZusammensetzung) {
        this.spielfeldKonfiguration = new SpielfeldKonfiguration(spielfeldBreite, spielfeldHoehe, schiffsberuehrungErlaubt, schiffsZusammensetzung);
        this.spielfeldLinks = new Spielfeld(spielfeldBreite, spielfeldHoehe);
        this.spielfeldRechts = new Spielfeld(spielfeldBreite, spielfeldHoehe);

        this.gui.setzeSchlachtBeginnenButtonAktiviertLinks(false);
        this.gui.setzeSchlachtBeginnenButtonAktiviertLinks(false);
        this.gui.spielrundeEinrichten(spielfeldBreite, spielfeldHoehe, schiffsZusammensetzung, this.spielerLinks.getName(), this.spielerRechts.getName(), schiffsberuehrungErlaubt);
    }

    /**
     * Beginnt eine neue Schlacht, indem der Score zurückgesetzt und die GUI entsprechend aktualisiert wird.
     * Wenn das aktuelle Spielfeld das rechte Spielfeld ist, wechselt es zum linken Spielfeld, ansonsten wird das rechte Spielfeld für den zweiten Spieler angezeigt.
     */
    public void lokalSchlachtBeginnen() {
        if (this.currentESpielfeldEnum == ESpielfeld.RECHTS) //starte Spiels
        {
            //reset score
            this.gui.aktualisiereScore(ESpielfeld.LINKS, 0);
            this.gui.aktualisiereScore(ESpielfeld.RECHTS, 0);
            this.istSpielBeendet = false;

            this.gui.entferneAlleSchiffe(ESpielfeld.LINKS);
            this.gui.entferneAlleSchiffe(ESpielfeld.RECHTS);
            if (this.spielerLinks.getSpielertyp() == ESpielertyp.MENSCHLICHER_SPIELER)
                this.gui.spielfeldKlickEventsRegistieren(ESpielfeld.RECHTS);
            if (this.spielerRechts.getSpielertyp() == ESpielertyp.MENSCHLICHER_SPIELER)
                this.gui.spielfeldKlickEventsRegistieren(ESpielfeld.LINKS);
            
            this.currentESpielfeldEnum = ESpielfeld.LINKS;

            this.gui.zeigeAnsicht(Ansicht.SPIEL_SPIELEN);

            this.naechsterSpielzug();
        } else { ///linkes Feld, zweiter Spieler muss noch Platzieren
            this.currentESpielfeldEnum = ESpielfeld.RECHTS;
            this.gui.zeigeAnsicht(Ansicht.SCHIFFE_ANORDNEN_RECHTS);
            this.schiffeAutomatischPlatzieren();
        }
    }

    /**
     * Verarbeitet die Konfiguration der Spieler, indem beide Spieler erstellt und die GUI entsprechend aktualisiert wird.
     */
    private void lokaleSpielerAbrufbar() {
        this.spielerLinksAbrufbar();
        this.spielerRechtsAbrufbar();

        this.gui.zeigeAnsicht(Ansicht.SPIELKONFIGURATION);
    }

    /**
     * Erstellt den linken Spieler basierend auf der Konfiguration aus der GUI.
     */
    private void spielerLinksAbrufbar() {
        if (this.gui.getSpielertypLinks() == ESpielertyp.MENSCHLICHER_SPIELER)
            this.spielerLinks = new Spieler(this.gui.getSpielernameLinks(), this.gui.getSpielertypLinks());
        else
            this.spielerLinks = new AiSpieler(this.listener, this.gui.getSpielernameLinks(), this.gui.getSpielertypLinks());
    }

    /**
     * Erstellt den rechten Spieler basierend auf der Konfiguration aus der GUI.
     */
    private void spielerRechtsAbrufbar() {
        if (this.gui.getSpielertypRechts() == ESpielertyp.MENSCHLICHER_SPIELER)
            this.spielerRechts = new Spieler(this.gui.getSpielernameRechts(), this.gui.getSpielertypRechts());
        else
            this.spielerRechts = new AiSpieler(this.listener, this.gui.getSpielernameRechts(), this.gui.getSpielertypRechts());
    }

    /**
     * Gibt den aktuellen Spieler basierend auf dem aktuellen Spielfeld zurück.
     *
     * @return Der aktuelle Spieler.
     */
    private Spieler getCurrentPlayer() {
        return this.currentESpielfeldEnum == ESpielfeld.LINKS ? this.spielerLinks : this.spielerRechts;
    }

    private Spieler getCurrentPlayerOpposite() {
        return this.currentESpielfeldEnum == ESpielfeld.LINKS ? this.spielerRechts : this.spielerLinks;
    }

    /**
     * Gibt das aktuelle Spielfeld basierend auf dem aktuellen Spielfeld zurück.
     *
     * @return Das aktuelle Spielfeld.
     */
    private Spielfeld getCurrentSpielfeld() {
        return this.currentESpielfeldEnum == ESpielfeld.LINKS ? this.spielfeldLinks : this.spielfeldRechts;
    }

    private Spielfeld getCurrentSpielfeldOpposite() {
        return this.currentESpielfeldEnum == ESpielfeld.LINKS ? this.spielfeldRechts : this.spielfeldLinks;
    }

    private void bestenlisteLeeren() {
        this.slurph.bestenListeLeeren();
    }

    /**
     * Speichert den aktuellen Stand der Spielrunde.
     */
    private void spielrundeSpeichern() {
        //TODO SLURPH
    }

    /**
     * Wiederholt den letzten rückgängig gemachten Zug.
     */
    private void redo() {
        //TODO SLUPRH
        /*
        //redoMove je nach current player hier
        GameMove gameMove = this.slurph.redoMove();
        SpielZug spielZug = gameMove.getMove();
        //aktualisieren in verwaltung
        if(currentESpielfeldEnum == ESpielfeld.LINKS)
            this.spielfeldLinks.schuss(spielZug);
        else
            this.spielfeldRechts.schuss(spielZug);
        //aktualisieren in gui
        this.gui.setzeSchuss(this.currentESpielfeldEnum, spielZug.getX(), spielZug.getY(), spielZug.getSchussTyp());
         */
    }

    /**
     * Macht den letzten Zug rückgängig.
     */
    private void undo() {
        //TODO SLUPRH
    }

    /**
     * Lädt ein gespeichertes Spiel.
     */
    private void spielLaden() {
        //TODO SLURPH
    }

    /**
     * Löscht ein gespeichertes Spiel.
     */
    private void spielLoeschen() {
        //TODO SLURPH
    }

    /**
     * Rotiert ein Schiff, indem die Rotation in der Verwaltung und in der GUI aktualisiert wird.
     */
    private void schiffRoetiert() {
        int schiffId = this.gui.getSchiffsId();
        int rotation = this.gui.getRotation();

        Schiff originalesSchiff = this.getCurrentSpielfeld().getSchiff(schiffId);

        //rotation ist gleich
        if (originalesSchiff.getRotation() == rotation)
            return;

        //verschiebe schiff
        this.getCurrentSpielfeld().verschiebeSchiff(originalesSchiff, originalesSchiff.getX(), originalesSchiff.getY(), rotation);

        //ueberpruefe alle schiffe auf gueltigkeit
        this.pruefeSchiffeGueltigkeit();
    }

    /**
     * Bewegt ein Schiff, indem die Position in der Verwaltung und in der GUI aktualisiert wird.
     */
    private void schiffBewegt() {
        int schiffId = this.gui.getSchiffsId();
        int x = this.gui.getX();
        int y = this.gui.getY();

        Schiff originalesSchiff = this.getCurrentSpielfeld().getSchiff(schiffId);

        //position ist gleich
        if (originalesSchiff.getX() == x && originalesSchiff.getY() == y)
            return;

        //verschiebe schiff
        this.getCurrentSpielfeld().verschiebeSchiff(originalesSchiff, x, y, originalesSchiff.getRotation());

        //ueberpruefe alle schiffe auf gueltigkeit
        this.pruefeSchiffeGueltigkeit();
    }

    /**
     * Überprüft die Gültigkeit aller Schiffe auf dem aktuellen Spielfeld und aktualisiert die GUI entsprechend.
     */
    private void pruefeSchiffeGueltigkeit() {
        boolean einSchiffUengueltig = false;
        for (Schiff schiff : this.getCurrentSpielfeld().getSchiffe().values())
            if (this.getCurrentSpielfeld().istSchiffGueltig(schiff, this.spielfeldKonfiguration.isSchiffsberuehrungErlaubt()))
                this.gui.setzeSchiffszustand(currentESpielfeldEnum, schiff.getId(), ESchiffsZustand.VERZIEHBAR);
            else {
                this.gui.setzeSchiffszustand(currentESpielfeldEnum, schiff.getId(), ESchiffsZustand.VERZIEHBAR_FALSCH_POSITIONIERT);
                einSchiffUengueltig = true;
            }

        //aktiviere/deaktiviere Button falls ein schiff uengueltig ist
        if (this.currentESpielfeldEnum == ESpielfeld.LINKS)
            this.gui.setzeSchlachtBeginnenButtonAktiviertLinks(!einSchiffUengueltig);
        else
            this.gui.setzeSchlachtBeginnenButtonAktiviertRechts(!einSchiffUengueltig);
    }

    /**
     * Beendet das Programm und stoppt den Timer.
     */
    private void programmBeenden() {
        this.timer.cancel();
        Platform.exit();
    }

    /**
     * Wählt die Ansicht basierend auf der vom Benutzer ausgewählten Ansicht und zeigt diese in der GUI an.
     */
    private void ansichtGewaehlt() {
        //TODO: hack weil GUI die Bestenliste und SpielLaden direkt aufruft und beim programmstart die ansichten noch nicht geladen sind
        switch(this.gui.getGewaelteAnsicht()) {
            case BESTENLISTE -> this.initializeHighscoreListe();
            case SPIEL_LADEN -> this.initializeSpielLaden();
        }
        this.gui.zeigeAnsicht(gui.getGewaelteAnsicht());
    }

    /**
     * Initialisiert die Bestenliste mit den Highscores aus der Datenbank und zeigt sie in der GUI an.
     */
    private void initializeHighscoreListe() {
        for (int i = 0; i < this.slurph.getHighscoreList().size(); i++)
            this.gui.setBestenlisteneintrag(i, this.slurph.getHighscoreList().get(i).getName(), this.slurph.getHighscoreList().get(i).getScore());
    }

    /**
     * Initialisiert die gespeicherten Spiele und zeigt sie in der GUI an.
     */
    private void initializeSpielLaden() {
        //TODO SLURPH
        //this.gui.setSpielLadenEintrag();
    }

    /**
     * Platziert die Schiffe automatisch auf dem aktuellen Spielfeld, indem alle Schiffe gelöscht und neue Schiffe gesetzt werden.
     * Aktiviert den Button zum Beginn der Schlacht basierend auf der aktuellen Spielfeldseite.
     */
    private void schiffeAutomatischPlatzieren() {
        
        //alle Schiffe loeschen
        this.gui.entferneAlleSchiffe(this.currentESpielfeldEnum);
        this.getCurrentSpielfeld().loescheSchiffe();
        //neue Schiffe setzen
        int idx = 0;
        for (Map.Entry<ESchiffsTyp, Integer> entry : this.spielfeldKonfiguration.getSchiffsZusammensetzung().entrySet()) {
            ESchiffsTyp schiffsTyp = entry.getKey();
            Integer anzahl = entry.getValue();
            for(int i = 0; i < anzahl; i++) {
                boolean placed = false;
                int attempts = 0;

                while (!placed && attempts < MAX_ATTEMPTS) {
                    int rotation = (this.random.nextInt(4)) * 90; //0 - 360

                    //nur Punkte im ESpielfeld generieren
                    int x = this.random.nextInt(this.spielfeldKonfiguration.getSpielfeldBreite());
                    int y = this.random.nextInt(this.spielfeldKonfiguration.getSpielfeldHoehe());

                    if (this.getCurrentSpielfeld().kannSchiffSetzen(x, y, rotation, schiffsTyp, this.spielfeldKonfiguration.isSchiffsberuehrungErlaubt())) { //can place?
                        //set in Verwaltung
                        this.getCurrentSpielfeld().setzeSchiff(idx, x, y, schiffsTyp, rotation, ESchiffsZustand.VERZIEHBAR);
                        //set in GUI
                        this.gui.setzeSchiff(this.currentESpielfeldEnum, idx, x, y, schiffsTyp, rotation, ESchiffsZustand.VERZIEHBAR);

                        placed = true;
                        idx++;
                    }
                    attempts++;
                }
            }
        }

        //aktiviere Buttons
        if(this.currentESpielfeldEnum == ESpielfeld.LINKS)
            this.gui.setzeSchlachtBeginnenButtonAktiviertLinks(true);
        else
            this.gui.setzeSchlachtBeginnenButtonAktiviertRechts(true);
    }
}
