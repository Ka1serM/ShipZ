package GUI.ansichten;


import Administration.Enums;
import GUI.ansichten.unteransichten.LabelMitNodeVertikal;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;


/**
 * Diese Ansicht wird zeigt die IP dis Hostrechners und den offenen Port an.
 */
public class WarteAufClient extends GUI.ansichten.Ansicht {

    // IV
    /** IP-Adresse, auf welcher sich dern Client connceten soll */
    private final SimpleStringProperty ip;

    /** Port, welcher beim Host geoffnet ist */
    private final SimpleStringProperty port;


    /**
     * Erstellt eine Ansicht, welche die IP dis Hostrechners und den offenen Port anzeigt.
     */
    public WarteAufClient() {

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
        inhaltZeile2.setPercentHeight(15.0);
        RowConstraints inhaltZeile3 = new RowConstraints();
        inhaltZeile3.setPercentHeight(15.0);
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
        Text ueberschrift = new Text("WARTE AUF CLIENT");
        ueberschrift.getStyleClass().add("ueberschrift");
        raster.add(ueberschrift,0,0,6,1);


        // Anleitung
        Text anleitung = new Text(
                "WARTE AUF CLIENT UM EINE VERBINDUNG AUFZUBAUEN."
        );
        anleitung.lineSpacingProperty().bind(gui.getSchriftgroesse().divide(4));
        //raster.add(anleitung,0,1,6,1);
        GridPane.setValignment(anleitung, VPos.TOP);


        // IP
        Text ipText = new Text();
        this.ip = new SimpleStringProperty("-");
        ipText.textProperty().bind(this.ip);
        ipText.getStyleClass().add("sehrGross");
        LabelMitNodeVertikal ipLabelmitText = new LabelMitNodeVertikal<>("IPV4-ADRESSE", ipText);


        // Port
        Text portText = new Text();
        this.port = new SimpleStringProperty("-");
        portText.textProperty().bind(this.port);
        portText.getStyleClass().add("sehrGross");
        LabelMitNodeVertikal portLabelmitText = new LabelMitNodeVertikal<>("PORT", portText);


        // IP und Port verbinden
        HBox ipUndPort = new HBox();
        ipUndPort.getChildren().addAll(ipLabelmitText, portLabelmitText);
        ipUndPort.spacingProperty().bind(gui.getSchriftgroesse().multiply(2));
        raster.add(ipUndPort, 0,2);


        // AbbrechenButton
        Button netzwerkspielAbbrechenButton = new Button("ABBRECHEN");
        netzwerkspielAbbrechenButton.getStyleClass().add("grosserButton");
        netzwerkspielAbbrechenButton.setOnAction(e -> {
            gui.feuerEvent(Enums.GameAction.NETZWERKSPIEL_ABBRECHEN, "");
        });
        raster.add(netzwerkspielAbbrechenButton,0,3);


    } // Ende Konstruktor


    /**
     * Setzt die angezeigte IP und den Port.
     * @param ip    IP
     * @param port  Port
     */
    public void setVerbindungsdaten(String ip, int port) {
        this.ip.set(ip);
        this.port.set(Integer.toString(port));
    }

} // Ende Class
