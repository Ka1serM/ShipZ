package GUI.ansichten;


import GUI.Ansicht;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * Diese Ansicht wird nach dem Ende einer Spielrunde angezeigt.
 */
public class SpielrundeBeendet extends GUI.ansichten.Ansicht {

    /** Spielername des Gewinners */
    private final SimpleStringProperty gewinnername;

    /** Endscore des Gewinners */
    private final SimpleIntegerProperty endscore;


    /**
     * Erstellt eine Ansicht zum Anzeigen des Gewinners und der Punktestaende.
     */
    public SpielrundeBeendet() {

        this.gewinnername = new SimpleStringProperty("NIEMAND");
        this.endscore = new SimpleIntegerProperty(0);


        // Text
        Text gewinnerText = new Text();
        gewinnerText.textProperty().bind(Bindings.concat(this.gewinnername, " HAT GEWONNEN!"));
        gewinnerText.getStyleClass().add("ueberschrift");
        Text scoreText = new Text();
        scoreText.textProperty().bind(Bindings.concat("SCORE: ", this.endscore));
        scoreText.getStyleClass().add("groesser");


        // Buttons
            // BestenlisteButton
            Button highscoreButton = new Button("BESTENLISTE");
            highscoreButton.setOnAction(e -> wirfNaechsteAnsicht(Ansicht.BESTENLISTE));

            // HauptmenueButton
            Button hauptmenueButton = new Button("HAUPTMENÜ");
            hauptmenueButton.setOnAction(e -> wirfNaechsteAnsicht(Ansicht.HAUPTMENUE));

            // Buttons horizpntal verbinden
            HBox buttons = new HBox();
            buttons.getChildren().addAll(highscoreButton, hauptmenueButton);
            buttons.setAlignment(Pos.CENTER);
            buttons.spacingProperty().bind(gui.getSchriftgroesse());
            buttons.translateYProperty().bind(gui.getSchriftgroesse().multiply(1.5));


        // Text und Buttons vertikal verbinden
        VBox textUndButtons = new VBox();
        textUndButtons.getChildren().addAll(gewinnerText, scoreText, buttons);
        textUndButtons.setAlignment(Pos.CENTER);
        textUndButtons.spacingProperty().bind(gui.getSchriftgroesse());


        // Text und Buttons horizontal zentrieren
        HBox zentriereTextUndButtons = new HBox();
        zentriereTextUndButtons.getChildren().add(textUndButtons);
        zentriereTextUndButtons.setAlignment(Pos.CENTER);


        this.add(zentriereTextUndButtons,1,1,3,3);

    } // Ende Konstruktor


    /**
     * Aktualisiert die Daten des Gewinners.
     * @param spielername   Spielername des Gewinners
     * @param endscore      Endscore des Gewinners
     */
    public void setGewinner(String spielername, int endscore) {
        this.gewinnername.set(spielername);
        this.endscore.set(endscore);
    }

} // Ende Class
