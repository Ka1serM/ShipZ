package GUI.ansichten;

import Administration.Enums;
import GUI.Ansicht;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


/**
 * Hauptmenue des Spiels
 */
public class Hauptmenue extends GUI.ansichten.Ansicht {


    /**
     * Erstellt eine Ansicht des Hauptmenues.
     */
    public Hauptmenue() {

        // Buttons
        Button lokalesSpielButton = new Button("LOKALES SPIEL");
        lokalesSpielButton.setOnAction(e -> wirfNaechsteAnsicht(Ansicht.LOKALE_SPIELER_WAEHLEN));

        Button netzwerkButton = new Button("NETZWERKSPIEL");
        netzwerkButton.setOnAction(e -> wirfNaechsteAnsicht(Ansicht.NETZWERKMODUS_WAEHLEN));

        Button spielLadenButton = new Button("SPIEL LADEN");
        spielLadenButton.setOnAction(e -> wirfNaechsteAnsicht(Ansicht.SPIEL_LADEN));

        Button highscoreButton = new Button("BESTENLISTE");
        highscoreButton.setOnAction(e -> wirfNaechsteAnsicht(Ansicht.BESTENLISTE));

        Button einstellungenButton = new Button("EINSTELLUNGEN");
        einstellungenButton.setOnAction(e -> wirfNaechsteAnsicht(Ansicht.EINSTELLUNGEN));

        Button beendenButton = new Button("BEENDEN");
        beendenButton.setOnMouseClicked(e -> gui.feuerEvent(Enums.GameAction.PROGRAMM_BEENDEN, ""));


        // Linke Spalte mit Buttons
        VBox linkeSpalte = new VBox();
        linkeSpalte.getChildren().addAll(
                lokalesSpielButton,
                netzwerkButton,
                spielLadenButton
        );
        linkeSpalte.getChildren().forEach(node -> {
            Button button = (Button)node;
            button.setMinWidth(230.0);
            button.getStyleClass().add("grosserButton");
        });
        linkeSpalte.spacingProperty().bind(gui.getSchriftgroesse());
        linkeSpalte.setAlignment(Pos.CENTER);


        // Rechte Spalte mit Buttons
        VBox rechteSpalte = new VBox();
        rechteSpalte.getChildren().addAll(
                highscoreButton,
                einstellungenButton,
                beendenButton
        );
        rechteSpalte.getChildren().forEach(node -> {
            Button button = (Button)node;
            button.setMinWidth(230.0);
            button.getStyleClass().add("grosserButton");
        });
        rechteSpalte.spacingProperty().bind(gui.getSchriftgroesse());
        rechteSpalte.setAlignment(Pos.CENTER);


        // Spalten zusammenfuegen
        HBox beideSpalten = new HBox();
        beideSpalten.getChildren().addAll(linkeSpalte, rechteSpalte);
        beideSpalten.setAlignment(Pos.CENTER);
        beideSpalten.spacingProperty().bind(gui.getSchriftgroesse().multiply(2));
        this.add(beideSpalten,1,2,3,2);
    }

}
