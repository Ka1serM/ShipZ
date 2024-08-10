package GUI.ansichten;

import Administration.Enums;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

/** Ansicht zum Auswaehlen des Netzwerkmodus */
public class NetzwerkmodusWaehlen extends GUI.ansichten.Ansicht {

    /**
     * Erstellt eine Ansicht zum Auswaehlen des Netzwerkmodus
     */
    public NetzwerkmodusWaehlen() {

        // Buttonleiste
        Button zurueckButton = new Button("ZURÜCK");
        zurueckButton.setOnAction(e -> wirfNaechsteAnsicht(GUI.Ansicht.HAUPTMENUE));
        this.buttonleiste.add(zurueckButton);


        // Raster
        GridPane raster = new GridPane();
        this.add(raster, 1, 3,3,1);
        RowConstraints inhaltZeile1 = new RowConstraints();
        inhaltZeile1.setPercentHeight(20.0);
        RowConstraints inhaltZeile2 = new RowConstraints();
        inhaltZeile2.setPercentHeight(20.0);
        RowConstraints inhaltZeile3 = new RowConstraints();
        inhaltZeile3.setPercentHeight(18.0);
        RowConstraints inhaltZeile4 = new RowConstraints();
        inhaltZeile4.setPercentHeight(20.0);
        raster.getRowConstraints().addAll(
                inhaltZeile1,
                inhaltZeile2,
                inhaltZeile3,
                inhaltZeile4
        );
        ColumnConstraints spalte = new ColumnConstraints();
        spalte.setPercentWidth(100.0);
        raster.getColumnConstraints().addAll(
                spalte
        );


        // Ueberschrift
        Text ueberschrift = new Text("NETZWERKSPIEL");
        ueberschrift.getStyleClass().add("ueberschrift");
        raster.add(ueberschrift,0,0,6,1);


        // Anleitung
        Text anleitung = new Text(
                "UM EIN SPIEL ÜBER DAS NETZWERK AUFZUBAUEN, MÜSSEN SICH BEIDE SPIELER IM GLEICHEN LOKALEN NETZWERK BEFINDEN.\n" +
                "WÄHLE AUS, OB DU EIN NETZWERKSPIEL ERSTELLEN ODER DICH MIT EINEM ERSTELLTEN SPIEL VERBINDEN MÖCHTEST.\n" +
                "ALS ERSTELLER KANNST DU EINSTELLUNGEN ÜBER DIE SPIELFELDGRÖSSE, ANZAHL DER EINZELNEN SCHIFFE ETC. VORNEHMEN."
        );
        anleitung.lineSpacingProperty().bind(gui.getSchriftgroesse().divide(4));
        //raster.add(anleitung,0,1,6,1);
        GridPane.setValignment(anleitung, VPos.TOP);


        // HostButton
        Button hostButton = new Button("SPIEL ERSTELLEN");
        hostButton.getStyleClass().add("grosserButton");
        hostButton.setOnAction(e -> feuerNetzwerkmodus(true));


        // ClientButton
        Button clientButton = new Button("MIT SPIEL VERBINDEN");
        clientButton.getStyleClass().add("grosserButton");
        clientButton.setOnAction(e -> feuerNetzwerkmodus(false));


        // Buttons gruppieren
        HBox buttons = new HBox();
        buttons.getChildren().addAll(hostButton, clientButton);
        buttons.spacingProperty().bind(gui.getSchriftgroesse());
        raster.add(buttons,0,2,6,1);

    }


    /**
     * Wirft ein Event mit dem gewaehlten Netzwerkmodus an die Spiellogik.
     * @param istHost TRUE, wenn der Spieler als Host fungieren möchte, sonst FALSE
     */
    private void feuerNetzwerkmodus(boolean istHost) {

        // Eventdaten setzen
        gui.setIstHost(istHost);

        // Bumm
        gui.feuerEvent(Enums.GameAction.NETZWERKMODUS_ABRUFBAR, "");
    }

}
