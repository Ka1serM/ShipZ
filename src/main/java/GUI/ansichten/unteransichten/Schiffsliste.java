package GUI.ansichten.unteransichten;

import Administration.Enums.ESchiffsTyp;
import GUI.ShipzGui;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import java.util.*;

class Schiffsliste extends Pane {
    /** Abstand zwischen zwei Schiffen in Prozent der Höhe einer Zelle */
    private static final double SCHIFFSABSTAND = 0.4;

    /** Zum Themepfad lesen */
    private static final ShipzGui gui = ShipzGui.getInstance();

    /** Alle Schiff in der Liste */
    private final List<Schiff.SchiffslisteSchiff> schiffe;


    Schiffsliste(HashMap<ESchiffsTyp, Integer> schiffskonfiguration) {
        // GridPane in dem alle Schiffe gespeichert werden.
        GridPane listenGrid = new GridPane();

        // Aus Konfiguration alle Schiffslistenschiffe erstellen
        this.schiffe = new LinkedList<>();
        int idx = 0;
        for (Map.Entry<ESchiffsTyp, Integer> entry : schiffskonfiguration.entrySet()) {
            ESchiffsTyp typ = entry.getKey();
            Integer anzahl = entry.getValue();
            for(int i = 0; i < anzahl; i++) {
                this.schiffe.add(Schiff.getSchiffslisteschiff(typ, idx));
                idx++;
            }
            // Schiffslistenschiff mit Typ und Id erstellen
        }

        // Schiffsliste sortieren, sodass das breiteste Schiff ganz oben ist
        Collections.sort(this.schiffe);

        // Breite entspricht der maximalen Breite aller Schiffe
        double breite = 1;
        for (Schiff.SchiffslisteSchiff schiff : this.schiffe) {
            if (schiff.breite > breite) {
                breite = schiff.breite;
            }
        }

        // Höhe entspricht der summierten Höhe aller Schiffe plus Abstände
        double hoehe = 0;
        for (Schiff.SchiffslisteSchiff schiff : this.schiffe) {
            hoehe += schiff.hoehe;
        }
        hoehe += (this.schiffe.size() > 0 ? this.schiffe.size() : 0) * SCHIFFSABSTAND;


        // Seitenverhaeltnis berechnen
        double ratio = breite / hoehe;

        // Ratio des Listengrids fixieren
        listenGrid.prefWidthProperty().bind(Bindings.min(super.widthProperty().divide(ratio), super.heightProperty()).multiply(ratio));
        listenGrid.prefHeightProperty().bind(Bindings.min(super.widthProperty().divide(ratio), super.heightProperty()));
        listenGrid.maxWidthProperty().bind(Bindings.min(super.widthProperty().divide(ratio), super.heightProperty()).multiply(ratio));
        listenGrid.maxHeightProperty().bind(Bindings.min(super.widthProperty().divide(ratio), super.heightProperty()));

        // Reihen und Spaltenconstraints erstenne
        List<RowConstraints> rowConstraintsListe = listenGrid.getRowConstraints();
        List<ColumnConstraints> columnConstraintsListe = listenGrid.getColumnConstraints();

        // Breite und Höhe der Schiffszellen in Prozent berechnen
        double schiffszellenProzentBreite = 100.0 / breite;
        double schiffszellenProzentHoehe = schiffszellenProzentBreite * ratio;
        double schiffsabstandProzentHoehe = schiffszellenProzentHoehe * SCHIFFSABSTAND;

        // ColumnConstraints für alle Spalten erstellen
        for (int spalte = 0; spalte < breite; spalte++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(schiffszellenProzentBreite);
            columnConstraintsListe.add(columnConstraints);
        }

        // Alle Schiffe in die Liste einfügen
        int reihe = 0;
        for (Schiff.SchiffslisteSchiff schiff : schiffe) {

            // RowConstraints für das Schiff je nach Höhe hinzufügen
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(schiffszellenProzentHoehe * schiff.hoehe);
            rowConstraintsListe.add(rowConstraints);

            // Wenn das aktuelle Schiff nicht das letzte ist, zusätzlichen Abstand einfügen
            if (schiff != schiffe.get(schiffe.size() - 1)) {
                RowConstraints abstandsConstraints = new RowConstraints();
                abstandsConstraints.setPercentHeight(schiffsabstandProzentHoehe);
                rowConstraintsListe.add(abstandsConstraints);
            }

            listenGrid.add(schiff, 0, reihe, schiff.breite, 1);
            reihe += 2;
        }

        // Listengrid sich selbst hinzufuegen
        super.getChildren().add(listenGrid);
    }

    /**
     * Markiert das Schiff mit gegebener Id als versenkt.
     * @param id Id des als versenkt zu markierden Schiffes
     */
    void versenkeSchiff(int id) {
        for (Schiff.SchiffslisteSchiff schiff : this.schiffe) {
            if (schiff.id == id) {
                schiff.setzteVersenkt(true);
                return;
            }
        }
    }

    /**
     * Markiert das Schiff mit gegebener Id als nicht versenkt.
     * @param id Id des als nicht versenkt zu markierden Schiffes
     */
    void schiffAuftauchen(int id) {
        for (Schiff.SchiffslisteSchiff schiff : this.schiffe) {
            if (schiff.id == id) {
                schiff.setzteVersenkt(false);
                return;
            }
        }
    }
}
