package GUI.ansichten.unteransichten;

import Administration.Enums.EAction;
import Administration.Enums.ESchiffsTyp;
import Administration.SpielZug;
import GUI.ESchiffsZustand;
import GUI.Schusstyp;
import GUI.ShipzGui;
import GUI.ESpielfeld;
import com.google.gson.Gson;
import javafx.animation.FadeTransition;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Klasse Spielraster
 *
 * Das ESpielfeld besteht aus vier Ebenen (von unten nach oben):
 * - Felder:    Panes mit Border, die ein sichtbares Raster bilden
 * - Schiffe:   Werden je nach Modus von Anfang an oder beim Versenken reingeschrieben
 * - Schuesse:  Liegen auf oberster Ebene und koennen mit scharfstellen() mit Eventhandlern zum feuern versehen werden.
 *              Bei einem Schuss wird eine passende Grafik eingesetzt.
 * - Rahmen:    Zeigt mit leuchtender Border an, welches ESpielfeld gerade aktiv ist.
 *              Nur bei aktivem Zustand werden die Klickevents durchgeleitet.
 *
 * Unter dem ESpielfeld befinden sich außerdem:
 * - Ein Button zum automatischen Platzieren der Schiffe
 * - Der Spielername mit seinem Punktestand
 * Button und Spielername mit Punkten können einzelnd ein- und ausgeblendet werden.
 *
 */
public class Spielraster extends GridPane {

    // CV
    /** Abstand zwischen dem ESpielfeld und den Dekorationen, in Prozent der Breite einer Spielrasterzelle. */
    private static final double DEKORATIONSABSTAND = 0.17;

    /** Breite der rechten Dekoration (Schiffsliste), in Prozent der Breite einer Spielrasterzelle. */
    private static final double RECHTE_DEKORATIONSBREITE = 1.35;

    /** Hoehe der unteren Dekoration (Plazierenbutton / Label), in Prozent der Breite einer Spielrasterzelle. */
    private static final double UNTERE_DEKORATIONSHOEHE = 0.85;


    // IV
    /** Höhe und Breite des Spielrasters */
    private final int hoehe;
    private final int breite;

    /** Ratio des gesamten Spielrasters unter Berücksichtigung der Dekorationen */
    private final double ratio;

    /** Linkes oder rechtes Spielraster */
    private final ESpielfeld seite;

    /** Speichert Schussreferenzen, um später darauf zugreifen zu können */
    private final Schuss[][] schuesse;

    /** Speichert Schiffsreferenzen mit ihrer ID als Key */
    private final HashMap<Integer, Schiff> schiffe;

    /** Zeigt, ob ein ESpielfeld aktiv ist und verdeckt sonst die Felder,
     * sodass diese nicht geklickt werden können */
    private final Pane rahmen;

    /** Binding fuer die Breite/Hoehe einer Zelle */
    private final NumberBinding zellengroesse;

    /** Binding fuer die Offsets zum ESpielfeld ohne Dekorationen */
    private final NumberBinding linkesOffset;
    private final NumberBinding rechtesOffset;

    /** Wird zum Feuern der Events und beziehen von Daten genutzt */
    private final ShipzGui gui = ShipzGui.getInstance();

    /** Platziert die Schiffe automatisch auf dem ESpielfeld */
    private final Button automatischPlatzierenButton;

    /** Spielernamen */
    private final String spielername;

    /** Label mit Name und Score */
    private final Label label;

    /** Schiffsliste */
    private final Schiffsliste schiffsliste;



    /**
     * Konstruktor Spielraster
     * @param breite                Anzahl der Felder in der Breite
     * @param hoehe                 Anzahl der Felder pro Hoehe
     * @param schiffskonfiguration  Zusammensetzung der Schiffstypen
     * @param spielername           Name des Spielers
     * @param seite                 Linkes oder rechtes Spielraster
     */
    public Spielraster(int breite, int hoehe, HashMap<ESchiffsTyp, Integer> schiffskonfiguration, String spielername, ESpielfeld seite) {

        // Seite speichern
        this.seite = seite;

        // Rastergrößen setzen
        this.hoehe = hoehe;
        this.breite= breite;
        this.schuesse = new Schuss[hoehe][breite];

        // Aspectratio berechnen
        double gesamtBreite = breite + 1 + DEKORATIONSABSTAND + RECHTE_DEKORATIONSBREITE;
        double gesamtHoehe = hoehe + 1 + DEKORATIONSABSTAND + UNTERE_DEKORATIONSHOEHE;
        this.ratio = gesamtBreite / gesamtHoehe;

        // Schifsmap anlegen
        this.schiffe = new HashMap<Integer, Schiff>(10);

        // Zellengroesse berchnen und als Binding abspeichern
        zellengroesse = this.widthProperty().divide(breite + 1 + DEKORATIONSABSTAND + RECHTE_DEKORATIONSBREITE);
        this.linkesOffset = zellengroesse;
        this.rechtesOffset = zellengroesse.multiply(DEKORATIONSABSTAND + RECHTE_DEKORATIONSBREITE);

        // Constraintslisten holen
        List<RowConstraints> rowConstraintsListe = this.getRowConstraints();
        List<ColumnConstraints> columnConstraintsListe = this.getColumnConstraints();

        // Spalten fuer das Spielraster inklsuive Labelspalte erstellen
        for (int spalte = 0; spalte < (breite + 1); spalte++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / gesamtBreite);
            columnConstraints.setMaxWidth(0.0); // Wird laut Doku PercentHeight ignoriert, stimmt aber nicht <3
            columnConstraintsListe.add(columnConstraints);
        }

        // Spalte fuer den Abstand zur rechten Dekoration (Schiffsliste) erstellen
        ColumnConstraints rechterAbstandColumnConstraints = new ColumnConstraints();
        rechterAbstandColumnConstraints.setPercentWidth(100.0 / gesamtBreite * DEKORATIONSABSTAND);
        columnConstraintsListe.add(rechterAbstandColumnConstraints);

        // Spalte fuer die rechte Dekoration (Schiffsliste) erstellen
        ColumnConstraints schiffslisteColumnConstraints = new ColumnConstraints();
        schiffslisteColumnConstraints.setPercentWidth(100.0 / gesamtBreite * RECHTE_DEKORATIONSBREITE);
        columnConstraintsListe.add(schiffslisteColumnConstraints);

        // Reihen fuer das Spielraster inklusive Labelreihe erstellen
        for (int zeile = 0; zeile < (hoehe + 1); zeile++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / gesamtHoehe);
            rowConstraints.setMaxHeight(0.0); // Wird laut Doku PercentHeight ignoriert, stimmt aber nicht <3
            rowConstraintsListe.add(rowConstraints);
        }

        // Reihe fuer den Abstand zur unteren Dekoration erstellen
        RowConstraints untererAbstandRowConstraints = new RowConstraints();
        untererAbstandRowConstraints.setPercentHeight(100.0 / gesamtHoehe * DEKORATIONSABSTAND);
        rowConstraintsListe.add(untererAbstandRowConstraints);

        // Reihe fuer die untere Dekoration erstellen
        RowConstraints untereDekorationRowConstraints = new RowConstraints();
        untereDekorationRowConstraints.setPercentHeight(100.0 / gesamtHoehe * UNTERE_DEKORATIONSHOEHE);
        rowConstraintsListe.add(untereDekorationRowConstraints);


        // Grid aufbauen
        // Gehe durch alle Zeilen des Grids
        for(int zeile = 0; zeile < hoehe + 2; zeile++) {
            if(zeile < hoehe) {
                // Zeilenindizes einfügen
                Label index = new Label("ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(zeile, zeile + 1)); // Label erstellen
                index.getStyleClass().add("index");
                if((breite > 15) || (hoehe > 15)) {
                    index.getStyleClass().add("kleinererIndex");
                }

                this.add(index, 0, zeile + 1);                  // Label ins Grid einfügen
                GridPane.setHalignment(index, HPos.CENTER);     // Label in der Gridzelle horizontal zentrieren

                // Gehe durch alle Spalten des Grids
                for (int spalte = 0; spalte < breite; spalte++) {

                    // Spaltenindizes einfügen
                    index = new Label(Integer.toString(spalte + 1));       // Label erstellen
                    this.add(index,spalte+1,0);                            // Label ins Grid einfügen
                    index.getStyleClass().add("index");
                    if((breite > 15) || (hoehe > 15)) {
                        index.getStyleClass().add("kleinererIndex");
                    }
                    GridPane.setHalignment(index, HPos.CENTER);      // Label in der Gridzelle horizontal zentrieren

                    // Hintergrundraster bauen
                    this.add(new Feld(), spalte + 1, zeile + 1);

                    // Schussfelder erstellen und ins Grid eintragen
                    this.schuesse[zeile][spalte] = new Schuss(spalte, zeile, Schusstyp.KEIN_SCHUSS);
                    this.add(schuesse[zeile][spalte], spalte + 1, zeile + 1);
                }
            }
        }


        // ----- Rahmen für aktives Spielraster -----
        this.rahmen = new Pane();
        this.rahmen.getStyleClass().add("spielfeldrahmen");
        this.add(rahmen, 1, 1, breite, hoehe);


        // ----- "Automatisch Platzieren"-Button -----
        this.automatischPlatzierenButton = new Button("AUTOMATISCH PLATZIEREN");
        this.automatischPlatzierenButton.getStyleClass().add("kleinerButton");
        this.automatischPlatzierenButton.setOnMouseClicked(e -> {
            gui.feuerEvent(EAction.SCHIFFE_AUTOMATISCH_PLATZIEREN, "");
        });
        this.add(
                this.automatischPlatzierenButton,
                1,
                this.getHoehe() + 2,
                this.getBreite(),
                1
        );
        GridPane.setValignment(this.automatischPlatzierenButton, VPos.TOP);
        switch (seite) {
            case LINKS:
                GridPane.setHalignment(automatischPlatzierenButton, HPos.LEFT);
                break;
            case RECHTS:
                GridPane.setHalignment(automatischPlatzierenButton, HPos.RIGHT);
                break;
        }
        this.automatischPlatzierenButton.setMinHeight(0.0);


        // ----- Label mit Spielername und Score -----
        this.spielername = spielername.toUpperCase();
        this.label = new Label();
        this.label.getStyleClass().add("spielername");
        this.aktualisiereScore(0);
        this.add(label, 1, this.hoehe + 2, this.breite, 1);
        GridPane.setValignment(label, VPos.TOP);
        GridPane.setHalignment(label, HPos.RIGHT);
        this.label.setMinHeight(0.0);


        // ----- Schiffsliste -----
        this.schiffsliste = new Schiffsliste(schiffskonfiguration);
        this.add(this.schiffsliste, this.breite + 2, 1, 1, this.hoehe);
    }


    /**
     * Gibt die Begrenzung des Rahmens in Szenenkoordinaten zurück.
     * @return Begrenzung des Rahmens
     */
    public Bounds getRahmenBegrenzung() {
        return rahmen.localToScene(rahmen.getLayoutBounds());
    }


    /**
     * Gibt ein NumberBindung zurueck, welches die Grüße einer Zeller enthält
     * @return Das NumberBinding
     */
    public NumberBinding getZellengroesse() {
        return zellengroesse;
    }

    /**
     * Gibt ein NumberBinding zurueck, welches das Offset zum undekoriertien Spielraster links enthaelt.
     * @return Das NumberBinding
     */
    public NumberBinding getLinkesOffset() {
        return linkesOffset;
    }

    /**
     * Gibt ein NumberBinding zurueck, welches das Offset zum undekorierten Spielraster rechts enthaelt.
     * @return Das NumberBinding
     */
    public NumberBinding getRechtesOffset() {
        return rechtesOffset;
    }

    /**
     * Gibt die Anzahl der Felder in der Hoehe zurück
     * @return Anzahl Felder in der Hoehe
     */
    public int getHoehe() {
        return this.hoehe;
    }

    /**
     * Gibt die Anzahl der Felder in der Breite zurück
     * @return Anzahl Felder in der Breite
     */
    public int getBreite() {
        return this.breite;
    }


    /**
     * Gibt das Seitenverhältnis des Spielrasters inklusive der zusätzlichen Zeilen/Spalten zurueck.
     * @return Anzahl Felder in der Breite
     */
    public double getRatio() {
        return this.ratio;
    }


    /**
     * Gibt den Spielernamen zurück.
     * @return Angezeigter Spielername
     */
    public String getSpielername() {
        return this.spielername;
    }


    //  ------------------------------------------ Spielrasteroperationen ------------------------------------------

    /**
     * Macht Felder des Spielfelds klickbar
     */
    public void scharfstellen() {

        for(Schuss[] zeile : this.schuesse) {
            for(Schuss schuss : zeile) {

                // IV auf aktiviert setzen und CSS-Klasse ergänzen
                schuss.aktivieren();

                // Eventhandler hinzufügen
                schuss.setOnMouseClicked(e -> {
                    if (schuss.aktiviert) {

                        // Abrufbare IVs setzen und Event werfen
                        String spielZugJson = new Gson().toJson(new SpielZug(schuss.x, schuss.y));
                        ShipzGui.getInstance().feuerEvent(EAction.SPIELZUG_GEMACHT, spielZugJson);

                        // Debugausgabe
                        ShipzGui.getInstance().debugNachricht("Feld geklickt: x: " + schuss.x + ", y:" + schuss.y,false);
                    }
                });

            }
        }
    }

    /**
     * Markiert das ESpielfeld und lässt Klicks auf Felder durch.
     */
    public void aktivieren() {
        // MouseEvents durchreichen
        this.rahmen.setMouseTransparent(true);

        // Umstylen
        this.rahmen.getStyleClass().clear(); // Falls Methode zweimal auf gleiche Seite aufgerufen wird
        this.rahmen.getStyleClass().addAll("spielfeldrahmen", "aktiviert");
    }


    /**
     * Entfernt Markierung des ESpielfeld und lässt keine Klicks auf Felder mehr durch.
     */
    public void deaktivieren() {
        // MouseEvents blockieren
        this.rahmen.setMouseTransparent(false);

        // Umstylen
        this.rahmen.getStyleClass().clear(); // Falls Methode zweimal auf gleiche Seite aufgerufen wird
        this.rahmen.getStyleClass().add("spielfeldrahmen");
    }


    /**
     * Zeigt oder versteckt den AutomatischPlatzierenButton
     * @param sichtbar TRUE, wenn sichtbar
     */
    public void zeigeAutomatischPlatzierenButton(boolean sichtbar) {
        this.automatischPlatzierenButton.setVisible(sichtbar);
    }


    public void setzeAutomatischPlatzierenButtonAktiviert(boolean aktiviert) {
        this.automatischPlatzierenButton.setDisable(!aktiviert);
    }

    /**
     * Zeigt oder versteckt das Label mit Score und Spielername.
     * @param sichtbar TRUE, wenn sichtbar
     */
    public void zeigeLabel(boolean sichtbar) {
        this.label.setVisible(sichtbar);
    }


    /**
     * Zeigt oder versteckt die Schiffsliste.
     * @param sichtbar TRUE, wenn sichtbar
     */
    public void zeigeSchiffsliste(boolean sichtbar) {
        this.schiffsliste.setVisible(sichtbar);
    }


    /**
     * Aendert den angezeigten Score des Spielfelds
     * @param neuerScore    Neuer angezeigter Score
     */
    public void aktualisiereScore(int neuerScore) {
        this.label.setText(this.spielername + " // SCORE " + neuerScore);
    }



    //  ------------------------------------------ Schussoperationen ------------------------------------------

    /**
     * Setzt einen Schuss in das ESpielfeld
     * @param x     X-Koordinate
     * @param y     Y-Koordinate
     * @param typ   Typ des Schusses
     */
    public void setzeSchuss(int x, int y, Schusstyp typ) {

        this.aendereSchusstyp(x,y,typ);
        this.schuesse[y][x].deaktivieren();

        // Schuss animieren
        FadeTransition fade = new FadeTransition(Duration.millis(150), schuesse[y][x]);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }


    /**
     * Entfernen eines Schusses aus dem ESpielfeld
     * @param x     x-Koordinate
     * @param y     y-Koordinate
     */
    public void entferneSchuss(int x, int y) {

        // Entfernung animieren
        FadeTransition fade = new FadeTransition(Duration.millis(150), schuesse[y][x]);
        fade.setFromValue(1.0);
        fade.setToValue(0);
        fade.play();
        fade.setOnFinished(e -> {

            // Schussttyp zurücksetzen
            this.aendereSchusstyp(x,y,Schusstyp.KEIN_SCHUSS);

            // Feld wieder aktivieren
            schuesse[y][x].aktivieren();

            // Feld wieder sichtbar machen
            schuesse[y][x].setOpacity(1.0);

        });
    }


    /**
     * Ändert den Typ eines Schusses im ESpielfeld
     * @param x     x-Koordinate
     * @param y     y-Koordinate
     * @param typ   Typ des Schusses
     */
    public void aendereSchusstyp(int x, int y, Schusstyp typ) {

        // Alte Klassen entfernen
        schuesse[y][x].getStyleClass().remove("treffer");
        schuesse[y][x].getStyleClass().remove("trefferVersenkt");
        schuesse[y][x].getStyleClass().remove("keinTreffer");

        // Neue Klasse setzen
        switch (typ) {
            case TREFFER:
                schuesse[y][x].getStyleClass().add("treffer");
                break;
            case KEIN_TREFFER:
                schuesse[y][x].getStyleClass().add("keinTreffer");
                break;
            case TREFFER_VERSENKT:
                schuesse[y][x].getStyleClass().add("trefferVersenkt");
                break;
        }
    }

    /**
     * Schussgrafik im Spielraster, kann geklickt werden
     */
    private class Schuss extends Pane {

        private final int x;
        private final int y;                   // Feldkoordination
        private boolean aktiviert = false;  // Deaktivierte Felder können nicht mehr geklickt werden

        /**
         * Konstruktor Schuss
         * @param x X-Koordinate
         * @param y X-Koordinate
         * @param typ Typ des Schusses
         */
        private Schuss(int x, int y, Schusstyp typ) {

            // IVs setzen
            this.x = x;
            this.y = y;
            this.aktiviert = false;

            // Klasse fuer Hintergrundgrafik setzen
            switch (typ) {
                case KEIN_SCHUSS:
                    // Nichts machen
                    break;
                case KEIN_TREFFER:
                    this.getStyleClass().add("keinTreffer");
                    break;

                case TREFFER:
                    this.getStyleClass().add("treffer");
                    break;

                case TREFFER_VERSENKT:
                    this.getStyleClass().add("trefferVersenkt");
                    break;
            }
        }

        // CSS-Typ-Selektor für innere Klasse korrigieren
        @Override
        public String getTypeSelector() {
            return "Schuss";
        }

        /**
         * Deaktiviert ein Schussfeld,
         * sodass es nicht mehr geklickt werden kann.
         */
        void deaktivieren() {
            aktiviert = false;
            this.getStyleClass().remove("aktiviert");
        }


        /**
         * Aktiviert ein Schussfeld,
         * sodass es geklickt und gehovert werden kann.
         */
        void aktivieren() {
            aktiviert = true;
            this.getStyleClass().add("aktiviert");
        }

    } // Ende Class Schuss



    /**
     * Klasse Feld
     * Feld um Raster im Hintergrund zu erzeugen
     */
    private class Feld extends Pane {

        // CSS-Typ-Selektor für innere Klasse korrigieren
        @Override
        public String getTypeSelector() {
            return "Feld";
        }

    } // Ende Class Feld


    //  ------------------------------------------ Schiffsoperationen ------------------------------------------

    public void setzeSchiff(int id, int x, int y, ESchiffsTyp typ, int rotation, ESchiffsZustand ESchiffsZustand) {

        // Schiffsobjekt erstellen und in Map speichern
        Schiff schiff = new Schiff(this, id, x, y, typ, rotation, ESchiffsZustand);
        this.schiffe.put(id,schiff);

        // Schiff animieren
        FadeTransition fade = new FadeTransition(Duration.millis(150), schiff);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        // Schiff unter Berücksichtigung von Rotation und Größe ins Spielraster setzen
        switch (rotation) {
            case 0:
                this.add(schiff,x+1,y+1,schiff.getBreite(),schiff.getLaenge());
                break;
            case 90:
                this.add(schiff,x+1,y+1,schiff.getLaenge(),schiff.getBreite());
                break;
            case 180:
                this.add(schiff,x+1,y+1,schiff.getBreite(),schiff.getLaenge());
                break;
            case 270:
                this.add(schiff,x+1,y+1,schiff.getLaenge(),schiff.getBreite());
                break;
            default:
                ShipzGui.getInstance().debugNachricht("Nicht erlaubter Rotationswert für setzeSchiff(): " + rotation,true);
        }

        // In Schiffliste Typ entsprechend markieren
        if (ESchiffsZustand == ESchiffsZustand.VERSENKT) {
            this.schiffsliste.versenkeSchiff(id);
        } else {
            this.schiffsliste.schiffAuftauchen(id);
        }

        if(
            (ESchiffsZustand != ESchiffsZustand.VERZIEHBAR) &&
            (ESchiffsZustand != ESchiffsZustand.VERZIEHBAR_FALSCH_POSITIONIERT)
        ) {

            // Schuesse vor die Schiffstyp setzen
            // Macht Hover und Focus schoen... (Klickibunti...)
            for(Schuss[] schussZeile : this.schuesse) {
                for(Schuss schuss : schussZeile) {
                    schuss.toFront();
                }
            }

            // Rahmen wirder nach ganz ganz oben
            this.rahmen.toFront();

            // Animation starten
            fade.play();
        }
    }


    /**
     * Entfernt ein Schiff aus dem Spielraster
     * @param id           ID des Schiffs
     */
    public void entferneSchiff(int id) {

        if(this.schiffe.containsKey(id)) {

            // Schiff holen
            Schiff schiff = this.schiffe.get(id);

            // Aus der Liste der blinkenden Schiffe entfernen
            // Wenn drin...
            schiff.entferneAusFalschPositionierteSchiffe();

            // Aus dem GridPane entfernen
            this.getChildren().remove(schiff);

            // Aus der Map entfernen
            if (this.schiffe.containsKey(id)) {
                this.schiffe.remove(id);

                // Wenn ein Schiff entfernt wird muss es aufgetaucht werden
                this.schiffsliste.schiffAuftauchen(id);
            }
        }

    }


    /**
     * Entfernt alle Schiff aus dem Spielraster
     */
    public void entferneAlleSchiffe() {

        // Alle Schiffe aus dem Grid entfernen
        Set<Map.Entry<Integer,Schiff>> alleSchiffe = this.schiffe.entrySet();
        for(Map.Entry<Integer, Schiff> schiffsEntry : alleSchiffe) {
            this.getChildren().remove(schiffsEntry.getValue());

            // Entsprechend das zu entfernende Schiff auftauchen
            this.schiffsliste.schiffAuftauchen(schiffsEntry.getKey());
        }
        // Alle Schiffe löschen
        this.schiffe.clear();
    }


    /**
     * Verschiebt ein Schiff in einem Spielraster
     * @param id           ID des Schiffs
     * @param x            X-Koordinate, auf die das Schiff verschoben werden soll
     * @param y            Y-Koordinate, auf die das Schiff verschoben werden soll
     */
    public void verschiebeSchiff(int id, int x, int y) {

        Schiff schiff = this.schiffe.get(id);

        // Schiff versetzen
        GridPane.setColumnIndex(schiff,x+1);
        GridPane.setRowIndex(schiff,y+1);
    }


    /**
     * Ändert du Zustand eines Schiffs
     * @param id                ID des Schiffes
     * @param ESchiffsZustand    Neuer Zustand des Schiffes
     */
    public void setzeSchiffszustand(int id, ESchiffsZustand ESchiffsZustand) {
        if(this.schiffe.containsKey(id)) {
            this.schiffe.get(id).setzeSchiffszustand(ESchiffsZustand);

            // In Schiffliste Typ entsprechend markieren
            if (ESchiffsZustand == ESchiffsZustand.VERSENKT) {
                this.schiffsliste.versenkeSchiff(id);
            } else {
                this.schiffsliste.schiffAuftauchen(id);
            }

        } else {
            ShipzGui.getInstance().debugNachricht("setzeSchiffszustand(): Es wurden vorher keine Schiffe in das ESpielfeld geschrieben.", true);
        }
    }

    /**
     * Setzt alle Schiff aus dem Spielraster auf UNGETROFFEN
     */
    public void setzteAlleSchiffeAufUngetroffen() {

        Set<Map.Entry<Integer,Schiff>> alleSchiffe = this.schiffe.entrySet();
        for(Map.Entry<Integer, Schiff> schiffsEntry : alleSchiffe) {
            schiffsEntry.getValue().setzeSchiffszustand(ESchiffsZustand.UNGETROFFEN);

            // Entsprechend das auf ungetroffen zu setztende Schiff auftrauchen
            this.schiffsliste.schiffAuftauchen(schiffsEntry.getKey());
        }
    }


} // Ende Class Spielraster