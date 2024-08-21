package GUI.ansichten;

import Administration.Enums.EAction;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;


/** Ansicht zum Auswaehlen des Netzwerkmodus */
public class SpielLaden extends GUI.ansichten.Ansicht {

    // IV
    /** Hier kommen alle Eintraege rein */
    private final FlowPane eintraegeFlowPane;

    /** Hier kommen alle Eintraege rein */
    private final LinkedList<SpielLadenEintrag> alleEintraege;



    /**
     * Erstellt eine Ansicht zum Auswaehlen des Netzwerkmodus
     */
    public SpielLaden() {

        this.alleEintraege = new LinkedList<>();

        // Buttonleiste
        Button zuruckButton = new Button("ZURÜCK");
        zuruckButton.setOnMouseClicked(e -> wirfMenueZurueck());
        this.buttonleiste.add(zuruckButton);

        // Raster
        GridPane raster = new GridPane();
        this.add(raster, 1, 3,3,1);
        RowConstraints inhaltZeile1 = new RowConstraints();
        inhaltZeile1.setPercentHeight(20.0);
        RowConstraints inhaltZeile2 = new RowConstraints();
        inhaltZeile2.setPercentHeight(15.0);
        RowConstraints inhaltZeile3 = new RowConstraints();
        inhaltZeile3.setPercentHeight(58.0);
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
        Text ueberschrift = new Text("SPIEL LADEN");
        ueberschrift.getStyleClass().add("ueberschrift");
        raster.add(ueberschrift,0,0,6,1);


        // Anleitung
        Text anleitung = new Text(
                "ALLE SPIELE SIND UNTER DEM ZEITPUNKTS ABGELEGT, AN DEM SIE GESPEICHERT WORDEN SIND.\n" +
                "KLICKE AUF \"LADEN\", UM DAS JEWEILIGE SPIEL ZU WEITERZUSPIELEN ODER AUF \"LÖSCHEN\" UM EIN GESPEICHERTES SPIEL ZU LÖSCHEN."
        );
        anleitung.lineSpacingProperty().bind(gui.getSchriftgroesse().divide(4));
        //raster.add(anleitung,0,1,6,1);
        GridPane.setValignment(anleitung, VPos.TOP);



        // Scrollbarer Content
        this.eintraegeFlowPane = new FlowPane();
        eintraegeFlowPane.vgapProperty().bind(gui.getSchriftgroesse().multiply(2));
        eintraegeFlowPane.hgapProperty().bind(gui.getSchriftgroesse().multiply(3));
        eintraegeFlowPane.paddingProperty().bind(
                Bindings.createObjectBinding(() -> new Insets(
                        gui.getSchriftgroesse().multiply(2).doubleValue()), gui.getSchriftgroesse()
                )
        );


        // ScrollPane
        ScrollPane scrollPane = new ScrollPane(eintraegeFlowPane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        raster.add(scrollPane,0,2);
        eintraegeFlowPane.prefWrapLengthProperty().bind(scrollPane.widthProperty().subtract(20.0));
    }


    /**
     * Laed alle SpielLadenEintrage in die Ansicht.
     * Wenn es keine Eintrage gibt, wird ein entsprechender Text angezeigt.
     */
    public void aktualisieren() {

        // Alte Eintraege entfernen
        this.eintraegeFlowPane.getChildren().clear();

        // Neue Eintraege laden
        if(this.alleEintraege.isEmpty()) {
            this.eintraegeFlowPane.getChildren().add(
                    new Text("ES WURDE NOCH KEIN SPIEL GESPEICHERT.")
            );
        } else {
            while (!this.alleEintraege.isEmpty()) {
                this.eintraegeFlowPane.getChildren().add(this.alleEintraege.poll());
            }
        }
    }


    /**
     * /**
     * Ergaenzt einen Eintrag in die Ansicht.
     * @param timecome Timecode des Eintrags
     */
    public void setSpielLadenEintrag(long timecome) {
        this.alleEintraege.add(new SpielLadenEintrag(timecome));
    }


    /**
     * Entfernt einen Eintrag aus dem Flowpane
     * @param spielLadenEintrag Zu entfernender Eintrag
     */
    private void entferneEintrag(SpielLadenEintrag spielLadenEintrag) {
        this.eintraegeFlowPane.getChildren().remove(spielLadenEintrag);
        if(this.eintraegeFlowPane.getChildren().isEmpty()) {
            this.eintraegeFlowPane.getChildren().add(
                    new Text("ES WURDE NOCH KEIN SPIEL GESPEICHERT.")
            );
        }
    }



    /**
     * Eintrag eines gespeicherten Spiels mit Laden- und Loeschen-Button.
     */
    private class SpielLadenEintrag extends HBox {

        /** Timecode aus der Datenhaltung */
        private final long timecode;


        /**
         * Erstellt einen Bestenlisteneintrag fuer die Ansicht SpielLaden
         * @param timecode  Datumslong
         */
        private SpielLadenEintrag(long timecode) {

            this.timecode = timecode;

            // Timecode zu lesbarem Datum machen
            Date datumDate = new Date(timecode);
            SimpleDateFormat datumsFormat = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat uhrzeitFormat = new SimpleDateFormat("H:mm");
            String datumString = datumsFormat.format(datumDate);
            String uhrzeitString = uhrzeitFormat.format(datumDate);

            // Label
            Label label = new Label(datumString + " - " + uhrzeitString + " UHR");
            label.setStyle(
                    "-fx-border-width: 0px 0px 1px 0px;" +
                    "-fx-border-color: #C9C9C9;" +
                    "-fx-padding: 0px 0px 5px 0px;"
            );


            // LadenButton
            Button ladenButton = new Button("LADEN");
            ladenButton.setOnAction(e -> feuerZuLadendesSpiel());

            // LadenButton
            Button loeschenButton = new Button("LÖSCHEN");
            loeschenButton.setOnAction(e -> feuerZuLoeschendesSpiel());

            // Buttons Horizontal anordnen
            HBox buttons = new HBox();
            buttons.getChildren().addAll(ladenButton, loeschenButton);
            buttons.spacingProperty().bind(gui.getSchriftgroesse().divide(2));

            // Alles horizontal anordnen
            this.getChildren().addAll(label, buttons);
            this.spacingProperty().bind(gui.getSchriftgroesse());
            this.setAlignment(Pos.CENTER);
        }


        /**
         * Feuert den timecode-Long zum Laden an die Spiellogik.
         */
        private void feuerZuLadendesSpiel() {

            // Daten setzen
            gui.setDatum(timecode);

            // Bumm
            gui.feuerEvent(EAction.SPIEL_LADEN, "");
        }

        /**
         * Feuert den timecode-Long zum Loeschen an die Spiellogik
         * und fordert Ansicht auf, sich aus Ansicht zu entfernen.
         */
        private void feuerZuLoeschendesSpiel() {

            // Daten setzen
            gui.setDatum(timecode);

            // Bumm
            gui.feuerEvent(EAction.SPIEL_LOESCHEN, "");

            // Aus der Ansicht entfernen
            entferneEintrag(this);
        }

    }

}
