package GUI.ansichten;

import Administration.Enums;
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
import javafx.scene.text.TextFlow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** Ansicht zum Konfigurieren des Netzwerk-Clients */
public class VerbindeMitSpiel extends GUI.ansichten.Ansicht {

    // IV
    /** Spielertyp */
    private final ChoiceBox<String> spielertypChoiceBox;

    /** Spielername */
    private TextField spielernameTextfeld;

    /** Textfeld fuer IP */
    private final TextField ipTextField;

    /** Textfeld fuer Port */
    private final TextField portTextField;

    /** Button zum Verbinden */
    private final Button verbindenButton;

    /** Maximale Zeichenanzahl der Spielernamen */
    private static final int MAXIMALE_LAENGE_SPIELERNAMEN = 20;

    /** Regex Pattern für eine IP */
    private static final Pattern IP_PATTERN = Pattern.compile(
            "(?<ip1>\\d{1,3})\\.(?<ip2>\\d{1,3})\\.(?<ip3>\\d{1,3})\\.(?<ip4>\\d{1,3})"
    );

    /** Regex Pattern für einen Port */
    private static final Pattern PORT_PATTERN = Pattern.compile(
            "(?<port>\\d{1,5})"
    );

    /** Text der angezeigt wird, wenn Verbindung nicht funktioniert */
    private final TextFlow verbindungNichtErfolgreichTextFlow;

    /** Raster fuer den Inhalt */
    private final GridPane raster;


    /**
     * Erstellt eine Ansicht zum Konfigurieren der des Netzwerk-Clients
     */
    public VerbindeMitSpiel() {

        // Buttonleiste
        final Button zurueckButton = new Button("ZURÜCK");
        zurueckButton.setOnMouseClicked(e -> wirfNaechsteAnsicht(GUI.Ansicht.NETZWERKMODUS_WAEHLEN));
        buttonleiste.add(zurueckButton);

        // Raster
        raster = new GridPane();
        add(this.raster, 1, 3,3,1);
        final RowConstraints inhaltZeile1 = new RowConstraints();
        inhaltZeile1.setPercentHeight(20.0);
        final RowConstraints inhaltZeile2 = new RowConstraints();
        inhaltZeile2.setPercentHeight(30.0);
        final RowConstraints inhaltZeile3 = new RowConstraints();
        inhaltZeile3.setPercentHeight(20.0);
        final RowConstraints inhaltZeile4 = new RowConstraints();
        inhaltZeile4.setPercentHeight(30.0);
        this.raster.getRowConstraints().addAll(
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
        final Text ueberschrift = new Text("MIT SPIEL VERBINDEN");
        ueberschrift.getStyleClass().add("ueberschrift");
        raster.add(ueberschrift,0,0,6,1);


        // Anleitung
        final Text anleitung = new Text(
                "ENTSCHEIDE, WELCHER SPIELERTYP AUF DEINEM SPIELFELD SPIELEN SOLL.\n" +
                "DU KANNST ZWISCHEN VIER OPTIONEN WAEHLEN: MENSCHLICHER SPIELER, KI1 (LEICHT), KI2 (MITTEL) und KI3 (SCHWER).\n" +
                "WENN DU MENSCHLICHER SPIELER AUSWAEHLST, GIB ZUSAETZLICH NOCH EINEN SPIELERNAMEN EIN.\n" +
                "\n" +
                "UM DICH MIT DEM HOST ZU VERBINDEN, GIB DIE IPV4-ADRESSE UND DEN PORT EIN, WELCHER AUF DEM MONITOR DES HOSTS ANGEZEIGT WIRD.\n" +
                "KLICKE ANSCHLIESSEND AUF \"VERBINDEN\"."
        );
        anleitung.lineSpacingProperty().bind(this.gui.getSchriftgroesse().divide(4));
        //raster.add(anleitung,0,1,6,1);
        setValignment(anleitung, VPos.TOP);



        // Spielertyp
        spielertypChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                "MENSCHLICHER SPIELER",
                "KI1 (LEICHT)",
                "KI2 (MITTEL)",
                "KI3 (SCHWER)"
        ));
        spielertypChoiceBox.setValue("MENSCHLICHER SPIELER");
        spielertypChoiceBox.setOnAction(e -> {
            aktualisiereSpielernamePreset(spielertypChoiceBox, spielernameTextfeld);
            aktualisiereVerbindenButton();
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
                    neuerWert.substring(0, Math.min(VerbindeMitSpiel.MAXIMALE_LAENGE_SPIELERNAMEN, neuerWert.length())).toUpperCase()
            );
            aktualisiereVerbindenButton();
        });


        // IP
        ipTextField = new TextField();
        ipTextField.textProperty().addListener((ov, alterWert, neuerWert) -> {
            // Loesche alle anderen Buchstaben als 0-9 und . aus der Eingabe
            if (!neuerWert.matches("(\\.\\d)*")) {
                ipTextField.setText(neuerWert.replaceAll("[^(\\.\\d)]", ""));
            }
            aktualisiereVerbindenButton();
        });
        ipTextField.prefWidthProperty().bind(this.gui.getSchriftgroesse().multiply(12));
        final LabelMitNodeVertikal<TextField> ipLabelMitTextField = new LabelMitNodeVertikal<>(
                "IPV4-ADRESSE",
                ipTextField
        );


        // Port
        portTextField = new TextField();
        portTextField.textProperty().addListener((ov, alterWert, neuerWert) -> {
            // Loesche alle anderen Buchstaben als 0-9 aus der Eingabe
            if (!neuerWert.matches("\\d*")) {
                portTextField.setText(neuerWert.replaceAll("[^\\d]", ""));
            }
            aktualisiereVerbindenButton();
        });
        portTextField.prefWidthProperty().bind(this.gui.getSchriftgroesse().multiply(6));
        final LabelMitNodeVertikal<TextField> portLabelMitTextField = new LabelMitNodeVertikal<>(
                "PORT",
                portTextField
        );

        // Verbinden Button
        verbindenButton = new Button("VERBINDEN");
        verbindenButton.setOnAction(e -> this.feuerDaten());
        verbindenButton.setAlignment(Pos.BOTTOM_LEFT);
        final LabelMitNodeVertikal<Button> verbindenButtonMitLabel = new LabelMitNodeVertikal<>(
                "",
                verbindenButton
        );


        // Inputs mit HBox verbinden
        final HBox inhalt = new HBox();
        inhalt.spacingProperty().bind(this.gui.getSchriftgroesse());
        inhalt.getChildren().addAll(
                spielertypLabelMitChoiceBox,
                spielernameLabelMitTextField,
                ipLabelMitTextField,
                portLabelMitTextField,
                verbindenButtonMitLabel
        );
        this.raster.add(inhalt, 0, 2);


        // Text fuer Verbindung nicht erfolgreich
        final Text verbindungNichtErfolgreichUeberschrift = new Text(
                "VERBINDUNGSVERSUCH NICHT ERFOLGREICH.\n"
        );
        verbindungNichtErfolgreichUeberschrift.getStyleClass().add("groesser");

        final Text verbindungNichtErfolgreichText = new Text(
                "\nDAS KANN VERSCHIEDENE GRUENDE HABEN:\n" +
                "- DIE EINGEGEBENE IP ODER PORT SIND NICHT KORREKT\n" +
                "- DIE RECHNER BEFINDEN SICH NICHT IM SELBEN LOKALEN NETZ\n" +
                "- DAS NETZWERKSPIEL WURDE ABGEBROCHEN"
        );
        verbindungNichtErfolgreichTextFlow = new TextFlow();
        verbindungNichtErfolgreichTextFlow.getChildren().addAll(
                verbindungNichtErfolgreichUeberschrift,
                verbindungNichtErfolgreichText
        );
        this.verbindungNichtErfolgreichTextFlow.lineSpacingProperty().bind(this.gui.getSchriftgroesse().divide(4));


        aktualisiereVerbindenButton();
    }


    /**
     * Zeigt einen Text an, dass die Verbindung nicht hergestellt werden konnte.
     */
    public void zeigeTextVerbindungNichtErfolreich() {
        raster.add(this.verbindungNichtErfolgreichTextFlow, 0,3);
        setValignment(this.verbindungNichtErfolgreichTextFlow, VPos.TOP);
    }


    @Override
    public void aktualisieren() {
        raster.getChildren().remove(this.verbindungNichtErfolgreichTextFlow);
    }


    /**
     * Deaktiviert den VerbindenButton, wenn IP oder Port nicht gueltig ist.
     */
    private void aktualisiereVerbindenButton() {

        verbindenButton.setDisable(true);

        // Wenn der Spielername ausgefuellt worden ist
        if(!"".equals(this.spielernameTextfeld.getText())) {

            // Matcher fuer die IP Adresse erstellen
            final Matcher matcherIp = VerbindeMitSpiel.IP_PATTERN.matcher(ipTextField.getText());

            // Matcher fuer den Port erstellen
            final Matcher matcherPort = VerbindeMitSpiel.PORT_PATTERN.matcher(portTextField.getText());

            // Wenn beide Matcher matchen IP zusammensetzen
            if (matcherIp.matches() && matcherPort.matches()) {
                final int ip1 = Integer.parseInt(matcherIp.group("ip1"));
                final int ip2 = Integer.parseInt(matcherIp.group("ip2"));
                final int ip3 = Integer.parseInt(matcherIp.group("ip3"));
                final int ip4 = Integer.parseInt(matcherIp.group("ip4"));
                final int port = Integer.parseInt(matcherPort.group("port"));

                // Wenn alle Nummern im gueltigen Bereich sind
                if (256 > ip1 && 256 > ip2 && 256 > ip3 && 256 > ip4 && 65536 > port) {
                    // System.out.println(ip1 + "." + ip2 + "." + ip3 + "." + ip4 + ":" + port);
                    verbindenButton.setDisable(false);
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
        this.gui.setIp(ipTextField.getText());
        this.gui.setPort((Integer.parseInt(portTextField.getText())));

        // Bumm
        this.gui.feuerEvent(Enums.GameAction.NETZWERKDATEN_CLIENT_ABRUFBBAR, "");
    }


}
