package GUI.ansichten;

import Administration.Enums.EAction;
import GUI.ESpielfeld;
import GUI.ansichten.unteransichten.FixedRatioPane;
import GUI.ansichten.unteransichten.Spacer;
import GUI.ansichten.unteransichten.Spielraster;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Ansicht zum Anordnen der Schiffe
 */
public class SchiffeAnordnen extends Ansicht {

    // IV
    /** FixedRationPanes */
    private FixedRatioPane fixedRatioPaneLinks, fixedRatioPaneRechts;

    /** Genutztes Spielraster */
    private Spielraster spielraster;

    /** Auf welcher Seite werden die Schiffe angeordnet */
    private ESpielfeld anordnenAufESpielfeld;

    /** Schließt Drag and Drop ab */
    private Button schlachtBeginnenButton;

    /** Names des Spielers */
    private SimpleStringProperty spielername = new SimpleStringProperty();

    /** Genutzt um entsprechnenden Beschreibungstext anzuzeigen */
    private SimpleBooleanProperty schiffsberuehrungErlaubt = new SimpleBooleanProperty();

    /** VBox welche die Anleitung enthaelt */
    private VBox anleitung;


    /**
     * Erstellt eine Ansicht zum Anordnen der Schiffe.
     * @param anordnenAufESpielfeld Die Spielfeldseite auf der das Raster angezeigt und die Schiffe angeordnet werden sollen.
     */
    public SchiffeAnordnen(ESpielfeld anordnenAufESpielfeld) {

        // IV setzen fuer aktualisieren()
        this.anordnenAufESpielfeld = anordnenAufESpielfeld;


        // -------------- Buttons --------------
        // "Schlacht beginnen"-Button
        this.schlachtBeginnenButton = new Button("SCHLACHT BEGINNEN");
        this.schlachtBeginnenButton.getStyleClass().add("grosserButton");
        this.schlachtBeginnenButton.setOnMouseClicked(e -> {
            gui.feuerEvent(EAction.SCHLACHT_BEGINNEN, "");
        });
        this.schlachtBeginnenButton.setDisable(true);


        // "Abbrechen"-Button
        Button abbrechenButton = new Button("ABBRECHEN");
        abbrechenButton.setOnMouseClicked(e -> {
            gui.feuerEvent(EAction.SPIELRUNDE_ABBRECHEN, "");
        });

        // "Zurück"-Button
        Button zurueckButton = new Button("ZURÜCK");
        zurueckButton.setDisable(true);

        // Zur Buttonleiste hinzufügen
        this.buttonleiste.addAll(zurueckButton, new Spacer(), abbrechenButton);
        this.buttonleiste.toBack(); // Damit Schiffe beim Drag&Drop nicht unter der Leiste hängen bleiben können


        // Fixexed Ratio Panes erstellen (Platzhalterratio 1)
        this.fixedRatioPaneLinks = new FixedRatioPane(1);
        this.add(this.fixedRatioPaneLinks,1,3);
        this.fixedRatioPaneRechts = new FixedRatioPane(1);
        this.add(this.fixedRatioPaneRechts,3,3);

        // Anleitung erstellen
        Text anleitungsUeberschrift = new Text();
        anleitungsUeberschrift.textProperty().bind(Bindings.concat(
                "\n",
                spielername,
                ":",
                Bindings.when(spielername.length().greaterThan(10)).then("\n").otherwise(" "),
                "PLATZIERE DEINE SCHIFFE!\n"
        ));
        anleitungsUeberschrift.getStyleClass().add("ueberschrift");

        Text anleitungstext = new Text();
        anleitungstext.textProperty().bind(Bindings.concat(
                "\nORDNE DEINE SCHIFFE AUF DEM SCHLACHTFELD PER DRAG AND DROP AN. " +
                        "UM EIN SCHIFF UM 90 GRAD ZU ROTIEREN, KLICKE AUF DAS JEWEILIGE SCHIFF. " +
                        "ALTERNATIV KANNST DU DIE SCHIFFE AUCH AUTOMATISCH PLATZIEREN, INDEM DU " +
                        "AUF \"AUTOMATISCH PLATZIEREN\" KLICKST.\n" +
                        "\n",
                Bindings.when(schiffsberuehrungErlaubt).then("").otherwise(
                        "ES GELTEN FOLGENDE ZUSATZREGELN:\n- DEINE SCHIFFE DÜRFEN SICH NICHT BERÜHREN.\n\n"
                ),
                "WENN DU MIT DEINER AUFSTELLUNG ZUFRIEDEN BIST,\n" +
                        "KLICKE AUF \"SCHLACHT BEGINNEN\"."
        ));

        TextFlow textFlow = new TextFlow(anleitungsUeberschrift, anleitungstext);
        textFlow.lineSpacingProperty().bind(gui.getSchriftgroesse().divide(4));

        this.anleitung = new VBox();
        this.anleitung.setSpacing(20.0);
        this.anleitung.setAlignment(Pos.CENTER_LEFT);
        this.anleitung.getChildren().addAll(textFlow, new Spacer(), this.schlachtBeginnenButton);

        switch (anordnenAufESpielfeld) {
            case LINKS:
                this.fixedRatioPaneRechts.add(this.anleitung);
                break;

            case RECHTS:
                this.fixedRatioPaneLinks.add(this.anleitung);
                break;
        }


    } // Ende Konstruktor


    /**
     * Richtet die Ansicht mit Namen, Spielraster und Text ein.
     * @param spielraster   Spielraster auf dem die Schiffe angeordnet werden
     * @param spielername   Name des Spielers
     * @param schiffsberuehrungErlaubt  True, wenn Schiffe sich berühren dürfen (für Text)
     */
    public void einrichten(Spielraster spielraster, String spielername, boolean schiffsberuehrungErlaubt) {

        // Alte Spielraster aus FixedRatioPanes entfernen
        switch (anordnenAufESpielfeld) {
            case LINKS:
                this.fixedRatioPaneLinks.clearChildren();
                break;
            case RECHTS:
                this.fixedRatioPaneRechts.clearChildren();
                break;
        }

        // Neues Spielraster in IV schreiben
        this.spielraster = spielraster;

        // Properties aktualisieren
        this.spielername.set(spielername);
        this.schiffsberuehrungErlaubt.set(schiffsberuehrungErlaubt);

        // Fixed Ratio Panes aktualisieren
        fixedRatioPaneLinks.setRatio(this.spielraster.getRatio());
        fixedRatioPaneRechts.setRatio(this.spielraster.getRatio());

        // Untere Kante des Buttons an untere Kante des Spielrasters setzen
        this.schlachtBeginnenButton.translateYProperty().bind(this.spielraster.getZellengroesse().multiply(-1));

        // Spacing fuer Buttonleiste
        this.buttonleiste.bindInnerSpacing(
                this.fixedRatioPaneLinks.getInnerLayoutXProperty().add(this.spielraster.getLinkesOffset()),
                this.fixedRatioPaneRechts.getInnerLayoutXProperty().add(this.spielraster.getRechtesOffset())
        );

        if (this.anordnenAufESpielfeld == ESpielfeld.RECHTS) {
            // Linke Kante der Anleitung mit Button bündig setzen
            this.anleitung.translateXProperty().bind(this.spielraster.getZellengroesse());
        }
    }


    @Override
    public void aktualisieren() {

        // GridPane wird erst beim Aufruf reingeschrieben,
        // da andere Views das Objekt auch nutzen
        switch (anordnenAufESpielfeld) {
            case LINKS:
                this.fixedRatioPaneLinks.add(this.spielraster);
                this.fixedRatioPaneLinks.toFront(); // Damit Schiffe nicht unter der Anleitung hängen bleiben können
                break;

            case RECHTS:
                this.fixedRatioPaneRechts.add(this.spielraster);
                this.fixedRatioPaneRechts.toFront(); // Damit Schiffe nicht unter der Anleitung hängen bleiben können
                break;
        }

        // "Automatisch Platzieren"-Button einblenden
        this.spielraster.zeigeAutomatischPlatzierenButton(true);
        this.spielraster.setzeAutomatischPlatzierenButtonAktiviert(true);

        // Label ausblenden
        this.spielraster.zeigeLabel(false);

        // Schiffslite ausblenden
        this.spielraster.zeigeSchiffsliste(false);
    }


    /**
     * Aktiviert den "Schlacht Beginnen"-Button
     */
    public void setzeSchlachtBeginnenButtonAktiviert(boolean aktiviert) {
        this.schlachtBeginnenButton.setDisable(!aktiviert);
    }

    /**
     * Aktiviert den "Automatisch Platzieren"-Button
     */
    public void setzeAutomatischPlatzierenButtonAktiviert(boolean aktiviert) {
        this.spielraster.setzeAutomatischPlatzierenButtonAktiviert(aktiviert);
    }

}
