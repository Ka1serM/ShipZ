package GUI.ansichten;


import Administration.Enums;
import GUI.Ansicht;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

/**
 * Wird angezeigt, wenn Netzwerkverbindung abbricht.
 */
public class NetzwerkverbindungAbgebrochen extends GUI.ansichten.Ansicht {


    /**
     * Erstellt eine Ansicht, welche die IP dis Hostrechners und den offenen Port anzeigt.
     */
    public NetzwerkverbindungAbgebrochen() {


        // Raster
        GridPane raster = new GridPane();
        this.add(raster, 1, 3,3,1);
        RowConstraints inhaltZeile1 = new RowConstraints();
        inhaltZeile1.setPercentHeight(20.0);
        RowConstraints inhaltZeile2 = new RowConstraints();
        inhaltZeile2.setPercentHeight(25.0);
        RowConstraints inhaltZeile3 = new RowConstraints();
        inhaltZeile3.setPercentHeight(15.0);
        raster.getRowConstraints().addAll(
                inhaltZeile1,
                inhaltZeile2,
                inhaltZeile3
        );
        ColumnConstraints spalte = new ColumnConstraints();
        spalte.setPercentWidth(100.0);
        raster.getColumnConstraints().addAll(
                spalte
        );


        // Ueberschrift
        Text ueberschrift = new Text("NETZWERKVERBINDUNG ABGEBROCHEN");
        ueberschrift.getStyleClass().add("ueberschrift");
        raster.add(ueberschrift,0,0,6,1);


        // Anleitung
        Text anleitung = new Text(
                "DIE NETZWERKVERBINDUNG IST ABGEBROCHEN. DIES KANN VERSCHIEDENE GRÜNDE HABEN:\n" +
                "- MANUELLER ABBRUCH DES ANDEREN SPIELERS\n" +
                "- STÖRUNG DER VERBINDUNG.\n\n" +
                "DU KANNST DAS SPIEL SPEICHERN ODER ABBRECHEN UND ZURÜCK ZUM HAUPTMENÜ GEHEN."
        );
        anleitung.lineSpacingProperty().bind(gui.getSchriftgroesse().divide(4));
        //raster.add(anleitung,0,1,6,1);
        GridPane.setValignment(anleitung, VPos.TOP);


        // AbbrechenButton
        Button hauptmenuebutton = new Button("HAUPTMENÜ");
        hauptmenuebutton.setOnAction(e -> wirfNaechsteAnsicht(Ansicht.HAUPTMENUE));

        // SaveButton
        Button speichernButton = new Button("SPEICHERN");
        speichernButton.setOnAction(e -> {
            gui.feuerEvent(Enums.GameAction.SPIELRUNDE_SPEICHERN, "");
        });


        // Buttons verbinden
        HBox buttons = new HBox();
        buttons.getChildren().addAll(speichernButton, hauptmenuebutton);
        buttons.spacingProperty().bind(gui.getSchriftgroesse().divide(2));
        raster.add(buttons, 0,2);


    } // Ende Konstruktor

} // Ende Class
