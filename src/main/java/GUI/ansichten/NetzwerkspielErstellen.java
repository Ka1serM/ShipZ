package GUI.ansichten;

import Administration.Enums.EAction;
import GUI.ansichten.unteransichten.LabelMitNodeVertikal;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;


/** Ansicht zum Konfigurieren des Netzwerk-Clients */
public class NetzwerkspielErstellen extends GUI.ansichten.Ansicht {

    // IV
    /** ESpielertyp */
    private final ChoiceBox<String> spielertypChoiceBox;

    /** Spielername */
    private TextField spielernameTextfeld;

    /** Textfeld fuer IP */
    private final TextField portTextField;

    /** Button zum Verbinden */
    private final Button spielAufbauenButton;

    /** Maximale Zeichenanzahl der Spielernamen */
    private static final int MAXIMALE_LAENGE_SPIELERNAMEN = 20;


    /**
     * Erstellt eine Ansicht zum Konfigurieren der des Netzwerk-Clients
     */
    public NetzwerkspielErstellen() {

        // Buttonleiste
        final Button zurueckButton = new Button("ZURÜCK");
        zurueckButton.setOnMouseClicked(e -> wirfMenueZurueck());
        buttonleiste.add(zurueckButton);

        // Raster
        final GridPane raster = new GridPane();
        add(raster, 1, 3,3,1);
        final RowConstraints inhaltZeile1 = new RowConstraints();
        inhaltZeile1.setPercentHeight(20.0);
        final RowConstraints inhaltZeile2 = new RowConstraints();
        inhaltZeile2.setPercentHeight(30.0);
        final RowConstraints inhaltZeile3 = new RowConstraints();
        inhaltZeile3.setPercentHeight(18.0);
        final RowConstraints inhaltZeile4 = new RowConstraints();
        inhaltZeile4.setPercentHeight(20.0);
        raster.getRowConstraints().addAll(
                inhaltZeile1,
                inhaltZeile2,
                inhaltZeile3,
                inhaltZeile4
        );
        final ColumnConstraints spalte = new ColumnConstraints();
        spalte.setPercentWidth(100.0);
        raster.getColumnConstraints().addAll(
                spalte
        );


        // Ueberschrift
        final Text ueberschrift = new Text("NETZWERKSPIEL ERSTELLEN");
        ueberschrift.getStyleClass().add("ueberschrift");
        raster.add(ueberschrift,0,0,6,1);


        // Anleitung
        final Text anleitung = new Text(
                "ENTSCHEIDE, WELCHER SPIELERTYP AUF DEINEM SPIELFELD SPIELEN SOLL.\n" +
                "DU KANNST ZWISCHEN VIER OPTIONEN WÄHLEN: MENSCHLICHER SPIELER, KI1 (LEICHT), KI2 (MITTEL) und KI3 (SCHWER).\n" +
                "WENN DU MENSCHLICHER SPIELER AUSWÄHLST, GIB ZUSÄTZLICH NOCH EINEN SPIELERNAMEN EIN.\n" +
                "\n" +
                "WÄHLE EINEN PORT AUS, AUF WELCHEM SICH DER CLIENT VERBINDEN SOLL.\n" +
                "KLICKE ANSCHLIESSEND AUF \"SPIEL ERSTELLEN\"."
        );
        anleitung.lineSpacingProperty().bind(this.gui.getSchriftgroesse().divide(4));
        //raster.add(anleitung,0,1,6,1);
        setValignment(anleitung, VPos.TOP);



        // ESpielertyp
        spielertypChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                "MENSCHLICHER SPIELER",
                "KI1 (LEICHT)",
                "KI2 (MITTEL)",
                "KI3 (SCHWER)"
        ));
        spielertypChoiceBox.setValue("MENSCHLICHER SPIELER");
        spielertypChoiceBox.setOnAction(e -> {
            aktualisiereSpielernamePreset(spielertypChoiceBox, spielernameTextfeld);
            aktualisiereSpielAufbauenButton();
        });
        spielertypChoiceBox.prefWidthProperty().bind(this.gui.getSchriftgroesse().multiply(16));
        final LabelMitNodeVertikal<ChoiceBox<String>> spielertypLabelMitChoiceBox = new LabelMitNodeVertikal<>(
                "SPIELERTYP",
                spielertypChoiceBox
        );


        // Spielername
        final LabelMitNodeVertikal<TextField> spielernameLabelMitTextField = new LabelMitNodeVertikal<>(
                "SPIELERNAME",
                spielernameTextfeld = new TextField()
        );
        spielernameTextfeld.prefWidthProperty().bind(this.gui.getSchriftgroesse().multiply(12));
        spielernameTextfeld.textProperty().addListener((ov, alterWert, neuerWert) -> {
            spielernameTextfeld.setText(
                    neuerWert.substring(0, Math.min(NetzwerkspielErstellen.MAXIMALE_LAENGE_SPIELERNAMEN, neuerWert.length())).toUpperCase()
            );
            aktualisiereSpielAufbauenButton();
        });


        // Port
        portTextField = new TextField();
        portTextField.textProperty().addListener((ov, alterWert, neuerWert) -> {
            // Loesche alle anderen Buchstaben als 0-9 aus der Eingabe
            if (!neuerWert.matches("\\d*")) {
                portTextField.setText(neuerWert.replaceAll("[^\\d]", ""));
            }
            aktualisiereSpielAufbauenButton();
        });
        portTextField.prefWidthProperty().bind(this.gui.getSchriftgroesse().multiply(6));
        final LabelMitNodeVertikal<TextField> portLabelMitTextField = new LabelMitNodeVertikal<>(
                "PORT",
                portTextField
        );

        // Spiel Aufbauen Button
        spielAufbauenButton = new Button("SPIEL ERSTELLEN");
        spielAufbauenButton.setOnAction(e -> this.feuerDaten());
        spielAufbauenButton.setAlignment(Pos.BOTTOM_LEFT);
        final LabelMitNodeVertikal<Button> spielAufbauenButtonnMitLabel = new LabelMitNodeVertikal<>(
                "",
                spielAufbauenButton
        );


        // Inputs mit HBox verbinden
        final HBox inhalt = new HBox();
        inhalt.spacingProperty().bind(this.gui.getSchriftgroesse());
        inhalt.getChildren().addAll(
                spielertypLabelMitChoiceBox,
                spielernameLabelMitTextField,
                portLabelMitTextField,
                spielAufbauenButtonnMitLabel
        );
        raster.add(inhalt, 0, 2);



        aktualisiereSpielAufbauenButton();
    }


    @Override
    public void aktualisieren() {
        spielernameTextfeld.setText("");
        portTextField.setText("");
        spielertypChoiceBox.setValue("MENSCHLICHER SPIELER");
    }


    /**
     * Deaktiviert den SpielAufbauenButton, wenn Spielername nicht ausgefuellt oder Port nicht gueltig ist.
     */
    private void aktualisiereSpielAufbauenButton() {

        spielAufbauenButton.setDisable(true);

        // Wenn der Spielername ausgefuellt worden ist
        if(!"".equals(this.spielernameTextfeld.getText())) {

            // Pruefe Port
            if(!"".equals(this.portTextField.getText())) {
                final int port = Integer.parseInt(portTextField.getText());
                if ((65536 > port) && (0 < port)) {
                    spielAufbauenButton.setDisable(false);
                }
            }
        }
    }


    /**
     * Wirft ein Event mit den eingegebenen Daten an die Spiellogik.
     */
    private void feuerDaten() {

        // Eventdaten setzen
        this.gui.setSpielertypLinks(this.spielertyp(spielertypChoiceBox.getValue()));
        this.gui.setSpielernameLinks(this.spielernameTextfeld.getText());
        this.gui.setPort((Integer.parseInt(portTextField.getText())));

        // Bumm
        this.gui.feuerEvent(EAction.NETZWERK_HOST_DATEN_ABRUFBBAR, "");

    }


}
