package GUI.ansichten;

import Administration.Enums.EAction;
import Administration.Enums.ESchiffsTyp;
import GUI.ansichten.unteransichten.IntSpinner;
import GUI.ansichten.unteransichten.LabelMitNodeVertikal;
import GUI.ansichten.unteransichten.SchiffSpinner;
import GUI.ansichten.unteransichten.Spacer;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Ansicht zum Konfigurieren der Anzahl an Schiffstypen,
 * Spielfeldmasse und Schiffsberuehrung erlaubt.
 */
public class Spielkonfiguration extends Ansicht {


    // ---------------------------- IV ----------------------------
    /**
     * Zeigt die Auslastung des Spielfelds mit Schiffen an.
     * 100% auf der Bar entsprechen 30% Flaeche auf dem ESpielfeld
     * bedeckt mit Schiffen
     */
    private final ProgressBar maxSchiffeBar;

    /** Alle SchiffSpinner indexiert mit dem Labelnamen */
    private final Set<SchiffSpinner> alleSchiffSpinner;

    /** Spinner fuer Spielfeldmasse */
    private final IntSpinner spielfeldbreite;
    private final IntSpinner spielfeldhoehe;

    /** Zur naechsten Ansicht, inaktiv bei 0 Schiffen */
    private final Button weiterButton;

    /** Bei aktiviert duerfen sich Schiffen beim Drag and Drop beruehren */
    private final CheckBox schiffsBeruehrungErlaubtCheckBox;

    /** Anzahl aller mit Schiffen belegten Felder */
    private int anzahlSchiffsfelder;

    /**
     * Maximale Anzahl an Feldern,
     * die bei eingestellten Spielfeldmassen mit Schiffen belegt sein
     * */
    private int maxSchiffsfelder;

    /** Anzahl an Feldern im ESpielfeld, berechnet aus Spielfeldbreite und -Hoehe */
    private int anzahlFelderSpielraster;



    /**
     * Erstellen eine Ansicht zur Spielkonfiguration
     */
    public Spielkonfiguration() {


        // -------------------- Buttonleiste --------------------
            Button zurueckButton = new Button("ZURÜCK");
            zurueckButton.setOnMouseClicked(e -> wirfMenueZurueck());
            this.buttonleiste.add(zurueckButton);



        // -------------------- Raster fuer Content --------------------
            GridPane raster = new GridPane();
            RowConstraints titelZeile = new RowConstraints();
                titelZeile.setPercentHeight(16.0);

            RowConstraints inhaltZeile1 = new RowConstraints();
                inhaltZeile1.setPercentHeight(12.0);

            RowConstraints inhaltZeile2 = new RowConstraints();
                inhaltZeile2.setPercentHeight(14.0);

            RowConstraints inhaltZeile3 = new RowConstraints();
                inhaltZeile3.setPercentHeight(6.0);

            RowConstraints inhaltZeile4 = new RowConstraints();
                inhaltZeile4.setPercentHeight(16.0);

            RowConstraints inhaltZeile5 = new RowConstraints();
                inhaltZeile5.setPercentHeight(18.0);

            RowConstraints inhaltZeile6 = new RowConstraints();
                inhaltZeile6.setPercentHeight(10.0);

            raster.getRowConstraints().addAll(
                    titelZeile,
                    inhaltZeile1,
                    inhaltZeile2,
                    inhaltZeile3,
                    inhaltZeile4,
                    inhaltZeile5,
                    inhaltZeile6
            );
            ColumnConstraints spalte1 = new ColumnConstraints();
                spalte1.setPercentWidth(33);

            ColumnConstraints gap = new ColumnConstraints();
                gap.setPercentWidth(7.0);

            ColumnConstraints spalte2 = new ColumnConstraints();
                spalte2.setPercentWidth(30.0);

            ColumnConstraints spalte3 = new ColumnConstraints();
                spalte3.setPercentWidth(30.0);

            raster.getColumnConstraints().addAll(spalte1,gap,spalte2,spalte3);

            this.add(raster,1,3,3,1);

            
            
        // -------------------- Presets --------------------
            HashMap<ESchiffsTyp, Integer> schiffskonfiguration = new HashMap<>();
            schiffskonfiguration.put(ESchiffsTyp.PATROL_BOAT, 1);
            schiffskonfiguration.put(ESchiffsTyp.SUBMARINE, 1);
            schiffskonfiguration.put(ESchiffsTyp.DESTROYER, 1);
            schiffskonfiguration.put(ESchiffsTyp.BATTLESHIP, 1);
            schiffskonfiguration.put(ESchiffsTyp.CARRIER, 1);
            
            Button presetKlassischButton = new Button("KLEINES SPIELFELD");
            presetKlassischButton.setOnMouseClicked(e -> {
                presetLaden(10,10,schiffskonfiguration, false);
            });

            Button presetModernButton = new Button("MITTLERES SPIELFELD");
            presetModernButton.setOnMouseClicked(e -> {
                presetLaden(11,11,schiffskonfiguration, false);
            });

            Button presetGrosseSeeschlachtButton = new Button("GROSSES SPIELFELD");
            presetGrosseSeeschlachtButton.setOnMouseClicked(e -> {
                presetLaden(20,20,schiffskonfiguration, false);
            });

            HBox presetButtons = new HBox();
            presetButtons.getChildren().addAll(
                    presetKlassischButton,
                    presetModernButton,
                    presetGrosseSeeschlachtButton
            );
            presetButtons.spacingProperty().bind(gui.getSchriftgroesse().divide(2));
            presetButtons.setAlignment(Pos.CENTER_LEFT);
            raster.add(presetButtons,0,1,3,1);
            

        // -------------------- Ueberschrift --------------------
            Text ueberschrift = new Text("SPIELRUNDE KONFIGURIEREN");
            ueberschrift.getStyleClass().add("ueberschrift");
            raster.add(ueberschrift,0,0);




        // -------------------- Spinner Spielfeldmasse --------------------
            LabelMitNodeVertikal<IntSpinner> spielfeldbreiteSpinnerMitLabel = new LabelMitNodeVertikal<>(
                   "SPIELFELDBREITE",
                    this.spielfeldbreite = new IntSpinner(10,20,11)
            );
            this.spielfeldbreite.addListener(e -> aktualisiereAnsicht());

            LabelMitNodeVertikal<IntSpinner> spielfeldhoeheSpinnerMitLabel = new LabelMitNodeVertikal<>(
                    "SPIELFELDHÖHE",
                    this.spielfeldhoehe = new IntSpinner(10,20,11)
            );
            this.spielfeldhoehe.addListener(e -> aktualisiereAnsicht());


            HBox spielfeldmasse = new HBox();
            spielfeldmasse.getChildren().addAll(
                    spielfeldbreiteSpinnerMitLabel,
                    spielfeldhoeheSpinnerMitLabel
            );
            spielfeldmasse.spacingProperty().bind(gui.getSchriftgroesse().multiply(2));
            raster.add(spielfeldmasse,0,4,1,1);




        // -------------------- CheckBox BeruehrungErlaubt --------------------
            this.schiffsBeruehrungErlaubtCheckBox = new CheckBox();
            this.schiffsBeruehrungErlaubtCheckBox.setSelected(true);
            this.schiffsBeruehrungErlaubtCheckBox.setText("SCHIFFSBERÜHRUNG ERLAUBT");
            raster.add(schiffsBeruehrungErlaubtCheckBox,0,5,1,1);
            GridPane.setValignment(schiffsBeruehrungErlaubtCheckBox, VPos.TOP);




        // -------------------- Schiffsspinner --------------------
            this.alleSchiffSpinner = new HashSet<>(8);

            // Standardschiffe

                // EINER
                SchiffSpinner einerSpinner = new SchiffSpinner(0,9,0, ESchiffsTyp.PATROL_BOAT);
                this.alleSchiffSpinner.add(einerSpinner);
                LabelMitNodeVertikal<SchiffSpinner> einerSchiffSpinnerMitLabel = new LabelMitNodeVertikal<>(
                        "EINER", einerSpinner
                );
        
                // ZWEIER
                SchiffSpinner zweierSpinner = new SchiffSpinner(0,9,1, ESchiffsTyp.SUBMARINE);
                this.alleSchiffSpinner.add(zweierSpinner);
                LabelMitNodeVertikal<SchiffSpinner> zweierSchiffSpinnerMitLabel = new LabelMitNodeVertikal<>(
                    "ZWEIER", zweierSpinner
                );

                // DREIER
                SchiffSpinner dreierSpinner = new SchiffSpinner(0,9,1, ESchiffsTyp.DESTROYER);
                this.alleSchiffSpinner.add(dreierSpinner);
                LabelMitNodeVertikal<SchiffSpinner> dreierSchiffSpinnerMitLabel = new LabelMitNodeVertikal<>(
                        "DREIER", dreierSpinner
                );

                // VIERER
                SchiffSpinner viererSpinner = new SchiffSpinner(0,9,1, ESchiffsTyp.BATTLESHIP);
                this.alleSchiffSpinner.add(viererSpinner);
                LabelMitNodeVertikal<SchiffSpinner> viererSchiffSpinnerMitLabel = new LabelMitNodeVertikal<>(
                        "VIERER",
                        viererSpinner
                );

                // FUENFER
                SchiffSpinner fuenferSpinner = new SchiffSpinner(0,9,1, ESchiffsTyp.CARRIER);
                this.alleSchiffSpinner.add(fuenferSpinner);
                LabelMitNodeVertikal<SchiffSpinner> fuenferSchiffSpinnerMitLabel = new LabelMitNodeVertikal<>(
                        "FÜNFER",
                        fuenferSpinner
                );

                HBox standardschiffe = new HBox();
                standardschiffe.getChildren().addAll(
                        zweierSchiffSpinnerMitLabel,
                        new Spacer(),
                        dreierSchiffSpinnerMitLabel,
                        new Spacer(),
                        viererSchiffSpinnerMitLabel,
                        new Spacer(),
                        fuenferSchiffSpinnerMitLabel
                );
                standardschiffe.setAlignment(Pos.BOTTOM_LEFT);
                raster.add(standardschiffe,2,4,2,1);
                
                HBox sonderschiffe = new HBox();
                sonderschiffe.getChildren().addAll(
                        einerSchiffSpinnerMitLabel
                );
                
                alleSchiffSpinner.forEach(spinner -> {
                    spinner.addListener(e -> {
                        aktualisiereAnsicht();
                    });
                });
                raster.add(sonderschiffe,2,5,2,1);

        // -------------------- Progressbar fuer Spielfeldauslastung --------------------
            Label maxSchiffeBarLabel = new Label("SPIELFELDAUSLASTUNG");
            this.maxSchiffeBar = new ProgressBar(0.5);
            //maxSchiffeBar.setPrefWidth(620.0);
            maxSchiffeBar.prefWidthProperty().bind(raster.widthProperty().multiply(0.6));


            VBox maxSchiffeBarBox = new VBox();
            maxSchiffeBarBox.getChildren().addAll(maxSchiffeBarLabel, this.maxSchiffeBar);
            maxSchiffeBarBox.setSpacing(10.0);
            maxSchiffeBarBox.setAlignment(Pos.BOTTOM_LEFT);
            raster.add(maxSchiffeBarBox,2,2,2,1);




        // -------------------- Weiter Button --------------------
            this.weiterButton = new Button("WEITER");
            this.weiterButton.getStyleClass().add("grosserButton");
            this.weiterButton.setOnMouseClicked(e -> spielkonfigurationWerfen());
            raster.add(this.weiterButton,3,6);
            GridPane.setHalignment(this.weiterButton, HPos.RIGHT);




        // -------------------- Anleitung --------------------
            Label anleitung = new Label(
                    "STELLE DEINE FLOTTE ZUSAMMEN UND WÄHLE EINE SCHLACHTFELDGRÖSSE. " +
                        "DU KANNST AUSSERDEM ENTSCHEIDEN, OB DIE SCHIFFE SICH IM SPIEL BERÜHREN DÜRFEN."
            );
            anleitung.getStyleClass().add("kleiner");
            anleitung.lineSpacingProperty().bind(gui.getSchriftgroesse().divide(4));
            anleitung.setWrapText(true);
            //raster.add(anleitung,0,2,1,1);
            GridPane.setValignment(anleitung, VPos.BOTTOM);
            
        aktualisiereAnsicht();

    } // Ende Konstruktor


    /**
     * Summiert die Anzahl an von Schiffen belegten Feldern.
     */
    private void errechneAnzahlSchiffsfelder() {
        this.anzahlSchiffsfelder = 0;
        this.alleSchiffSpinner.forEach(spinner -> {
            this.anzahlSchiffsfelder += spinner.getAnzahlFelderInsgesamt();
        });
    }


    /**
     * Aktualisiert die ProgressBar
     */
    private void aktualiereMaxSchiffeBar() {
        this.maxSchiffsfelder = (this.anzahlFelderSpielraster * 30) / 100;
        this.maxSchiffeBar.setProgress((double)this.anzahlSchiffsfelder / maxSchiffsfelder);
    }


    /**
     * Deaktiviert die ErniedrigenButtons der Spielfeldmasse,
     * wenn zu viele Schiffe im ESpielfeld sind.
     */
    private void deaktiviereErniedrigenhenButtons() {

        int anzahlFelderSpielrasterBreiteKleiner = (this.spielfeldbreite.getValue() - 1) * this.spielfeldhoehe.getValue();
        int anzahlFelderSpielrasterHoeheKleiner = this.spielfeldbreite.getValue() * (this.spielfeldhoehe.getValue() - 1);

        int maxSchiffsfelderBreiteKleiner = (anzahlFelderSpielrasterBreiteKleiner * 30) / 100;
        int maxSchiffsfelderHoeheKleiner = (anzahlFelderSpielrasterHoeheKleiner * 30) / 100;


        this.spielfeldbreite.setzeErniedrigenButtonAktiviert(maxSchiffsfelderBreiteKleiner >= this.anzahlSchiffsfelder);

        this.spielfeldhoehe.setzeErniedrigenButtonAktiviert(maxSchiffsfelderHoeheKleiner >= this.anzahlSchiffsfelder);
    }


    /**
     * Deaktiviert die Erhoehen-Buttons der SchiffSpinner,
     * bei welchen ein weiteres Schiff nicht mehr ins ESpielfeld passt.
     */
    private void deaktiviereErhoehenButtons() {

        int nochVerfuegbareAnzahlSchiffsfelder = this.maxSchiffsfelder - this.anzahlSchiffsfelder;

        this.alleSchiffSpinner.forEach(spinner -> {
            spinner.setzeErhoehenButtonAktiviert(spinner.getAnzahlFelder() <= nochVerfuegbareAnzahlSchiffsfelder);
        });
    }


    /**
     * Errechnet aus Spielfeldbreite und -Hoehe die Anzahl an Feldern im ESpielfeld
     * */
    private void errechneAnzahlFelderSpielraster() {
        this.anzahlFelderSpielraster = this.spielfeldbreite.getValue() * this.spielfeldhoehe.getValue();
    }


    /**
     * Deaktiviert den Weiter-Button, wenn kein Schiff konfiguriert ist.
     * Ist mindestens ein Schiff konfiguriert, wird der Button aktiviert.
     */
    private void aktualisiereWeiterButtonAktiviert() {
        this.weiterButton.setDisable(anzahlSchiffsfelder <= 0);
    }
    
    
    /**
     * Aktualisiert alle Spinnerbuttons , den Weiter-Button und die ProgressBar.
     */
    private void aktualisiereAnsicht() {
        errechneAnzahlFelderSpielraster();
        errechneAnzahlSchiffsfelder();
        aktualiereMaxSchiffeBar();

        deaktiviereErhoehenButtons();
        deaktiviereErniedrigenhenButtons();
        aktualisiereWeiterButtonAktiviert();
    }


    /**
     * Setzt in der Ansicht selektierte DAten als abrufbare Daten in ShipzGui und feuert ein Event.
     */
    private void spielkonfigurationWerfen() {

        // Map mit Schiffen zusammenbauen
        HashMap<ESchiffsTyp, Integer> schiffszusammensetzung = new HashMap<>();
        for(SchiffSpinner spinner : this.alleSchiffSpinner) {
            ESchiffsTyp typ = spinner.getTyp();
            schiffszusammensetzung.put(typ, schiffszusammensetzung.getOrDefault(typ, 0) + spinner.getValue());
        }

        // Abrufbare Daten setzen
        gui.setSchiffsberuehrungErlaubt(this.schiffsBeruehrungErlaubtCheckBox.isSelected());
        gui.setSchiffszusammensetzung(schiffszusammensetzung);
        gui.setzeSpielfeldgroesse(this.spielfeldbreite.getValue(), this.spielfeldhoehe.getValue());

        // Bumm
        gui.feuerEvent(EAction.SPIELFELD_KONFIGURATION_ABRUFBAR, "");
    }


    /**
     * Setzt Werte in die Spinner und die Checkbox.
     * @param spielfeldbreite           Breite des Spielfelds
     * @param spielfeldhoehe            Hoehe des Spielfelds
     * @param schiffskonfiguration      Long mit Schiffen
     * @param schiffsberuehrungErlaubt  True, wenn sich Schiffe beruehren duerfen, sonst False
     */
    private void presetLaden(int spielfeldbreite, int spielfeldhoehe, HashMap<ESchiffsTyp, Integer> schiffskonfiguration, boolean schiffsberuehrungErlaubt) {
        
        this.spielfeldbreite.setValue(spielfeldbreite);
        this.spielfeldhoehe.setValue(spielfeldhoehe);
        this.schiffsBeruehrungErlaubtCheckBox.setSelected(schiffsberuehrungErlaubt);

        this.alleSchiffSpinner.forEach(spinner -> spinner.setValue(schiffskonfiguration.get(spinner.getTyp())));

        aktualisiereAnsicht();

    }

    /**
     * Gibt Stelle aus einem long zuruck.
     * @param longZahl  Long-Zahl, aus welcher eine Stelle extrahiert werden soll
     * @param stelle    Stelle
     * @return          Stelle aus long
     */
    private static int stelleAusLong(long longZahl, int stelle) {
        return (int)((longZahl / Math.pow(10, stelle-1)) % 10);
    }
}
