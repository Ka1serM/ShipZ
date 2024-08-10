package GUI.ansichten;


import Administration.Enums;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;


/**
 * Wenn Host und Client sich erfolgreich verbunden haben.
 * wird beim Client diese Ansicht so lange angezeigt,
 * bis der Host die Spielkonfiguration abgeschlossen hat.
 */
public class WarteAufHostSpielkonfiguration extends Ansicht {


    /**
     * Erstellt eine Ansicht, welche die IP dis Hostrechners und den offenen Port anzeigt.
     */
    public WarteAufHostSpielkonfiguration() {

        // Buttonleiste
        Button zurueckButton = new Button("ZURÜCK");
        zurueckButton.setDisable(true);
        this.buttonleiste.add(zurueckButton);


        // Raster
        GridPane raster = new GridPane();
        this.add(raster, 1, 3,3,1);
        RowConstraints inhaltZeile1 = new RowConstraints();
        inhaltZeile1.setPercentHeight(20.0);
        RowConstraints inhaltZeile2 = new RowConstraints();
        inhaltZeile2.setPercentHeight(15.0);
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
        Text ueberschrift = new Text("ERFOLGREICH VERBUNDEN.");
        ueberschrift.getStyleClass().add("ueberschrift");
        raster.add(ueberschrift,0,0,6,1);


        // Anleitung
        Text anleitung = new Text(
                "DU WURDEST ERFOLGREICH MIT DEM HOST VERBUNDEN. WARTE NUN, BIS DER HOST DAS SPIEL KONFIGURIERT HAT.\n" +
                "DU KANNST JEDERZEIT DAS NETZWERKSPIEL MIT \"ABBRECHEN\" beenden."
        );
        anleitung.lineSpacingProperty().bind(gui.getSchriftgroesse().divide(4));
        raster.add(anleitung,0,1,6,1);
        GridPane.setValignment(anleitung, VPos.TOP);


        // AbbrechenButton
        Button netzwerkspielAbbrechenButton = new Button("ABBRECHEN");
        netzwerkspielAbbrechenButton.getStyleClass().add("grosserButton");
        netzwerkspielAbbrechenButton.setOnAction(e -> {
            gui.feuerEvent(Enums.GameAction.NETZWERKSPIEL_ABBRECHEN, "");
        });
        raster.add(netzwerkspielAbbrechenButton,0,2);


    } // Ende Konstruktor

} // Ende Class
