package Administration;

import Administration.Spieler.LokalerSpieler;
import Administration.Spieler.Spieler;
import Administration.Spieler.AiSpieler;
import GUI.*;
import Networking.Network;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.*;
import java.util.Timer;

public class Game {
    private static final int POLLING_RATE = 100;
    private static final int MAX_ATTEMPTS = 3000;

    private static Game instance;
    private final ShipzGui gui;
    private Network netzwerk;
    private boolean isServer;
    private boolean isProcessing;
    
    private final EventListener listener;
    private final Timer timer;
    private final Random random;

    private Spielfeld currentSpielfeldEnum;
    private Board spielfeldLinks;
    private Board spielfeldRechts;
    private int spielfeldHoehe;
    private int spielfeldBreite;
    private boolean schiffsberuehrungErlaubt;

    private Spieler spielerLinks;
    private Spieler spielerRechts;
    
    private boolean spielerRechtsBereit;

    private HashMap<Enums.ShipTyp, Integer> schiffsListeRechts;
    private HashMap<Enums.ShipTyp, Integer> schiffsListeLinks;

    private List<Administration.HighscoreItem> highscoreList;

    private boolean gewonnen;

    private Game(Stage stage) {
        this.isProcessing = false;
        this.timer = new Timer();
        this.listener = new EventListener();
        this.random = new Random();

        this.schiffsListeLinks = new HashMap<>();
        this.schiffsListeRechts = new HashMap<>();

        this.highscoreList = new ArrayList<>();

        this.gui = ShipzGui.createInstance(listener, stage);
        this.gui.debugModus(true);

        startPolling();
    }

    public static Game getInstance(Stage stage) {
        if (instance == null) {
            instance = new Game(stage);
        }
        return instance;
    }

    private void startPolling() {
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Game.this.isProcessing) return; //is still processing previous
                Event nextAction = Game.this.listener.getNextAction();
                if (nextAction == null) return; //queue is empty, nothing to process

                Game.this.isProcessing = true; //set flag to processing
                Platform.runLater(() -> {
                    Game.this.processAction(nextAction);
                    Game.this.isProcessing = false; // set flag to idle
                });
            }
        }, 0, POLLING_RATE); //call every 200ms
    }

    private void processAction(Event action) {
        Enums.GameAction actionType = Enums.GameAction.values()[action.getID()];
        switch (actionType) {
            case UNDO -> this.undo();
            case REDO -> this.redo();
            case SPIELRUNDE_SPEICHERN -> this.spielrundeSpeichern();
            case SPIELRUNDE_ABBRECHEN -> this.spielrundeAbbrechen();
            case BESTENLISTE_LEEREN -> this.bestenlisteLeeren();
            case SCHUSS_RETURN -> this.schussReturn(action.getData());
            case SPIEL_LADEN -> this.spielLaden();
            case SPIEL_LOESCHEN -> this.spielLoeschen();
            case SCHIFFSKONFIGURATION_ABRUFBAR -> this.schiffskonfigurationAbrufbar();
            case SCHIFFE_FERTIG_PLATZIERT -> this.schiffeFertigPlatziert();
            case LOKALE_SPIELER_ABRUFBAR -> this.lokaleSpielerAbrufbar();
            case PROGRAMM_BEENDEN -> this.programmBeenden();
            case ANSICHT_GEWAEHLT -> this.ansichtGewaehlt();
            case MENUE_ZURUECK -> this.menueZurueck();
            case SCHIFF_ROTIERT -> this.schiffRoetiert();
            case SCHIFF_BEWEGT -> this.schiffBewegt();
            case SCHIFFE_AUTOMATISCH_PLATZIEREN -> this.schiffeAutomatischPlatzieren();
            case LOAD_GAME -> this.loadGame();

            case NETZWERKDATEN_CLIENT_ABRUFBBAR -> this.netzwerkdatenClientAbrufbar();
            case NETZWERKSPIEL_ABBRECHEN -> this.netzwerkspielAbbrechen();
            case NETZWERKDATEN_HOST_ABRUFBBAR -> this.netzwerkdatenHostAbrufbar();
            case NETZWERKMODUS_ABRUFBAR -> this.netzwerkmodusAbrufbar();
            case NETZWERK_VERBUNDEN -> this.netzwerkVerbunden();
            
            case NETZWERK_NACHRICHT_NAME -> this.netzwerkNachrichtName(action.getData());
            case NETZWERK_NACHRICHT_KONFIGURATION -> this.netzwerkNachrichtSpielkonfigurationEmpfangen(action.getData());
            case NETZWERK_NACHRICHT_SCHIFFE ->  this.netzwerkNachrichtSchiffe(action.getData());
        }
    }
    
    private void netzwerkNachrichtName(String name) {
        this.spielerRechts = new LokalerSpieler(name);
    }

    private void netzwerkNachrichtSpielkonfigurationEmpfangen(String json) {
         SpielKonfiguration konfiguration = new Gson().fromJson(json, SpielKonfiguration.class);
        this.setzeSchiffsKonfiguration(konfiguration.spielfeldBreite, konfiguration.spielfeldHoehe, konfiguration.schiffsberuehrungErlaubt, konfiguration.schiffsZusammensetzung);
    }
    private void netzwerkNachrichtSchiffe(String json) {
        List<Ship> schiffe = new Gson().fromJson(json, new TypeToken<List<Ship>>() {}.getType());
        
        for(var schiff : schiffe) {
            //set in Verwaltung
            this.spielfeldRechts.setShip(schiff);
            //set in GUI
            this.gui.setzeSchiff(Spielfeld.RECHTS, schiff.getId(), schiff.getX(), schiff.getY(), schiff.getType(), schiff.getRotation(), Schiffszustand.UNGETROFFEN);
        }
        spielerRechtsBereit = true;
    }


private void netzwerkVerbunden() {
        this.netzwerk.sendActionEvent(Enums.GameAction.NETZWERK_NACHRICHT_NAME, this.spielerLinks.name); //send my Name to second player
        if (this.isServer)
            this.gui.zeigeAnsicht(Ansicht.SPIELKONFIGURATION);
        else
            this.gui.zeigeAnsicht(Ansicht.WARTE_AUF_HOST_SPIELKONFIGURATION);
    }

    private void spielrundeAbbrechen() {
        this.gui.zeigeAnsicht(Ansicht.HAUPTMENUE);
    }

    private void netzwerkdatenClientAbrufbar() { //rechts
        this.spielerLinksAbrufbar(); //eigenen Spieler erstellen
        this.netzwerk = new Network(this.listener, this.gui.getIp(), this.gui.getPort(), 200, 30000);
        new Thread(this.netzwerk).start();
    }

    private void netzwerkdatenHostAbrufbar() { //links
        this.spielerLinksAbrufbar(); //eigenen Spieler erstellen
        this.netzwerk = new Network(this.listener, this.gui.getPort(), 200, 30000);
        new Thread(this.netzwerk).start();
        this.gui.zeigeAnsicht(Ansicht.WARTE_AUF_CLIENT);
    }

    private void netzwerkmodusAbrufbar() {
        this.isServer = this.gui.getIstHost();
        if (this.isServer)
            this.gui.zeigeAnsicht(Ansicht.NETZWERKSPIEL_ERSTELLEN);
        else
            this.gui.zeigeAnsicht(Ansicht.VERBINDE_MIT_HOST);
    }

    private void netzwerkspielAbbrechen() {
    }

    private void bestenlisteLeeren() {
    }

    private void spielLaden() {
    }

    private void spielLoeschen() {
    }

    private void schiffskonfigurationAbrufbar() {
        if (netzwerk != null)
            this.netzwerkNachrichtSpielKonfigurationSenden();
        else
            this.lokalSchiffsKonfiguration();
        
        this.setzeSchiffsKonfiguration(this.gui.getSpielfeldbreite(), this.gui.getSpielfeldhoehe(), this.gui.getSchiffsberuehrungErlaubt(), this.gui.getSchiffszusammensetzung());
    }

    private void setzeSchiffsKonfiguration(int spielfeldBreite, int spielfeldHoehe, boolean schiffsberuehrungErlaubt, HashMap<Enums.ShipTyp, Integer> schiffsZusammensetzung) {
        this.spielfeldBreite = spielfeldBreite;
        this.spielfeldHoehe = spielfeldHoehe;
        this.spielfeldLinks = new Board(spielfeldBreite, spielfeldHoehe);
        this.spielfeldRechts = new Board(spielfeldBreite, spielfeldHoehe);
        this.schiffsberuehrungErlaubt = schiffsberuehrungErlaubt;
        this.schiffsListeLinks.putAll(schiffsZusammensetzung);
        this.schiffsListeRechts.putAll(schiffsZusammensetzung);

        this.gui.setzeSchlachtBeginnenButtonAktiviertLinks(false);
        this.gui.setzeSchlachtBeginnenButtonAktiviertLinks(false);
        this.gui.spielrundeEinrichten(spielfeldBreite, spielfeldHoehe, schiffsZusammensetzung, this.spielerLinks.name, this.spielerRechts.name, schiffsberuehrungErlaubt);
        
        this.currentSpielfeldEnum = Spielfeld.LINKS;
        this.gui.zeigeAnsicht(Ansicht.SCHIFFE_ANORDNEN_LINKS);
    }
    
    private void netzwerkNachrichtSpielKonfigurationSenden() {
        String spielKonfigurationJson = new Gson().toJson(new SpielKonfiguration(this.gui.getSpielfeldbreite(), this.gui.getSpielfeldhoehe(), this.gui.getSchiffsberuehrungErlaubt(), this.gui.getSchiffszusammensetzung()));
        this.netzwerk.sendActionEvent(Enums.GameAction.NETZWERK_NACHRICHT_KONFIGURATION, spielKonfigurationJson);
    }
    
    private void lokalSchiffsKonfiguration() {
        if(spielerRechts instanceof AiSpieler aiRechts)
            aiRechts.setKonfiguration(this.spielfeldBreite, this.spielfeldHoehe, this.gui.getSchiffszusammensetzung());
        if(spielerLinks instanceof AiSpieler aiLinks)
            aiLinks.setKonfiguration(this.spielfeldBreite, this.spielfeldHoehe, this.gui.getSchiffszusammensetzung());
    }   
    
    private void schiffeFertigPlatziert() {
        if(netzwerk != null) {
            
            var schiffeJson = new Gson().toJson(getCurrentSpielfeld().getShips());
            this.netzwerk.sendActionEvent(Enums.GameAction.NETZWERK_NACHRICHT_SCHIFFE, schiffeJson);
            
            if(spielerRechtsBereit) {
                this.starteSpiel();
                this.netzwerk.sendActionEvent(Enums.GameAction.NETZWERK_SPIEL_STARTEN, "");
            }
        }
        else {
            if (this.currentSpielfeldEnum == Spielfeld.RECHTS) //starte Spiels
                this.starteSpiel();
            else { ///linkes Feld, zweiter Spieler muss noch Platzieren
                this.currentSpielfeldEnum = Spielfeld.RECHTS;
                this.gui.zeigeAnsicht(Ansicht.SCHIFFE_ANORDNEN_RECHTS);
            }
        }
    }
    
    private void starteSpiel() {
        //reset score
        this.spielerLinks.score = 0;
        this.gui.aktualisiereScore(Spielfeld.LINKS, this.spielerLinks.score);
        this.spielerRechts.score = 0;
        this.gui.aktualisiereScore(Spielfeld.RECHTS, this.spielerRechts.score);
        
        if (netzwerk != null)
            this.gui.spielfeldKickEventsRegistieren(Spielfeld.RECHTS);
        else {
            this.gui.spielfeldKickEventsRegistieren(Spielfeld.LINKS);
            this.gui.spielfeldKickEventsRegistieren(Spielfeld.RECHTS);
        }

        this.currentSpielfeldEnum = isServer ? Spielfeld.LINKS : Spielfeld.RECHTS;

        this.gui.zeigeAnsicht(Ansicht.SPIEL_SPIELEN);
        
        //erster Zug
        this.naechsterZug();
    }
    
    private void naechsterZug() {
        //flip enum
        this.currentSpielfeldEnum = currentSpielfeldEnum == Spielfeld.RECHTS ? Spielfeld.LINKS : Spielfeld.RECHTS;
        
        //TODO NUR AI
        this.getCurrentPlayer().shoot();
        
        //TODO: in LokalerSpieler shoot auslagern
        
        if(this.getCurrentPlayer() instanceof LokalerSpieler || netzwerk != null)
            this.gui.spielfeldUmrandungSetzen(this.currentSpielfeldEnum);
    }
    
    private void schussReturn(String schussString) {
        String[] parts = schussString.split("/");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        if(this.getCurrentSpielfeld().isHit(x, y))
            return;

        //set in Verwaltung
        var schussTypReturn = this.getCurrentSpielfeld().hit(x, y);
        //set in GUI
        this.gui.setzeSchuss(this.currentSpielfeldEnum, x, y, schussTypReturn);
        
        //score aktualisieren & spieler bescheid sagen, falls hit
        if(schussTypReturn == Schusstyp.TREFFER || schussTypReturn == Schusstyp.TREFFER_VERSENKT) {
            
            if(this.getCurrentPlayer() instanceof AiSpieler aiSpieler)
                aiSpieler.setLastHit(new int[] {x, y});
            
            this.inkrementiereScore();
        }
        
        if(schussTypReturn == Schusstyp.TREFFER_VERSENKT) {
            //in ui schiffsliste auf versenkt
            var schiff = this.getCurrentSpielfeld().getShip(x, y);
            this.gui.setzeSchiffszustand(this.currentSpielfeldEnum, schiff.getId(), Schiffszustand.VERSENKT);
            //alle felder auf versenkt updaten
            for (int[] pos : schiff.getPositions())
                this.gui.aendereSchusstyp(this.currentSpielfeldEnum, pos[0], pos[1], Schusstyp.TREFFER_VERSENKT);
            //versenkte Schiffe von Schiffslisten abziehen
            this.getCurrentSchiffsliste().put(schiff.getType(), this.getCurrentSchiffsliste().getOrDefault(schiff.getType(), 0) -1);
            
            if(this.getCurrentPlayer() instanceof AiSpieler aiSpieler)
                aiSpieler.removeDestroyedShip(schiff.getType());
        }
        
        if(!hatGewonnen())
            this.naechsterZug();
    }

    private void inkrementiereScore() {
        this.gui.aktualisiereScore(this.currentSpielfeldEnum, this.getCurrentPlayer().score++);
    }
    
    private boolean hatGewonnen() {
        boolean gewonnen = this.getCurrentSchiffsliste().values().stream().mapToInt(Integer::intValue).sum() == 0;
        if(gewonnen) {
            //gewinner und highscore
            String name = this.getCurrentPlayer().name;
            int score = this.getCurrentPlayer().score;
            this.gui.setGewinner(name, score);
            this.highscoreList.add(new Administration.HighscoreItem(name, score));
            this.gui.setBestenlisteneintrag(1, name, score);
            this.gui.zeigeAnsicht(Ansicht.SPIELRUNDE_BEENDET);
        }
        return gewonnen;
    }

    private void lokaleSpielerAbrufbar() {
        this.spielerLinksAbrufbar();
        this.spielerRechtsAbrufbar();
        
        this.gui.zeigeAnsicht(Ansicht.SPIELKONFIGURATION);
    }

    private void spielerLinksAbrufbar() {
        if(this.gui.getSpielertypLinks() == Spielertyp.MENSCHLICHER_SPIELER)
            this.spielerLinks = new LokalerSpieler(this.gui.getSpielernameLinks());
        else
            this.spielerLinks = new AiSpieler(this.listener, this.gui.getSpielertypLinks(), this.gui.getSpielernameLinks());
    }
    
    private void spielerRechtsAbrufbar() {
        if(this.gui.getSpielertypRechts() == Spielertyp.MENSCHLICHER_SPIELER)
            this.spielerRechts = new LokalerSpieler(this.gui.getSpielernameLinks());
        else
            this.spielerRechts = new AiSpieler(this.listener, this.gui.getSpielertypRechts(), this.gui.getSpielernameRechts());
    }
    
    private void menueZurueck() {
        this.gui.zeigeAnsicht(Ansicht.HAUPTMENUE);
    }

    private void schiffRoetiert() {
    }

    private void schiffBewegt() {
    }

    private void schiffeAutomatischPlatzieren() {
        //alle Schiffe loeschen
        this.gui.entferneAlleSchiffe(this.currentSpielfeldEnum);
        this.getCurrentSpielfeld().clear();
        //neue Schiffe setzen
        int idx = 0;
        for (Map.Entry<Enums.ShipTyp, Integer> entry : this.getCurrentSchiffsliste().entrySet()) {
            Enums.ShipTyp typ = entry.getKey();
            Integer anzahl = entry.getValue();
            for(int i = 0; i < anzahl; i++) {
                boolean placed = false;
                int attempts = 0;

                while (!placed && attempts < MAX_ATTEMPTS) {
                    int rotation = (this.random.nextInt(4)) * 90; //0 - 360

                    //nur Punkte im Spielfeld generieren
                    int x = this.random.nextInt(this.spielfeldBreite);
                    int y = this.random.nextInt(this.spielfeldHoehe);

                    if (this.canSetShip(this.getCurrentSpielfeld(), typ, x, y, rotation, this.schiffsberuehrungErlaubt)){ //can place?
                        //set in Verwaltung
                        this.getCurrentSpielfeld().setShip(new Ship(idx, x, y, typ, rotation));
                        //set in GUI
                        this.gui.setzeSchiff(this.currentSpielfeldEnum, idx, x, y, typ, rotation, Schiffszustand.UNGETROFFEN);
                        
                        placed = true;
                        idx++;
                    }
                    attempts++;
                }
            }
        }
        
        //aktiviere Buttons
        if(this.currentSpielfeldEnum == Spielfeld.LINKS)
            this.gui.setzeSchlachtBeginnenButtonAktiviertLinks(true);
        else
            this.gui.setzeSchlachtBeginnenButtonAktiviertRechts(true);
    }

    private boolean canSetShip(Board spielfeld, Enums.ShipTyp typ, int x, int y, int rotation, boolean beruehrungErlaubt) {
        int breite = (rotation == 90 || rotation == 270) ? typ.getBreite() : typ.getLaenge();
        int laenge = (rotation == 90 || rotation == 270) ? typ.getLaenge() : typ.getBreite();

        int endX = x + breite - 1;
        int endY = y + laenge - 1;

        //fast bounds check
        if (endX < 0 || endX >= this.spielfeldBreite || endY < 0 || endY >= this.spielfeldHoehe)
            return false;

        for (int i = x; i <= endX; i++) {
            for (int j = y; j <= endY; j++) {

                //+-1 surrounding check
                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <=  1; l++) {
                        int posX = i + k;
                        int posY = j + l;

                        //both are 1, corner
                        if (beruehrungErlaubt && (Math.abs(k) == 1 && Math.abs(l) == 1))
                            continue;

                        //bounds & neighbour ship check
                        if (posX >= 0 && posX < this.spielfeldBreite && posY >= 0 && posY < this.spielfeldHoehe && spielfeld.getShip(posX, posY) != null)
                            return false;
                    }
                }
            }
        }
        return true;
    }

    
    private Spieler getCurrentPlayer() {
        return this.currentSpielfeldEnum == Spielfeld.LINKS ? this.spielerLinks : this.spielerRechts;
    }
    
    private Board getCurrentSpielfeld() {
        return this.currentSpielfeldEnum == Spielfeld.LINKS ? this.spielfeldLinks : this.spielfeldRechts;
    }

    private HashMap<Enums.ShipTyp, Integer> getCurrentSchiffsliste() {
        return this.currentSpielfeldEnum == Spielfeld.LINKS ? this.schiffsListeLinks : this.schiffsListeRechts;
    }
    
    private void loadGame() {
    }

    private void spielrundeSpeichern() {
    }

    private void redo() {
    }

    private void undo() {
    }

    private void programmBeenden() {
        this.timer.cancel();
        Platform.exit();
    }

    private void ansichtGewaehlt() {
        gui.zeigeAnsicht(gui.getGewaelteAnsicht());
    }
}
