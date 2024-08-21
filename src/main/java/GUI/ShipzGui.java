package GUI;

import Administration.Enums.EAction;
import Administration.Enums.ESchiffsTyp;
import Administration.Event;
import Administration.EventListener;
import GUI.ansichten.*;
import GUI.ansichten.unteransichten.Schiff;
import GUI.ansichten.unteransichten.Spielraster;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.HashMap;


/**
 * Grafische Benutzeroberfläche für Shipz
 *
 * Diese Klasse dient als Schnittstelle zwischen den Ansichten und der Spiellogik.
 * Damit sich die Spiellogik bei jeder Ansicht nicht erneut anmelden muss um Events zu erhalten,
 * wird das einzige Objekt von ShipzGui an jede Ansicht weitergeben
 * und wirft für alle Ansichten Events an die Spiellogik.
 * Die Spiellogik manipuliert die Ansichten widerum indirekt über Methoden dieser Klasse.
 */
public class ShipzGui {


    //  ---------------------------- IV ----------------------------

        /** Singleton */
        private static ShipzGui einzigeInstanz;

        /** DebugModus (de)aktiviert */
        private boolean debugModusAktiviert;

        /** Objekt der Spiellogik, um Events zu feuern */
        private final EventListener einzigerListener;

        /** Von der Spiellogik übergebene Stage des Hauptfensters */
        private final Stage hauptfensterStage;

        /** Einzige Szene des Hauptfensters */
        private final Scene hauptfensterSzene;



        // ---- Ansichten ----
        /** Ansicht des Hauptmenues */
        private Hauptmenue ansichtHauptmenue;

        /** Ansicht zum Waehlen von ESpielertyp und Namen eines lokalen Spiels */
        private LokaleSpielerWaehlen ansichtLokaleSpielerWaehlen;

         /** Ansicht zum Waehlen zwischen Host- oder Clientmodus */
        private NetzwerkmodusWaehlen ansichtNetzwerkmodusWaehlen;

        /** Ansicht zum Erstellen eines Netzwerkspiels */
        private NetzwerkspielErstellen ansichtNetzwerkspielErstellen;

        /** Ansicht zum Verbinden mit erstelltem Netzwerkspiel */
        private VerbindeMitSpiel ansichtVerbindeMitSpiel;

        /** Ansicht zur Einstellung von Spielfeldgroesse und Schiffskonfiguration */
        private Spielkonfiguration ansichtSpielkonfiguration;

        /** Ansicht zum Anordnen der Schiffe auf der linken Seite */
        private SchiffeAnordnen ansichtSchiffeAnordnenLinks;

        /** Ansicht zum Anordnen der Schiffe auf der rechten Seite */
        private SchiffeAnordnen ansichtSchiffeAnordnenRechts;

        /** Ansicht zum Spielen des Spiels */
        private Spielrunde ansichtSpielrunde;

        /** Ansicht, welche den Spielgewinner mit Score zeigt */
        private SpielrundeBeendet ansichtSpielrundeBeendet;

        /** Ansicht zum Laden gespeicherter Spiele */
        private SpielLaden ansichtSpielLaden;

        /** Ansicht mit der Bestenliste als Tabelle */
        private Bestenliste ansichtBestenliste;

        /** Ansicht zeigt IP und offenen Port */
        private WarteAufClient ansichtWarteAufClient;

        /** Ansicht fordert Client zum Warten auf */
        private WarteAufHostSpielkonfiguration ansichtWarteAufHostSpielkonfiguration;

        /** Ansicht bei Netzwerkabbruch */
        private NetzwerkverbindungAbgebrochen ansichtNetzwerkverbindungAbgebrochen;

        /** Ansicht fuer allgemeine Einstellungen */
        private Einstellungen einstellungen;

        /** Map aller Ansichten, indexiert mit Ansicht-Enum */
        private HashMap<GUI.Ansicht, GUI.ansichten.Ansicht> ansichten;



        // ---- Theme ----
        /** Themename */
        private final String themename = "military";

        /** Pfad zum Theme */
        private final String themepfad = "/themes/" + this.themename + "/";

        /** Schriftgroesse */
        private final SimpleDoubleProperty schriftgroesse = new SimpleDoubleProperty();



        // ---- Allgemeine Spieleinstellungen ----
        /** Spielraster für Spielrunde und SchiffeAnordnen */
        private Spielraster spielrasterLinks, spielrasterRechts;

        /** Anzahl Felder in der Breite */
        private int spielfeldbreite;

        /** Anzahl Felder in der Hoehe */
        private int spielfeldhoehe;

        /** Anzahl pro Schiffstyp */
        private HashMap<ESchiffsTyp, Integer> schiffszusammensetzung;

        /** Schiffe dürfen sich beruehren */
        private boolean schiffsberuehrungErlaubt;


        // ---- Einstellungen fuer Netzwerkspiel ----
        /** Host / Client */
        private boolean istHost;

        /** IP-Adresse */
        private String ip = null;

        /** Port */
        private int port = -1;


        // ---- Infos ueber Spieler ----
        /** Name des Spielers auf der linken Seite */
        private String spielernameLinks;

        /** Name des Spielers auf der rechten Seite */
        private String spielernameRechts;

        /** Score des Spielers auf der linken Seite */
        private int scoreSpielerLinks;

        /** Score des Spielers auf der rechten Seite */
        private int scoreSpielerRechts;

        /** Typ des Spielers auf der linken Seite */
        private ESpielertyp ESpielertypLinks;

        /** Typ des Spielers auf der rechten Seite */
        private ESpielertyp ESpielertypRechts;



        // ----  Schiffe und Koordinaten ----
        /** x-Koordinate eines Schusses/Schiffs */
        private int x = -1;

        /** y-Koordinate eines Schusses/Schiffs */
        private int y = -1;

        /** ID eines Schiffs */
        private int schiffsId = -1;

        /** Rotation eines Schiffs */
        private int rotation = -1;


        // ---- Weitere abbrufebare Variablen ----
        /** Vom User selektierte Ansicht */
        private Ansicht gewaelteAnsicht = Ansicht.NICHT_VERFUEGBAR;

        // ---- Spiel Laden ----
        /** Long mit Datum */
        private long datum = -1;




    /**
     * Konstruktor ShipzGui
     * Privat, weil Singleton
     * @param einzigerListener    EventListener, an den die Events gefeuert werden
     * @param stage               JavaFX Stage
     */
    private ShipzGui(EventListener einzigerListener, Stage stage) {

        this.einzigerListener = einzigerListener;

        // Hauptfenster konfigurieren
            this.hauptfensterStage = stage;
            this.hauptfensterStage.setResizable(false);
            this.hauptfensterStage.setFullScreenExitHint("");
            this.hauptfensterStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

            // Mindestgröße
            Image Icon = new Image(getClass().getClassLoader().getResourceAsStream("IconVectorizedTransparent.png"));
            stage.getIcons().add(Icon);
            stage.setMinWidth(800);
            stage.setMinHeight(600);

            // Szene konfigurieren
            this.hauptfensterSzene = new Scene(new Pane(), 900, 600);

            // CSS
            String cssPath = this.getClass().getResource(themepfad + this.themename + ".css").toExternalForm(); // Pfad zusammenbauen
            hauptfensterSzene.getStylesheets().add(cssPath); // CSS zur Szene hinzufuegen

            // Szene in Stage setzen
            this.hauptfensterStage.setScene(hauptfensterSzene);

        // Startsceen laden
        this.hauptfensterSzene.setRoot(new Startscreen(this));
        this.hauptfensterStage.setTitle("Shipz");
        this.hauptfensterStage.show();
    }


    /**
     * Erzeugt beim ersten Aufruf ein ShipzGui-Objekt, läd benötigte Grafiken und zeigt die Startansicht an.
     * Bei jedem weiteren Aufruf wird das erstelle ShipzGui-Objekt zurückgegeben.
     * @param einzigerListener    EventListener, an den die Events gefeuert werden
     * @param stage               JavaFX Stage
     * @return Einzigartiges ShipzGui-Objekt
     */
    public static ShipzGui getInstance(EventListener einzigerListener, Stage stage) {
        if(einzigeInstanz == null) {
            einzigeInstanz = new ShipzGui(einzigerListener, stage);

            // Schiffsbilder für später laden
            Schiff.schiffsbilderLaden();

            // Blinker fuer falsch Positionierte Schiffe starten
            Schiff.blinkerStarten();
        }
        return einzigeInstanz;
    }


    /**
     * Gibt einziges ShipGui-Objekt zurück. Wenn dieses nicht existiert, dann null.
     * @return Einzigartiges ShipzGui-Objekt
     */
    public static ShipzGui getInstance() {
        return einzigeInstanz;
    }


    /**
     * (De)Aktiviert den DebugModus.
     * Der aktivierte Debugmodus zeigt Errornachrichten in der Console an.
     * @param aktiviert  DebugModus (de)aktiviert
     */
    public void debugModus(boolean aktiviert) {
        debugModusAktiviert = aktiviert;
    }


    /**
     * Scheibt eine Nachricht auf die Konsole, wenn der Debugmodus aktiviert ist.
     * @param nachricht     Auszugebende Nachricht
     * @param istError      Wenn True wird System.err.println() genutzt, sonst System.out.println()
     */
    public void debugNachricht(String nachricht, boolean istError) {
        if(this.debugModusAktiviert) {
            if (istError) {
                System.err.println("GUI: " + nachricht);
            } else {
                System.out.println("GUI: " + nachricht);
            }
        }
    }


    /**
     * Feuert ein Event mit Source this an this.einzigerListener
     */
    public void feuerEvent(EAction eventTyp, String command) {
        this.einzigerListener.actionPerformed(new Event(ShipzGui.class.getSimpleName(), eventTyp.ordinal(), command));
    }


    /**
     * Gibt die eingestellte Schriftgroesse als SimpleDoubleProperty zurueck.
     * @return  Eingestellte Schriftgroesse
     */
    public SimpleDoubleProperty getSchriftgroesse() {
        return this.schriftgroesse;
    }


    /**
     * Gibt den aktuellen Themepfad zurueck.
     * @return  Pfad zum aktuellen Theme
     */
    public String getThemepfad() {
        return this.themepfad;
    }

    /**
     * Gibt den Namen des aktuellen Themes zurueck.
     * @return  Namen des aktuellen Themes
     */
    public String getThemename() {
        return this.themename;
    }


    /**
     * Laed die GUI mit einem neuen Theme.
     */
    public void wechselTheme() {
        // TODO
    }



    //  ---------------------------- Abrufen und Setzen von Eventdaten ----------------------------


    /**
     * Gibt die X-Koordinate eines Schusses zurück
     * @return X-Koordinate
     */
    public int getX() {
        return this.x;
    }


    /**
     * Gibt die y-Koordinate eines Schusses zurück
     * @return Y-Koordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * Setzt abrufbare Koordinate.
     * @param x-Koordinate
     * @param y-Koordinate
     */
    public void setzeKoordinaten(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Gibt die Spielfeldbreite zurück
     * @return Anzahl an Feldern in der Breite
     */
    public int getSpielfeldbreite() {
        return this.spielfeldbreite;
    }

    /**
     * Gibt die Spielfeldhoehe zurück
     * @return Anzahl an Feldern in der Hoehe
     */
    public int getSpielfeldhoehe() {
        return this.spielfeldhoehe;
    }

    /**
     * Setzt abrufbare Spielfeldbreite.
     * @param spielfeldbreite Anzahl an Feldern in der Breite
     * @param spielfeldhoehe Anzahl an Feldern in der Hoehe
     */
    public void setzeSpielfeldgroesse(int spielfeldbreite, int spielfeldhoehe) {
        this.spielfeldbreite = spielfeldbreite;
         this.spielfeldhoehe = spielfeldhoehe;
    }


    /**
     * Gibt SchiffsId zurück
     * @return SchiffsID
     */
    public int getSchiffsId() {
        return this.schiffsId;
    }

    /**
     * Setzt abrufbare SchiffsId
     * @param id SchiffsID
     */
    public void setSchiffsId(int id) {
        this.schiffsId = id;
    }


    /**
     * Gibt Rotation eines Schiffs zurück
     * @return SchiffsID
     */
    public int getRotation() {
        return this.rotation;
    }

    /**
     * Setzt abrufbare Rotation eines Schiffs
     * @param rotation Rotation eines Schiffs
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }


    /**
     * Gibt Schiffszusammensetzung fuer Spielrunde zurück
     *
     * @return SchiffsID
     */
    public HashMap<ESchiffsTyp, Integer> getSchiffszusammensetzung() {
        return this.schiffszusammensetzung;
    }

    /**
     * Setzt abrufbare Schiffszusammensetzung fuer Spielrunde.
     * @param schiffszusammensetzung Anzahl je Schiffstyp
     */
    public void setSchiffszusammensetzung(HashMap<ESchiffsTyp, Integer> schiffszusammensetzung) {
        this.schiffszusammensetzung = schiffszusammensetzung;
    }


    /**
     * Gibt TRUE zurueck, wenn sich Schiffe im Spiel beruehren duerfen.
     * @return TRUE, wenn sich Schiffe im Spiel beruehren duerfen, sonst FALSE
     */
    public boolean getSchiffsberuehrungErlaubt() {
        return this.schiffsberuehrungErlaubt;
    }

    /**
     * Setzt abrufbaren Boolean, ob Schiffe sich im Spiel beruehren duerfen
     * @param schiffsberuehrungErlaubt Anzahl je Schiffstyp
     */
    public void setSchiffsberuehrungErlaubt(boolean schiffsberuehrungErlaubt) {
        this.schiffsberuehrungErlaubt = schiffsberuehrungErlaubt;
    }


    /**
     * Gibt den Spielernamen des linken Spielers zurueck.
     * @return Name des linken Spielers
     */
    public String getSpielernameLinks() {
        return this.spielernameLinks;
    }

    /**
     * Setzt abrufbaren Namen des linken Spielers
     * @param spielernameLinks Name des Spielers am linken ESpielfeld
     */
    public void setSpielernameLinks(String spielernameLinks) {
        this.spielernameLinks = spielernameLinks;
    }


    /**
     * Gibt den Spielernamen des rechten Spielers zurueck.
     * @return Name des rechten Spielers
     */
    public String getSpielernameRechts() {
        return this.spielernameRechts;
    }


    /**
     * Setzt abrufbaren Namen des rechten Spielers
     * @param spielernameRechts Name des Spielers am linken ESpielfeld
     */
    public void setSpielernameRechts(String spielernameRechts) {
        this.spielernameRechts = spielernameRechts;
    }


    /**
     * Gibt den Typ des linken Spielers zurueck und setzt die IV danach auf NICHT_VERFUEGBAR.
     * @return Typ des linken Spielers
     */
    public ESpielertyp getSpielertypLinks() {
        return this.ESpielertypLinks;
    }

    /**
     * Setzt den Typ des linken Spielers.
     * @param ESpielertyp ESpielertyp
     */
    public void setSpielertypLinks(ESpielertyp ESpielertyp) {
        this.ESpielertypLinks = ESpielertyp;
    }


    /**
     * Gibt den Typ des rechten Spielers zurueck und setzt die IV danach auf NICHT_VERFUEGBAR.
     * @return Typ des rechten Spielers
     */
    public ESpielertyp getSpielertypRechts() {
        return this.ESpielertypRechts;
    }

    /**
     * Setzt den Typ des rechten Spielers.
     * @param ESpielertyp ESpielertyp
     */
    public void setSpielertypRechts(ESpielertyp ESpielertyp) {
        this.ESpielertypRechts = ESpielertyp;
    }


    /**
     * Setzt Gewinnernamen mit Score.
     * @param gewinnername Name des Gewinners
     * @param endscore     Endscore des Gewinners
     */
    public void setGewinner(String gewinnername, int endscore) {
        this.ansichtSpielrundeBeendet.setGewinner(gewinnername, endscore);
    }


    /**
     * Gibt die vom User durch Menuepunkte selektierte Ansicht zurueck
     * und setzt die IV danach auf NICHT_VERFUEGBAR.
     * @return Gewaelte Ansicht
     */
    public Ansicht getGewaelteAnsicht() {
        return this.gewaelteAnsicht;
    }


    /**
     * Setzt eine vom User selektierte Ansicht.
     * @param  gewaelteAnsicht  Ansicht, als naeschtes aufgerufen werden soll
     */
    public void setGewaelteAnsicht(Ansicht gewaelteAnsicht) {
        this.gewaelteAnsicht = gewaelteAnsicht;
    }


    /**
     * Gibt die eingegebenen Port-Nummer zurueck
     * und setzt die IV danacht auf -1.
     * @return Port-Nummer
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Setzt Port-Nummer
     * @param port Port-Nummer
     */
    public void setPort(int port) {
        this.port = port;
    }


    /**
     * Gibt die eingegebenen IPv4-Adresse zurueck
     * @return IPv4-Adresse
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * Setzt IP-Adresse
     * @param ip IP-Adresse
     */
    public void setIp(String ip) {
        this.ip = ip;
    }


    /**
     * Gibt zurueck, ob Programm Host oder Client im Netzwerkmodus ist.
     * TRUE = Host, FALSE = Client
     * @return TRUE, wenn Host, sonst FALSE
     */
    public boolean getIstHost() {
        return this.istHost;
    }

    /**
     * Setzt IV istHost.
     * @param istHost TRUE, wenn Host, FALSE wenn Client
     */
    public void setIstHost(boolean istHost) {
        this.istHost = istHost;
    }


    /**
     * Setzt die angezeigte IP und den Port für Ansicht WartenAufClient
     * @param ip    IP
     * @param port  Port
     */
    public void setVerbindungsdaten(String ip, int port) {
        this.ansichtWarteAufClient.setVerbindungsdaten(ip,port);
    }


    /**
     * Zeigt auf der Anischt VerbindeMitSpiel einen Text an, dass die Verbindung nicht hergestellt werden konnte.
     */
    public void zeigeTextVerbindungNichtErfolreich() {
        this.ansichtVerbindeMitSpiel.zeigeTextVerbindungNichtErfolreich();
    }


    /**
     * Setzt einen Bestenlisteneintrag fuer die Ansicht SpielLaden
     * @param datum Long mit Unixzeit
     */
    public void setSpielLadenEintrag(long datum) {
        this.ansichtSpielLaden.setSpielLadenEintrag(datum);
    }


    /**
     * Gibt Unix-Timestamp eines SpielLadenEintrags zurueck
     
     * @return IPv4-Adresse
     */
    public long getDatum() {
        return this.datum;
    }

    /**
     * Setzt Unix-Timestamp eines SpielLadenEintrags.
     * @param datum Unix-Timestamp eines SpielLadenEintrags
     */
    public void setDatum(long datum) {
        this.datum = datum;
    }


    /**
     * Setzt einen Eintrag fuer die Bestenliste.
     * @param rang          Rang in der Liste
     * @param spielername   Name des Spielers
     * @param score         Punktestand
     */
    public void setBestenlisteneintrag(int rang, String spielername, int score) {
        this.ansichtBestenliste.setEintrag(rang, spielername, score);
    }




    //  ---------------------------- Funktionen ----------------------------

    /**
     * Erstellt jede Ansicht einmal und schreibt sie in das this.ansichten.
     * Bestehende Ansichten werden geloescht.
     */
    private void ansichtenErstellen() {

        // Map erstellen
        this.ansichten = new HashMap<>(15);

        // Jede Ansicht einmal erstellen und in die Map schreiben
        this.ansichten.put(Ansicht.HAUPTMENUE, this.ansichtHauptmenue = new Hauptmenue());
        this.ansichten.put(Ansicht.LOKALE_SPIELER_WAEHLEN, this.ansichtLokaleSpielerWaehlen = new LokaleSpielerWaehlen());
        this.ansichten.put(Ansicht.NETZWERKMODUS_WAEHLEN, this.ansichtNetzwerkmodusWaehlen = new NetzwerkmodusWaehlen());
        this.ansichten.put(Ansicht.NETZWERKSPIEL_ERSTELLEN, this.ansichtNetzwerkspielErstellen = new NetzwerkspielErstellen());
        this.ansichten.put(Ansicht.NETZWERKSPIEL_VERBINDEN_ZU_HOST, this.ansichtVerbindeMitSpiel = new VerbindeMitSpiel());
        this.ansichten.put(Ansicht.SPIELKONFIGURATION, this.ansichtSpielkonfiguration = new Spielkonfiguration());
        this.ansichten.put(Ansicht.SCHIFFE_ANORDNEN_LINKS, this.ansichtSchiffeAnordnenLinks = new SchiffeAnordnen(ESpielfeld.LINKS));
        this.ansichten.put(Ansicht.SCHIFFE_ANORDNEN_RECHTS, this.ansichtSchiffeAnordnenRechts = new SchiffeAnordnen(ESpielfeld.RECHTS));
        this.ansichten.put(Ansicht.SPIEL_SPIELEN, this.ansichtSpielrunde = new Spielrunde());
        this.ansichten.put(Ansicht.SPIELRUNDE_BEENDET, this.ansichtSpielrundeBeendet = new SpielrundeBeendet());
        this.ansichten.put(Ansicht.SPIEL_LADEN, this.ansichtSpielLaden = new SpielLaden());
        this.ansichten.put(Ansicht.BESTENLISTE, this.ansichtBestenliste = new Bestenliste());
        this.ansichten.put(Ansicht.NETZWERKSPIEL_WARTE_AUF_CLIENT,this.ansichtWarteAufClient = new WarteAufClient());
        this.ansichten.put(Ansicht.WARTE_AUF_HOST_SPIELKONFIGURATION, this.ansichtWarteAufHostSpielkonfiguration = new WarteAufHostSpielkonfiguration());
        this.ansichten.put(Ansicht.NETZWERK_ABBRUCH, this.ansichtNetzwerkverbindungAbgebrochen = new NetzwerkverbindungAbgebrochen());
        this.ansichten.put(Ansicht.EINSTELLUNGEN, this.einstellungen = new Einstellungen());


        // Schriftgroesse jeder Ansichten binden
        this.ansichten.values().forEach(ansicht -> {
            ansicht.styleProperty().bind(Bindings.concat(
                    "-fx-font-size: ",
                    schriftgroesse,
                    "px"
            ));
        });
    }
    
    /**
     * Zeigt das Hauptmenü auf der obersten Ebene an.
     * @param ansicht View, der angezeigt werden soll
     */
    public void zeigeAnsicht(Ansicht ansicht) {
        this.setGewaelteAnsicht(ansicht);
        // Wenn Ansicht in der Map existiert (sicher ist sicher...)
        if(this.ansichten.containsKey(ansicht)) {

            // Ansicht holen
            GUI.ansichten.Ansicht naechsteAnsicht = this.ansichten.get(ansicht);

            // Ansicht aktualiseren
            naechsteAnsicht.aktualisieren();

            // Ansicht als RootPane hinzufuegen
            hauptfensterSzene.setRoot(naechsteAnsicht);
        }
    }


    /**
     * Konfiguriert eine Spielrunde, ohne das Spiel zu starten.
     * @param spiefeldbreite            Anzahl Felder Breite
     * @param spielfeldhoehe            Anzahl Felder Hoehe
     * @param schiffszusammensetzung    Long mit Schiffskonfiguration
     * @param spielernameLinks          Name Spieler Links
     * @param spielernameRechts         Name Spieler Rechts
     * @param schiffsberuehrungErlaubt  True, wenn Beruehrung erlaubt
     */
    public void spielrundeEinrichten(
            int spiefeldbreite,
            int spielfeldhoehe,
            HashMap<ESchiffsTyp, Integer> schiffszusammensetzung,
            String spielernameLinks,
            String spielernameRechts,
            boolean schiffsberuehrungErlaubt
    ) {

        // Spielraster erstellen
        this.spielrasterLinks = new Spielraster(
                spiefeldbreite,
                spielfeldhoehe,
                schiffszusammensetzung,
                spielernameLinks,
                ESpielfeld.LINKS
        );
        this.spielrasterRechts = new Spielraster(
                spiefeldbreite,
                spielfeldhoehe,
                schiffszusammensetzung,
                spielernameRechts,
                ESpielfeld.RECHTS
        );


        // Ansicht Spielrunde einrichten
        this.ansichtSpielrunde.einrichten(this.spielrasterLinks, this.spielrasterRechts);

        // SchiffeAnordnen Ansichten einrichten
        this.ansichtSchiffeAnordnenLinks.einrichten(
                this.spielrasterLinks,
                spielernameLinks,
                schiffsberuehrungErlaubt
        );
        this.ansichtSchiffeAnordnenRechts.einrichten(
                this.spielrasterRechts,
                spielernameRechts,
                schiffsberuehrungErlaubt
        );
    }


    /**
     * Macht Felder des gewählten Spielfelds klickbar
     * @param ESpielfeld  Scharfzustellendes ESpielfeld
     */
    public void spielfeldKlickEventsRegistieren(ESpielfeld ESpielfeld) {

        if(spielraster(ESpielfeld) != null) {
            spielraster(ESpielfeld).scharfstellen();
        } else {
            this.debugNachricht("spielfeldScharfstellen(): Keine Spielrunde konfiguriert.", true);
        }

    }


    /**
     * Aktiviert das gewählte ESpielfeld.
     * Wenn dieses scharfgestellt ist, kann darauf geschossen werden.
     * Das jeweils andere ESpielfeld wird deaktiviert.
     * @param ESpielfeld  Zu aktivierendes ESpielfeld
     */
    public void spielfeldUmrandungSetzen(ESpielfeld ESpielfeld) {

        if(spielraster(ESpielfeld) != null) {
            switch (ESpielfeld) {
                case LINKS:
                    this.spielrasterRechts.deaktivieren();
                    spielrasterLinks.aktivieren();
                    break;

                case RECHTS:
                    spielrasterLinks.deaktivieren();
                    spielrasterRechts.aktivieren();
                    break;
            }
        }

    }


    /**
     * Setzt einen Schuss in ein ESpielfeld
     * @param ESpielfeld    Linkes oder rechtes ESpielfeld
     * @param x            x-Koordinate
     * @param y            y-Koordinate
     * @param typ          Typ des Schusses
     */
    public void setzeSchuss(ESpielfeld ESpielfeld, int x, int y, Schusstyp typ) {

        if(spielraster(ESpielfeld) != null) {
            spielraster(ESpielfeld).setzeSchuss(x,y,typ);
        } else {
            this.debugNachricht("setzeSchuss(): Keine Spielrunde konfiguriert.", true);
        }

    }


    /**
     * Entfernt einen Schuss aus einem ESpielfeld
     * @param ESpielfeld    Linkes oder rechtes ESpielfeld
     * @param x            x-Koordinate
     * @param y            y-Koordinate
     */
    public void entferneSchuss(ESpielfeld ESpielfeld, int x, int y) {

        if(spielraster(ESpielfeld) != null) {
            spielraster(ESpielfeld).entferneSchuss(x,y);
        } else {
            this.debugNachricht("entferneSchuss(): Keine Spielrunde konfiguriert.", true);
        }

    }


    /**
     * Ändert einen Schusstyp in einem ESpielfeld
     * @param ESpielfeld    Linkes oder rechtes ESpielfeld
     * @param x            x-Koordinate
     * @param y            y-Koordinate
     * @param typ          Typ des Schusses
     */
    public void aendereSchusstyp(ESpielfeld ESpielfeld, int x, int y, Schusstyp typ) {

        if(spielraster(ESpielfeld) != null) {
            spielraster(ESpielfeld).aendereSchusstyp(x,y,typ);
        } else {
            this.debugNachricht("aendereSchusstyp(): Keine Spielrunde konfiguriert.", true);
        }

    }


    /**
     * Setzt ein Schiff in eines der Spielfelder
     * @param ESpielfeld         ESpielfeld, in welches das Schiff geschrieben werden soll
     * @param id                ID des Schiffes
     * @param x                 x-Koordinate
     * @param y                 y-Koordinate
     * @param typ               Typ des Schiffes
     * @param rotation          Rotation des Schiffes
     * @param ESchiffsZustand    Zustand des Schiffes
     */
    public void setzeSchiff(ESpielfeld ESpielfeld, int id, int x, int y, ESchiffsTyp typ, int rotation, ESchiffsZustand ESchiffsZustand) {

        if(spielraster(ESpielfeld) != null) {
            spielraster(ESpielfeld).setzeSchiff(id,x,y,typ,rotation, ESchiffsZustand);
        } else {
            this.debugNachricht("setzeSchiff(): Keine Spielrunde konfiguriert.", true);
        }

    }


    /**
     * Entfernt ein Schiff aus einem ESpielfeld
     * @param ESpielfeld    Linkes oder rechtes ESpielfeld
     * @param id           ID des Schiffs
     */
    public void entferneSchiff(ESpielfeld ESpielfeld, int id) {

        if(spielraster(ESpielfeld) != null) {
            spielraster(ESpielfeld).entferneSchiff(id);
        } else {
            this.debugNachricht("entferneSchiff(): Keine Spielrunde konfiguriert.", true);
        }

    }


    /**
     * Entfernt alle Schiff aus einem ESpielfeld
     * @param ESpielfeld    Linkes oder rechtes ESpielfeld
     */
    public void entferneAlleSchiffe(ESpielfeld ESpielfeld) {

        if(spielraster(ESpielfeld) != null) {
            spielraster(ESpielfeld).entferneAlleSchiffe();
        } else {
            this.debugNachricht("entferneAlleSchiffe(): Keine Spielrunde konfiguriert.", true);
        }

    }


    /**
     * Aktualisiert den optischen Zustand eines Schiffes.
     * @param ESpielfeld         Linkes oder rechtes ESpielfeld
     * @param id                ID des Schiffs
     * @param ESchiffsZustand    Der neue Zustand des Schiffes (UNGETROFFEN, GETROFFEN, VERSENKT, VERZIEHBAR)
     */
    public void setzeSchiffszustand(ESpielfeld ESpielfeld, int id, ESchiffsZustand ESchiffsZustand) {

        if(spielraster(ESpielfeld) != null) {
            spielraster(ESpielfeld).setzeSchiffszustand(id, ESchiffsZustand);
        } else {
            this.debugNachricht("setzeSchiffszustand(): Keine Spielrunde konfiguriert.", true);
        }

    }


    /**
     * Setzt den Zustand aller Schiffe eines Spielfelds auf UNGETROFFEN.
     * @param ESpielfeld         Linkes oder rechtes ESpielfeld
     */
    public void setzeAlleSchiffeAufUngetroffen(ESpielfeld ESpielfeld) {

        if(spielraster(ESpielfeld) != null) {
            spielraster(ESpielfeld).setzteAlleSchiffeAufUngetroffen();
        } else {
            this.debugNachricht("setzteAlleSchiffeAufUngetroffen(): Keine Spielrunde konfiguriert.", true);
        }

    }


    /**
     * Aendert den angezeigten Score eines Spielfelds
     * @param ESpielfeld     Linkes oder rechtes ESpielfeld
     * @param neuerScore    Neuer angezeigter Score
     */
    public void aktualisiereScore(ESpielfeld ESpielfeld, int neuerScore) {

        // Score im Spielrasterlabel aktualisieren
        spielraster(ESpielfeld).aktualisiereScore(neuerScore);

        // IV setzen (wird spaeter fuer Gewinner gebraucht)
        switch (ESpielfeld) {
            case LINKS:
                this.scoreSpielerLinks = neuerScore;
                break;

            case RECHTS:
                this.scoreSpielerRechts = neuerScore;
                break;
        }
    }


    /**
     * Aktiviert / Deaktiviert den "Undo"-Button
     * @param aktiviert    True = aktiviert, false = deaktiviert
     */
    public void setzeUndoButtonAktiviert(boolean aktiviert) {
        this.ansichtSpielrunde.setzeUndoButtonAktviert(aktiviert);
    }


    /**
     * Aktiviert / Deaktiviert den "Redo"-Button
     * @param aktiviert    True = aktiviert, false = deaktiviert
     */
    public void setzeRedoButtonAktiviert(boolean aktiviert) {
        this.ansichtSpielrunde.setzeRedoButtonAktviert(aktiviert);
    }


    /**
     * Aktiviert / Deaktiviert den "Speichern"-Button
     * @param aktiviert    True = aktiviert, false = deaktiviert
     */
    public void setzeSpeichernBeginnenButtonAktiviert(boolean aktiviert) {
        this.ansichtSpielrunde.setzeSpeichernBeginnenButtonAktiviert(aktiviert);
    }


    /**
     * Aktiviert den "Schlacht Beginnen"-Button
     */
    public void setzeSchlachtBeginnenButtonAktiviertRechts(boolean aktiviert) {
        this.ansichtSchiffeAnordnenRechts.setzeSchlachtBeginnenButtonAktiviert(aktiviert);
    }

    public void setzeSchlachtBeginnenButtonAktiviertLinks(boolean aktiviert) {
        this.ansichtSchiffeAnordnenLinks.setzeSchlachtBeginnenButtonAktiviert(aktiviert);
    }

    /**
     * Aktiviert / Deaktiviert den "Automatisch Platzieren"-Button
     * @param aktiviert    True = aktiviert, false = deaktiviert
     */
    public void setzeAutomatischPlatzierenButtonAktiviertRechts(boolean aktiviert) {
        this.ansichtSchiffeAnordnenRechts.setzeAutomatischPlatzierenButtonAktiviert(aktiviert);
    }

    public void setzeAutomatischPlatzierenButtonAktiviertLinks(boolean aktiviert) {
        this.ansichtSchiffeAnordnenLinks.setzeAutomatischPlatzierenButtonAktiviert(aktiviert);
    }


    /**
     * Gibt je nach Enumwert von ESpielfeld das jeweilige Spielraster zurück.
     * @param   ESpielfeld ESpielfeld.LINKS oder ESpielfeld.RECHTS
     * @return  Linkes oder rechtes Spielraster
     */
    private Spielraster spielraster(ESpielfeld ESpielfeld) {
        switch (ESpielfeld) {
            case LINKS:
                return this.spielrasterLinks;

            case RECHTS:
                return this.spielrasterRechts;

            default:
                // Kommt nie vor
                throw new IllegalArgumentException("Ungueltiges ESpielfeld");
        }
    }


    /**
     * Aendert Eigenschaften der Fensters.
     * Bei istFullscreen = True werden die Masse des Fensters ignoriert.
     * @param aufoesungsstring      Aus der ChoiceBox ausgelesener String der Aufloesung
     * @param fenstermodusString    Aus der ChoiceBox ausgelesener String des FEnstermodus
     */
    public void fensterKonfigurieren(String aufoesungsstring, String fenstermodusString) {


        // Fenstergroessen aus ChoiceBox beziehen
        int fensterbreite, fensterhoehe, schriftgroesse;
        switch (aufoesungsstring) {
            case "1024x768":
                fensterbreite = 1024;
                fensterhoehe = 768;
                schriftgroesse = 16;
                break;
            case "1280x720":
                fensterbreite = 1280;
                fensterhoehe = 720;
                schriftgroesse = 18;
                break;
            case "1280x800":
                fensterbreite = 1280;
                fensterhoehe = 800;
                schriftgroesse = 18;
                break;
            case "1366x768":
                fensterbreite = 1366;
                fensterhoehe = 768;
                schriftgroesse = 20;
                break;
            case "1920x1080":
                fensterbreite = 1920;
                fensterhoehe = 1080;
                schriftgroesse = 26;
                break;
            case "1920x1200":
                fensterbreite = 1920;
                fensterhoehe = 1200;
                schriftgroesse = 26;
                break;

            case "800x600":
            default:
                fensterbreite = 800;
                fensterhoehe = 600;
                schriftgroesse = 14;
                break;
        }

        // Fenstermodus aus ChoiceBox beziehen
        boolean istFullscreen;
        switch (fenstermodusString) {
            case "FULLSCREEN":
                istFullscreen = true;
                break;
            case "FENSTER":
            default:
                istFullscreen = false;
                break;
        }


        if(istFullscreen) {
            hauptfensterStage.setResizable(true);
            hauptfensterStage.setFullScreen(true);
        } else {
            this.hauptfensterStage.setFullScreen(false);
            this.hauptfensterStage.setWidth(fensterbreite);
            this.hauptfensterStage.setHeight(fensterhoehe);
        }
        this.schriftgroesse.set(schriftgroesse);

        hauptfensterStage.setResizable(false);
        hauptfensterStage.setMaximized(false);

        // Alle Ansichten neu erstellen
        ansichtenErstellen();
    }

} // Ende Class ShipzGui
