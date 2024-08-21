package GUI.ansichten;

import GUI.ansichten.unteransichten.LabelMitNodeHorizontal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


/**
 * Ansicht fuer allgemeine Spieleinstellungen
 */
public class Einstellungen extends Ansicht {

    /** Auswahlbox fuer Aufloesung */
    private final ChoiceBox<String> aufloesungChoiceBox;

    /** Auswahlbox fuer Fenstermodus */
    private final ChoiceBox<String> fenstermodusChoiceBox;


    /**
     * Erstellt eine Ansicht auf welcher allgemeine Spieleinstellungen vorgenommen werden können.
     */
    public Einstellungen() {

        // Buttonleiste
        Button zurueckButton = new Button("ZURÜCK");
        zurueckButton.setOnMouseClicked(e -> wirfMenueZurueck());
        this.buttonleiste.add(zurueckButton);


        // Raster
        GridPane raster = new GridPane();
        this.add(raster, 1, 3,3,1);
        RowConstraints inhaltZeile1 = new RowConstraints();
        inhaltZeile1.setPercentHeight(20.0);
        RowConstraints inhaltZeile2 = new RowConstraints();
        inhaltZeile2.setPercentHeight(20.0);
        RowConstraints inhaltZeile3 = new RowConstraints();
        inhaltZeile3.setPercentHeight(20.0);
        RowConstraints inhaltZeile4 = new RowConstraints();
        inhaltZeile4.setPercentHeight(40.0);
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
        Text ueberschrift = new Text("EINSTELLUNGEN");
        ueberschrift.getStyleClass().add("ueberschrift");
        raster.add(ueberschrift,0,0,6,1);



        // "Speichern"-Button
        Button speichernButton = new Button("SPEICHERN");
        speichernButton.getStyleClass().add("grosserButton");
        speichernButton.setOnAction(e -> speichern());
        raster.add(speichernButton,0,2);
        GridPane.setValignment(speichernButton, VPos.TOP);


        // Aufloesungen
        ObservableList<String> aufloesungen = FXCollections.observableArrayList(
                "800x600",
                "1024x768",
                "1280x720",
                "1280x800",
                "1366x768",
                "1920x1080"
        );
        this.aufloesungChoiceBox = new ChoiceBox<>(aufloesungen);
        this.aufloesungChoiceBox.setValue("1920x1080");
        this.aufloesungChoiceBox.prefWidthProperty().bind(gui.getSchriftgroesse().multiply(8));
        LabelMitNodeHorizontal<ChoiceBox> aufloesungLabelMitChoiceBox = new LabelMitNodeHorizontal<>(
                "AUFLÖSUNG",
                this.aufloesungChoiceBox
        );

        // Fenstermodus
        this.fenstermodusChoiceBox = new ChoiceBox<>(
                FXCollections.observableArrayList("FULLSCREEN", "FENSTER")
        );
        this.fenstermodusChoiceBox.setValue("FULLSCREEN");
        this.fenstermodusChoiceBox.prefWidthProperty().bind(gui.getSchriftgroesse().multiply(8));
        LabelMitNodeHorizontal<ChoiceBox> fenstermodusLabelMitChoiceBox = new LabelMitNodeHorizontal<>(
                "FENSTERMODUS",
                this.fenstermodusChoiceBox
        );

        // Einstellungen
        HBox einstellungen = new HBox();
        einstellungen.spacingProperty().bind(gui.getSchriftgroesse().multiply(2));
        einstellungen.getChildren().addAll(aufloesungLabelMitChoiceBox, fenstermodusLabelMitChoiceBox);
        einstellungen.setAlignment(Pos.CENTER_LEFT);
        raster.add(einstellungen, 0,1);
        GridPane.setValignment(einstellungen, VPos.TOP);


        // Credits
        /*
        Text creditsUeberschrift = new Text("CREDITS");
        creditsUeberschrift.getStyleClass().add("ueberschrift");
        Text creditsText = new Text(
            "\n\nDATENHALTUNG: CHRISTOPH SCHNEIDER\n" +
            "GRAFISCHE BENUTZEROBERFLAECHE: ANDRÉ RAIDER\n" +
            "KUENSTLICHE INTELLIGENZ: MATS SCHARNBERG\n" +
            "NETZWERK: MARKUS POTYKA\n" +
            "SPIELLOGIK: JOHANNES HIRTH\n"
        );

        TextFlow credits = new TextFlow();
        credits.getChildren().addAll(creditsUeberschrift, creditsText);
        credits.lineSpacingProperty().bind(gui.getSchriftgroesse().divide(4));
        raster.add(credits,0,3);
        s
        */


    } // Ende Konstruktor


    /**
     * Wendet die selektierten Einstellungen auf die GUI an.
     */
    private void speichern() {
        gui.fensterKonfigurieren(this.aufloesungChoiceBox.getValue(), this.fenstermodusChoiceBox.getValue());
    }


} // Ende Class
