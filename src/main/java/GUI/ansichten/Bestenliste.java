package GUI.ansichten;

import Administration.Enums.EAction;
import GUI.Ansicht;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

import java.util.LinkedList;


/** Ansicht mit Bestenliste */
public class Bestenliste extends GUI.ansichten.Ansicht {


    /** Tabelleneintraege */
    private final ObservableList<Bestenlisteneintrag> tabelleneintraege;

    /** Eintrage (Sammelt erstmal die Eintraege, damit sie später nicht soppel drinstehen*/
    private final LinkedList<Bestenlisteneintrag> eintraege;



    /**
     * Erstellt eine Ansicht zum Auswaehlen des Netzwerkmodus
     */
    public Bestenliste() {

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
        inhaltZeile2.setPercentHeight(60.0);
        RowConstraints inhaltZeile3 = new RowConstraints();
        inhaltZeile3.setPercentHeight(10.0);
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
        Text ueberschrift = new Text("BESTENLISTE");
        ueberschrift.getStyleClass().add("ueberschrift");
        raster.add(ueberschrift,0,0,6,1);


        // Tabelle
        TableView<Bestenlisteneintrag> tabelle = new TableView<>();
        tabelle.setPlaceholder(new Label("ES GIBT AKTUELL KEINE EINTRÄGE."));

        TableColumn<Bestenlisteneintrag,Integer> rangTableColumn = new TableColumn<>("RANG");
        rangTableColumn.setSortable(false);
        rangTableColumn.setResizable(false);
        rangTableColumn.setCellValueFactory(new PropertyValueFactory<>("rang"));
        rangTableColumn.minWidthProperty().bind(tabelle.widthProperty().divide(100).multiply(20));
        rangTableColumn.prefWidthProperty().bind(tabelle.widthProperty().divide(100).multiply(20));
        rangTableColumn.maxWidthProperty().bind(tabelle.widthProperty().divide(100).multiply(20));
        tabelle.getColumns().add(rangTableColumn);

        TableColumn<Bestenlisteneintrag,String> nameTableColumn = new TableColumn<>("NAME");
        nameTableColumn.setSortable(false);
        nameTableColumn.setResizable(false);
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameTableColumn.minWidthProperty().bind(tabelle.widthProperty().divide(100).multiply(30));
        nameTableColumn.prefWidthProperty().bind(tabelle.widthProperty().divide(100).multiply(30));
        nameTableColumn.maxWidthProperty().bind(tabelle.widthProperty().divide(100).multiply(30));
        tabelle.getColumns().add(nameTableColumn);

        TableColumn<Bestenlisteneintrag,Integer> scoreTableColumn = new TableColumn<>("SCORE");
        scoreTableColumn.setSortable(false);
        scoreTableColumn.setResizable(false);
        scoreTableColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreTableColumn.minWidthProperty().bind(tabelle.widthProperty().divide(100).multiply(30));
        scoreTableColumn.prefWidthProperty().bind(tabelle.widthProperty().divide(100).multiply(30));
        scoreTableColumn.maxWidthProperty().bind(tabelle.widthProperty().divide(100).multiply(30));
        tabelle.getColumns().add(scoreTableColumn);

        this.tabelleneintraege = FXCollections.observableArrayList();
        tabelle.setItems(this.tabelleneintraege);
        this.eintraege = new LinkedList<>();

        raster.add(tabelle,0,1);


        // "Leeren"-Button
        Button leerenButon = new Button("BESTENLISTE LEEREN");
        raster.add(leerenButon,0,2);
        leerenButon.setOnAction(e -> {
            this.tabelleneintraege.clear();
            gui.feuerEvent(EAction.BESTENLISTE_LEEREN,"");
        });
        GridPane.setHalignment(leerenButon, HPos.RIGHT);
    }


    /**
     * Dateneintrag in der Tabelle
     */
    public class Bestenlisteneintrag { // Ja, das muss public -.-

        private final SimpleIntegerProperty rang;
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty score;

        private Bestenlisteneintrag(int rang, String name, int score) {
            this.rang = new SimpleIntegerProperty(rang);
            this.name = new SimpleStringProperty(name);
            this.score = new SimpleIntegerProperty(score);
        }

        public int getRang() {
            return rang.get();
        }


        public void setRang(int rang) {
            this.rang.set(rang);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public int getScore() {
            return score.get();
        }

        public void setScore(int score) {
            this.score.set(score);
        }
    }


    @Override
    public void aktualisieren() {
        // Tabelle leeren
        this.tabelleneintraege.clear();

        // Neue Eintrage aus Zwischentabelle reinladen
        while (!eintraege.isEmpty()) {
            this.tabelleneintraege.add(eintraege.poll());
        }
    }

    /**
     * Schreibt einen Eintrag in die Bestenliste.
     * @param rang          Rang
     * @param spielername   Spielername
     * @param score         Punktestand
     */
    public void setEintrag(int rang, String spielername, int score) {
        this.eintraege.add(new Bestenlisteneintrag(rang, spielername, score));
    }

}
