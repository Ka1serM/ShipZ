package GUI.ansichten;

import Administration.Enums;
import Administration.Game;
import GUI.ansichten.unteransichten.LabelMitNodeVertikal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

/**
 * Ansicht um die Typen und Namen der zwei Spieler eines lokalen Spieles zu setzen.
 */
public class LokaleSpielerWaehlen extends GUI.ansichten.Ansicht {

    /** Spielertypen */
    private final ChoiceBox<String> spielertypLinksChoiceBox;
    private final ChoiceBox<String> spielertypRechtsChoiceBox;

    /** Spielernamen */
    private TextField spielernameLinksTextfeld, spielernameRechtsTextfeld;

    /** WeiterButton */
    private final Button weiterButton;

    /** Maximale Zeichenanzahl der Spielernamen */
    private static final int MAXIMALE_LAENGE_SPIELERNAMEN = 20;


    /**
     * Erstellt eine Ansicht zum Setzen der Spielertypen / -namen.
     */
    public LokaleSpielerWaehlen() {

        // ZurueckButton
        Button zurueckButton = new Button("ZURÜCK");
        zurueckButton.setOnMouseClicked(e -> wirfNaechsteAnsicht(GUI.Ansicht.HAUPTMENUE));
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

        ColumnConstraints spalte1 = new ColumnConstraints();
        spalte1.setPercentWidth(23.0);
        ColumnConstraints spalte2 = new ColumnConstraints();
        spalte2.setPercentWidth(1.5);
        ColumnConstraints spalte3 = new ColumnConstraints();
        spalte3.setPercentWidth(23.0);
        ColumnConstraints spalte4 = new ColumnConstraints();
        spalte4.setPercentWidth(5.0);
        ColumnConstraints spalte5 = new ColumnConstraints();
        spalte5.setPercentWidth(23.0);
        ColumnConstraints spalte6 = new ColumnConstraints();
        spalte6.setPercentWidth(1.5);
        ColumnConstraints spalte7 = new ColumnConstraints();
        spalte7.setPercentWidth(23.0);
        raster.getColumnConstraints().addAll(
                spalte1,
                spalte2,
                spalte3,
                spalte4,
                spalte5,
                spalte6,
                spalte7
        );


        // Ueberschrift
        Text ueberschrift = new Text("LOKALES SPIEL");
        ueberschrift.getStyleClass().add("ueberschrift");
        raster.add(ueberschrift,0,0,7,1);


        // Anleitung
        Text anleitung = new Text(
                "ENTSCHEIDE, WELCHER SPIELERTYP AUF WELCHEM SPIELFELD SPIELEN SOLL.\n" +
                "DU KANNST AUF JEDEM SPIELFELD ZWISCHEN VIER OPTIONEN WÄHLEN: MENSCHLICHER SPIELER, KI1 (LEICHT), KI2 (MITTEL) und KI3 (SCHWER).\n" +
                "WENN DU MENSCHLICHER SPIELER AUSWÄHLST, GIB ZUSÄTZLICH NOCH EINEN SPIELERNAMEN EIN UND KLICKE ABSCHLIESSEND AUF \"WEITER\"."
        );
        anleitung.lineSpacingProperty().bind(gui.getSchriftgroesse().divide(4));
        //raster.add(anleitung,0,1,7,1);
        GridPane.setValignment(anleitung, VPos.TOP);


        // Optionen fuer Selectboxen
        ObservableList<String> spielertypen = FXCollections.observableArrayList(
                "MENSCHLICHER SPIELER",
                "KI1 (LEICHT)",
                "KI2 (MITTEL)",
                "KI3 (SCHWER)"
        );


        // Spielertyp Links
        this.spielertypLinksChoiceBox = new ChoiceBox<>(spielertypen);
        this.spielertypLinksChoiceBox.setValue("MENSCHLICHER SPIELER");
        this.spielertypLinksChoiceBox.setOnAction(e -> {
            this.aktualisiereSpielernamePreset(this.spielertypLinksChoiceBox, this.spielernameLinksTextfeld);
            this.aktualisiereWeiterButton();
        });
        LabelMitNodeVertikal<ChoiceBox<String>> spielertypLinksLabelMitChoiceBox = new LabelMitNodeVertikal<>(
                "SPIELERTYP LINKS",
                this.spielertypLinksChoiceBox
        );
        this.spielertypLinksChoiceBox.setMaxWidth(Double.MAX_VALUE);
        raster.add(spielertypLinksLabelMitChoiceBox,0,2);



        // Spielername Links
        LabelMitNodeVertikal<TextField> spielernameLinksLabelMitTextField = new LabelMitNodeVertikal<>(
                "SPIELERNAME LINKS",
                this.spielernameLinksTextfeld = new TextField()
        );
        this.spielernameLinksTextfeld.textProperty().addListener((ov, alterWert, neuerWert) -> {
            this.spielernameLinksTextfeld.setText(
                    neuerWert.substring(0, Math.min(MAXIMALE_LAENGE_SPIELERNAMEN, neuerWert.length())).toUpperCase()
            );
            this.aktualisiereWeiterButton();
        });
        raster.add(spielernameLinksLabelMitTextField, 2,2);



        // Spielertyp Rechts
        this.spielertypRechtsChoiceBox = new ChoiceBox<>(spielertypen);
        this.spielertypRechtsChoiceBox.setValue("MENSCHLICHER SPIELER");
        this.spielertypRechtsChoiceBox.setOnAction(e -> {
            this.aktualisiereSpielernamePreset(this.spielertypRechtsChoiceBox, this.spielernameRechtsTextfeld);
            this.aktualisiereWeiterButton();
        });
        LabelMitNodeVertikal<ChoiceBox<String>> spielertypRechtsLabelMitChoiceBox = new LabelMitNodeVertikal<>(
                "SPIELERTYP RECHTS",
                this.spielertypRechtsChoiceBox
        );
        this.spielertypRechtsChoiceBox.setMaxWidth(Double.MAX_VALUE);
        raster.add(spielertypRechtsLabelMitChoiceBox,4,2);



        // Spielername Rechts
        LabelMitNodeVertikal<TextField> spielernameRechtsLabelMitTextField = new LabelMitNodeVertikal<>(
                "SPIELERNAME RECHTS",
                this.spielernameRechtsTextfeld = new TextField()
        );
        this.spielernameRechtsTextfeld.textProperty().addListener((ov, alterWert, neuerWert) -> {
            this.spielernameRechtsTextfeld.setText(
                    neuerWert.substring(0, Math.min(MAXIMALE_LAENGE_SPIELERNAMEN, neuerWert.length())).toUpperCase()
            );
            this.aktualisiereWeiterButton();
        });
        raster.add(spielernameRechtsLabelMitTextField, 6,2);


        // WeiterButton
        this.weiterButton = new Button("WEITER");
        this.weiterButton.getStyleClass().add("grosserButton");
        this.weiterButton.setOnMouseClicked(e -> einstellungenWerfen());
        raster.add(this.weiterButton,6,3);
        GridPane.setHalignment(this.weiterButton, HPos.RIGHT);


        // Am Anfang direkt Spielernamenpresets und Weiter Button aktualisieren
        this.aktualisiereSpielernamePreset(this.spielertypLinksChoiceBox, this.spielernameLinksTextfeld);
        this.aktualisiereSpielernamePreset(this.spielertypRechtsChoiceBox, this.spielernameRechtsTextfeld);
        this.aktualisiereWeiterButton();
    }


    @Override
    public void aktualisieren() {
        this.spielertypLinksChoiceBox.setValue("MENSCHLICHER SPIELER");
        this.spielertypRechtsChoiceBox.setValue("MENSCHLICHER SPIELER");
        this.spielernameLinksTextfeld.setText("");
        this.spielernameRechtsTextfeld.setText("");
    }


    /**
     * Deaktiviert den WeiterButton, wenn in einem der Namensfeld kein Text ist.
     */
    private void aktualisiereWeiterButton() {
        this.weiterButton.setDisable(this.spielernameLinksTextfeld.getText().equals("") || this.spielernameRechtsTextfeld.getText().equals(""));
    }


    /**
     * Setzt Eventdaten und feuert ein Event an die Spiellogik.
     */
    private void einstellungenWerfen() {

        // Eventdaten setzen
        gui.setSpielernameLinks(this.spielernameLinksTextfeld.getText());
        gui.setSpielernameRechts(this.spielernameRechtsTextfeld.getText());
        gui.setSpielertypLinks(spielertyp(this.spielertypLinksChoiceBox.getValue()));
        gui.setSpielertypRechts(spielertyp(this.spielertypRechtsChoiceBox.getValue()));

        // BUMM
        gui.feuerEvent(Enums.GameAction.LOKALE_SPIELER_ABRUFBAR, "");
    }
}