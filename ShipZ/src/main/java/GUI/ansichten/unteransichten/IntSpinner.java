package GUI.ansichten.unteransichten;

import GUI.ShipzGui;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;


/**
 * Erzeugt IntergerSpinner, funktional vergleichbar mit javafx.scene.control.Spinner von Integer
 *
 * Da JavaFx die Aenderung der Increse/Decrease-Button-Texte nicht ermoeglicht (ausser durch einen SVG-Shape im CSS),
 * schreibe ich mir den Kram hat selber.
 * Ausserdem deaktivieren sich hier die Buttons, wenn es keinen kleineren / groesseren Wert gibt.
 */
public class IntSpinner extends HBox {


    // IV
    /** Button zum veraendern des angezeigten Werts */
    private final Button erniedrigenButton;
    private final Button erhoehenButton;

    /** Textfeld mit angezeigten Wert */
    private final TextField feld;

    /** Begrenzt den Wertebereich */
    private final int min;
    private final int max;

    /** Angezeigter Wert */
    protected SimpleIntegerProperty wert;

    /** Von aussen zusaetzlich setzbare Deaktivierung der Buttons,
     * auch wenn der Wertebereich noch Aenderungen zulaesst. */
    private boolean erniedrigenButtonExternDeaktiviert, erhoehenButtonExternDeaktiviert;

    /**
     * Erstellt einen IntSpinner
     * @param min       Minimaler Wert
     * @param max       Maximaler Wert
     * @param initial   Startwert
     */
    public IntSpinner(final int min, final int max, final int initial) {

        final ShipzGui gui = ShipzGui.getInstance();

        wert = new SimpleIntegerProperty(initial);
        this.min = min;
        this.max = max;
        erniedrigenButtonExternDeaktiviert = false;
        erniedrigenButtonExternDeaktiviert = false;

        erniedrigenButton = new Button("-");
        erniedrigenButton.setOnAction(e -> this.wertErniedrigen());
        feld = new TextField();
        feld.textProperty().bind(wert.asString());
        feld.setDisable(true);
        feld.setAlignment(Pos.CENTER);

        final int anzahlStellen = String.valueOf(max).length();
        feld.setMinWidth(30);
        feld.prefWidthProperty().bind(gui.getSchriftgroesse().multiply(2).add(gui.getSchriftgroesse().multiply(anzahlStellen-1)));

        erhoehenButton = new Button("+");
        erhoehenButton.setOnAction(e -> this.wertErhoehen());

        getChildren().addAll(this.erniedrigenButton, this.feld, this.erhoehenButton);
        setSpacing(7.0);

        this.aktualisiereButtons();
    }


    /**
     * (De)Aktviert die Erhoehen-/Erniedrigenbuttons unter Beruecksichtigung der Min-/Max-Werte des Spinners
     * und der manuellen Moeglichkeit zur Deaktivierung von aussen.
     */
    public void aktualisiereButtons() {

        erniedrigenButton.setDisable(wert.getValue() == min || erniedrigenButtonExternDeaktiviert);

        erhoehenButton.setDisable(wert.getValue() == max || erhoehenButtonExternDeaktiviert);
    }


    /**
     * Gibt den aktuell angezeigten Wert zurueck.
     * @return Angezeigter Wert
     */
    public int getValue() {
        return wert.getValue();
    }


    /**
     * Setzt den angezeigten Wert.
     * @param wert Angezeigter Wert
     */
    public void setValue(final int wert) {
        if((wert >= this.min) && (wert <= this.max)) {
            this.wert.setValue(wert);
            this.aktualisiereButtons();
        }
    }


    /**
     * Registriert einen Listener, welcher informiert wird,
     * wenn sich der angezeigte Wert des Textfelds ändert.
     * @param listener Zu registrierender Listener
     */
    public void addListener(final InvalidationListener listener) {
        wert.addListener(listener);
    }


    /**
     * (De)Aktiviert den Erniedrigen-Button
     * @param aktiviert TRUE = aktiviert, FALSE = Deaktiviert
     */
    public void setzeErniedrigenButtonAktiviert(final boolean aktiviert) {
        erniedrigenButtonExternDeaktiviert = !aktiviert;
        this.aktualisiereButtons();
    }


    /**
     * (De)Aktiviert den Erhoehen-Button
     * @param aktiviert TRUE = aktiviert, FALSE = Deaktiviert
     */
    public void setzeErhoehenButtonAktiviert(final boolean aktiviert) {
        erhoehenButtonExternDeaktiviert = !aktiviert;
        this.aktualisiereButtons();
    }


    /**
     * Erniedrigt den Wert des Feldes um 1.
     */
    private void wertErniedrigen() {
        wert.setValue(wert.getValue() - 1);
        this.aktualisiereButtons();
    }


    /**
     * Erhoeht den Wert des Feldes um 1.
     */
    private void wertErhoehen() {
        wert.setValue(wert.getValue() + 1);
        this.aktualisiereButtons();
    }



}
