package GUI.ansichten;

import Administration.Enums;
import GUI.ShipzGui;
import GUI.Spielertyp;
import GUI.ansichten.unteransichten.Buttonleiste;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

/**
 * Superklasse aller Views.
 *
 * Superklasse aller Ansichten.
 * Stellt eine Instanz von ShipzGui, eine Buttonleiste und ein Grundraster zum Layouten zur Verfuegung.
 *
 * Aufbau:
 *
 *                4%                   45%          2%            45%                 4%
 *       ----------------------------------------------------------------------------------------
 *    8% |                   |                   |      |                    |                  |  zeilePaddingOben
 *       ----------------------------------------------------------------------------------------
 *    5% |                   |                   |      |                    |                  |  zeileButtons
 *       ----------------------------------------------------------------------------------------
 *    3% |                   |                   |      |                    |                  |  Gap
 *       ----------------------------------------------------------------------------------------
 *   80% |                   |                   |      |                    |                  |  zeileInhalt
 *       ----------------------------------------------------------------------------------------
 *    4% |                   |                   |      |                    |                  |  zeilePaddingUnten
 *       ----------------------------------------------------------------------------------------
 *        spaltePaddingLinks   spalteInhaltLinks   gap   spalteInhaltRechts   spaltePaddingRechts
 *
 *
 *
 */
public abstract class Ansicht extends GridPane {

    /** Wird von Kindklassen zum Feuern der Events und beziehen von Daten genutzt */
    protected ShipzGui gui = ShipzGui.getInstance();

    /** Buttonleiste */
    protected Buttonleiste buttonleiste;


    Ansicht() {

        // ----- Raster aufbauen -----

        // Spalten
        ColumnConstraints spaltePaddingLinks = new ColumnConstraints();
        spaltePaddingLinks.setPercentWidth(4);
        ColumnConstraints spalteInhalt1 = new ColumnConstraints();
        spalteInhalt1.setPercentWidth(45);
        ColumnConstraints spalteGap = new ColumnConstraints();
        spalteGap.setPercentWidth(2);
        ColumnConstraints spalteInhalt2 = new ColumnConstraints();
        spalteInhalt2.setPercentWidth(45);
        ColumnConstraints spaltePaddingRechts = new ColumnConstraints();
        spaltePaddingRechts.setPercentWidth(4);
        this.getColumnConstraints().addAll(
                spaltePaddingLinks,
                spalteInhalt1,
                spalteGap,
                spalteInhalt2,
                spaltePaddingRechts
        );

        // Zeilen
            RowConstraints zeilePaddingOben = new RowConstraints();
            zeilePaddingOben.setPercentHeight(8);
            RowConstraints zeileButtons = new RowConstraints();
            zeileButtons.setPercentHeight(5);
            RowConstraints zeileGap = new RowConstraints();
            zeileGap.setPercentHeight(3);
            RowConstraints zeileInhalt = new RowConstraints();
            zeileInhalt.setPercentHeight(80);
            RowConstraints zeilePaddingUnten = new RowConstraints();
            zeilePaddingUnten.setPercentHeight(4);
            this.getRowConstraints().addAll(
                    zeilePaddingOben,
                    zeileButtons,
                    zeileGap,
                    zeileInhalt,
                    zeilePaddingUnten
            );


        // ----- Buttonleiste anlegen -----
        this.buttonleiste = new Buttonleiste();
        this.add(buttonleiste, 1,1,3,1);


    } // Ende Konstruktor


    /**
     * Aktualisiert die Daten in der Ansicht und entfernt ale Gebrauchssprugen.
     */
    public void aktualisieren() {}


    /**
     * Gibt je nach selektiertem Wert in der ChoiceBox einen passenden Enum zurueck.
     * @param spielertypString  String aus der ChoiceBox
     * @return                  Passendes Enum
     */
    protected Spielertyp spielertyp(String spielertypString) {
        switch (spielertypString) {
            case "MENSCHLICHER SPIELER":
                return Spielertyp.MENSCHLICHER_SPIELER;

            case "KI1 (LEICHT)":
                return Spielertyp.KI1;

            case "KI2 (MITTEL)":
                return Spielertyp.KI2;

            case "KI3 (SCHWER)":
                return Spielertyp.KI3;

            default:
                return Spielertyp.NICHT_VERFUEGBAR;
        }
    }

    /**
     * Setzt je nach selektiertem Wert in einer Choicebox die Daten in das zugehoerige Textfeld.
     * @param choicebox     ChoiceBox
     * @param textfeld      TextField
     */
    protected void aktualisiereSpielernamePreset(ChoiceBox<String> choicebox, TextField textfeld) {

        switch (choicebox.getValue()) {
            case "MENSCHLICHER SPIELER":
                setzeTextfelddaten(textfeld , "", "NAMEN EINGEBEN...", true);
                break;
            case "KI1 (LEICHT)":
                setzeTextfelddaten(textfeld , "LEICHT", "", false);
                break;
            case "KI2 (MITTEL)":
                setzeTextfelddaten(textfeld , "MITTEL", "", false);
                break;
            case "KI3 (SCHWER)":
                setzeTextfelddaten(textfeld , "SCHWER", "", false);
                break;
        }
    }


    /**
     * Setzt Daten in ein Textfeld
     * @param textfeld      Textfeld, in welches die Daten gesetzt werden sollen
     * @param text          Text
     * @param promptText    Placeholdertext
     * @param aktiviert     True, wenn Feld aktiviert sein soll
     */
    private void setzeTextfelddaten(TextField textfeld, String text, String promptText, boolean aktiviert) {
        textfeld.setText(text);
        textfeld.setPromptText(promptText);
        textfeld.setDisable(!aktiviert);
    }


    /**
     * Wirft die als naechstes aufzurufende Ansicht an die Spiellogik.
     * @param naechsteAnsicht   Als naechstes aufzurufende Ansicht
     */
    protected void wirfNaechsteAnsicht(GUI.Ansicht naechsteAnsicht) {
        gui.setGewaelteAnsicht(naechsteAnsicht);
        gui.feuerEvent(Enums.GameAction.ANSICHT_GEWAEHLT, "");
    }
}
