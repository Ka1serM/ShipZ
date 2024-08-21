package GUI.ansichten;

import GUI.Ansicht;
import GUI.ShipzGui;
import GUI.ansichten.unteransichten.LabelMitNodeHorizontal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Ansicht wird am Anfang des Spiels angezeigt,
 * um Auflösung und Fenstermodus zu setzen.
 */
public class Startscreen extends GUI.ansichten.Ansicht {

    /** Gui-Objekt zum Events feuern */
    private final ShipzGui gui;

    /** Auswahlbox fuer Aufloesung */
    private final ChoiceBox<String> aufloesungChoiceBox;

    /** Auswahlbox fuer Fenstermodus */
    private final ChoiceBox<String> fenstermodusChoiceBox;


    /**
     * Erstellt ine Ansicht um Auflösung und Fenstermodus zu setzen.
     * @param gui Instanz der GUI
     */
    public Startscreen(ShipzGui gui) {

        // GUI zum Events feuern
        this.gui = gui;

        // Titel
        Text titelText = new Text("SHIPZ");
        titelText.getStyleClass().add("spieltitel");

        // Subtitel
        //Text subtitelText = new Text("EIN PROJEKT DER HOCHSCHULE DÜSSELDORF");
        //subtitelText.getStyleClass().add("ueberschrift");
        //subtitelText.setTranslateY(4);

        // Titel und Subtitel
        HBox titelUndSubtitel = new HBox();
        //titelUndSubtitel.getChildren().addAll(titelText, subtitelText);
        //titelUndSubtitel.setAlignment(Pos.CENTER);
        //titelUndSubtitel.setSpacing(20);

        // SpielStartenButton
        Button spielStartenButton = new Button("SPIEL STARTEN");
        spielStartenButton.getStyleClass().add("grosserButton");
        spielStartenButton.setOnAction(e -> spielStarten());


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
        this.aufloesungChoiceBox.setValue("1280x720");
        this.aufloesungChoiceBox.setMaxWidth(0);
        this.aufloesungChoiceBox.setMinWidth(120);
        LabelMitNodeHorizontal<ChoiceBox> aufloesungLabelMitChoiceBox = new LabelMitNodeHorizontal<>(
                "AUFLÖSUNG",
                this.aufloesungChoiceBox
        );

        // Fenstermodus
        this.fenstermodusChoiceBox = new ChoiceBox<>(
                FXCollections.observableArrayList("FULLSCREEN", "FENSTER")
        );
        this.fenstermodusChoiceBox.setValue("FENSTER");
        this.fenstermodusChoiceBox.setMinWidth(120);
        this.fenstermodusChoiceBox.setMaxWidth(0);
        LabelMitNodeHorizontal<ChoiceBox> fenstermodusLabelMitChoiceBox = new LabelMitNodeHorizontal<>(
                "FENSTERMODUS",
                this.fenstermodusChoiceBox
        );

        // Einstellungen
        HBox einstellungen = new HBox();
        einstellungen.setSpacing(50.0);
        einstellungen.getChildren().addAll(
                aufloesungLabelMitChoiceBox,
                fenstermodusLabelMitChoiceBox,
                spielStartenButton);
        einstellungen.setAlignment(Pos.CENTER);


        // Titel und Einstellung verbinden
        VBox titelUndEinstellungen = new VBox();
        titelUndEinstellungen.getChildren().addAll(titelUndSubtitel, einstellungen);
        titelUndEinstellungen.setAlignment(Pos.CENTER);
        titelUndEinstellungen.setSpacing(20.0);
        this.add(titelUndEinstellungen, 0, 0, 5, 5);
    }


    /**
     * Kunfiguriert das GUI-Fenster und ruft das Hauptmenue auf.
     */
    private void spielStarten() {

        gui.fensterKonfigurieren(this.aufloesungChoiceBox.getValue(), this.fenstermodusChoiceBox.getValue());
        gui.zeigeAnsicht(Ansicht.HAUPTMENUE);
    }

}
