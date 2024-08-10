package GUI.ansichten;

import Administration.Enums;
import Administration.Game;
import GUI.ansichten.unteransichten.FixedRatioPane;
import GUI.ansichten.unteransichten.Spacer;
import GUI.ansichten.unteransichten.Spielraster;
import javafx.scene.control.Button;

/**
 * Ansicht für eine Spielrunde
 */
public class Spielrunde extends Ansicht {

    // IV
    /** FixedRationPanes */
    private FixedRatioPane fixedRatioPaneLinks, fixedRatioPaneRechts;

    /** Spielfelder */
    private Spielraster spielrasterLinks, spielrasterRechts;

    /** Button */
    private final Button undoButton;
    private final Button redoButton;
    private final Button saveButton;
    private final Button closeButton;


    public Spielrunde() {

        // Leiste mit Buttons

                // UndoButton
                this.undoButton = new Button("UNDO");
                this.undoButton.setOnAction(e -> {
                    gui.feuerEvent(Enums.GameAction.UNDO, "");
                });

                // RedoButton
                this.redoButton = new Button("REDO");
                this.redoButton.setOnMouseClicked(e -> {
                    gui.feuerEvent(Enums.GameAction.REDO, "");
                });

                // SaveButton
                this.saveButton = new Button("SPEICHERN");
                this.saveButton.setOnMouseClicked(e -> {
                    gui.feuerEvent(Enums.GameAction.SPIELRUNDE_SPEICHERN, "");
                });

                // CloseButton
                this.closeButton = new Button("ABBRECHEN");
                this.closeButton.setOnMouseClicked(e -> {
                    gui.feuerEvent(Enums.GameAction.SPIELRUNDE_ABBRECHEN, "");
                });

            // Buttons zur Buttonleiste hinzufuegen
            this.buttonleiste.addAll(undoButton,redoButton,new Spacer(), saveButton,closeButton);

    } // Ende Konstruktor


    /**
     * Richtet die Ansicht mit neuen Spielrastern ein.
     * @param spielrasterLinks      Linkes Spielraster
     * @param spielrasterRechts     Rechtes Spielraster
     */
    public void einrichten(Spielraster spielrasterLinks, Spielraster spielrasterRechts) {

        // Alte FixedRatioPanes aus Gridpane loeschen
        this.getChildren().removeAll(this.fixedRatioPaneLinks, this.fixedRatioPaneRechts);

        // Neue Spielraster in IV schreiben
        this.spielrasterLinks = spielrasterLinks;
        this.spielrasterRechts = spielrasterRechts;

        // Neue FixesrationPanes erstellen
        this.fixedRatioPaneLinks = new FixedRatioPane(this.spielrasterLinks.getRatio());
        this.fixedRatioPaneRechts = new FixedRatioPane(this.spielrasterRechts.getRatio());

        // Neue FixesrationPanes in GridPane einfuegen
        this.add(fixedRatioPaneLinks,1,3);
        this.add(fixedRatioPaneRechts,3,3);

        // Innnenabstand der Buttonleiste binden
        this.buttonleiste.bindInnerSpacing(
                this.fixedRatioPaneLinks.getInnerLayoutXProperty().add(spielrasterLinks.getLinkesOffset()),
                this.fixedRatioPaneRechts.getInnerLayoutXProperty().add(spielrasterRechts.getRechtesOffset())
        );
    }


    @Override
    public void aktualisieren() {

        // "Automatisch Platzieren"-Buttons verbergen
        this.spielrasterLinks.zeigeAutomatischPlatzierenButton(false);
        this.spielrasterRechts.zeigeAutomatischPlatzierenButton(false);

        // Labels einblenden
        this.spielrasterLinks.zeigeLabel(true);
        this.spielrasterRechts.zeigeLabel(true);

        // Schiffsliste einblenden
        this.spielrasterLinks.zeigeSchiffsliste(true);
        this.spielrasterRechts.zeigeSchiffsliste(true);

        // Spielraster werden erst jetzt ins GridPane geschrieben,
        // da die Objekte auch von anderen Ansichten genutzt werden
        this.fixedRatioPaneLinks.add(this.spielrasterLinks);
        this.fixedRatioPaneRechts.add(this.spielrasterRechts);
    }


    /**
     * Aktiviert / Deaktiviert den Undo-Button
     * @param aktiviert    True = aktiviert, false = deaktiviert
     */
    public void setzeUndoButtonAktviert(boolean aktiviert) {
        this.undoButton.setDisable(!aktiviert);
    }


    /**
     * Aktiviert / Deaktiviert den Redo-Button
     * @param aktiviert    True = aktiviert, false = deaktiviert
     */
    public void setzeRedoButtonAktviert(boolean aktiviert) {
        this.redoButton.setDisable(!aktiviert);
    }


    /**
     * Aktiviert / Deaktiviert den Speichern-Button
     * @param aktiviert    True = aktiviert, false = deaktiviert
     */
    public void setzeSpeichernBeginnenButtonAktiviert(boolean aktiviert) {
        this.saveButton.setDisable(!aktiviert);
    }

}
