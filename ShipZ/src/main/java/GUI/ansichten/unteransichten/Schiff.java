package GUI.ansichten.unteransichten;

import Administration.Enums;
import GUI.Schiffszustand;
import GUI.ShipzGui;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Darstellung eines Schiffs im Spielraster
 */
public class Schiff extends Pane {

    // CV
    /** Map von Schiffsimages, indiziert durch Dateinamen als String */
    private static final Map<String, Image> schiffsbilder = new HashMap<>();

    /** Zum Events Werfen */
    private static final ShipzGui gui = ShipzGui.getInstance();

    /** Pseudoklasse zum Blinken von falsch pltzierten Schiffen */
    private static final PseudoClass blink = PseudoClass.getPseudoClass("blink");

    /** Animiert alle falsch positionierten Schiffe in der gleichen Frequenz */
    private static Timeline blinker;

    /** Enthaelt alle falsch positionierten Schiffe, um diese zu animieren */
    private static final HashSet<Schiff> falschPositionierteSchiffe =  new HashSet<>();


    // IV
    /** ID des Schiffs */
    private final int id;

    /** Typ des Schiffs in der Nummernkodierung der Spiellogik */
    private final Enums.ShipTyp typ;

    /** Rotation des Schiffs */
    private final int rotation;

    /** X-Koordinate im Spielraster */
    private int x;

    /** Y-Koordinate im Spielraster */
    private int y;

    /** Zustand des Schiffs */
    private Schiffszustand schiffszustand;

    /** Wird für Verschiebung bei Rotation gebraucht */
    private final int breite;
    private final int laenge;

    /** Bild des Schiffs */
    private final ImageView bild;

    // IV fuer Drag & Drop
    /** Spielraster, in welchem sich das Schiff befindet */
    private final Spielraster spielraster;

    /** Ursprungsposition der Maus während des Ziehens (x-Koordinate) */
    private double altesX;

    /** Ursprungsposition der Maus während des Ziehens (y-Koordinate) */
    private double altesY;

    /** Versatz beim Verziehen auf X-Achse */
    private double verschiebungX;

    /** Versatz beim Verziehen auf Y-Achse */
    private double verschiebungY;


    /**
     * Läd alle Bilder aus dem Unterordner /schiffe des aktuellen Themes in die statische Map schiffsbilder
     */
    public static void schiffsbilderLaden() {

        for(final Enums.ShipTyp schiffsTyp : Enums.ShipTyp.values()) {
            for(final Schiffszustand schiffszustand : Schiffszustand.values()) {
                final String dateiname = (schiffsTyp + "_" + schiffszustand).toLowerCase();
                try {
                    final Image bild = new Image(Schiff.gui.getThemepfad() + "images/schiffe/" + dateiname + ".png");
                    Schiff.schiffsbilder.put(dateiname, bild);

                } catch (final IllegalArgumentException e) {
                    Schiff.gui.debugNachricht("Bild mit dem Dateinamen "+ dateiname + ".png konnte nicht gefunden werden.", true);
                }
            }
        }

        // Schiffsbilderordner zu referenzieren
        /*File schiffsbilderordner = null;
        try {
            schiffsbilderordner = new File(ShipzGui.class.getResource(gui.getThemepfad() + "images/schiffe").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Error, wenn Referenz kein Ordner ist
        if (!schiffsbilderordner.isDirectory()) {
            gui.debugNachricht("Schiffbilderordner ist kein Ordner.", true);
            return;
        }

        // Alle Dateien im Ordner durchgehen
        for (File datei : schiffsbilderordner.listFiles()) {

            // Kompletter Dateiname
            String dateiname = datei.getName();

            // Dateiendung abschneiden
            int endungsIndex = dateiname.lastIndexOf('.');
            String dateiendung = dateiname.substring(endungsIndex + 1);
            String dateinameOhneEndung = dateiname.substring(0, endungsIndex);

            // Prüfe, ob es sich um ein png handelt
            if (dateiendung.equals("png")) {
                gui.debugNachricht("Schiffbild gefunden: " + dateinameOhneEndung, false);
                try {
                    // Datei in Map laden
                    schiffsbilder.put(dateinameOhneEndung, new Image(String.valueOf(datei.toURI().toURL())));
                } catch (MalformedURLException e) {
                    gui.debugNachricht("Fehler beim formatieren des Schiffsbildpfades... sollte nie auftreten.", true);
                }
            }
        }*/
    }


    /**
     * Startet einen Keyframe, welcher alle falsch positionierten Schiffe zum Blinken bringt.
     */
    public static void blinkerStarten() {

        // Pseudklasse fuer alle falsche positioierten Schiffe
        // immer wieder entfernen und wieder hinzufuegen
        Schiff.blinker = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    Schiff.falschPositionierteSchiffe.forEach(schiff -> {
                        schiff.pseudoClassStateChanged(Schiff.blink, true);
                    });
                }),

                new KeyFrame(Duration.seconds(1.0), e -> {
                    Schiff.falschPositionierteSchiffe.forEach(schiff -> {
                        schiff.pseudoClassStateChanged(Schiff.blink, false);
                    });
                })
        );
        Schiff.blinker.setCycleCount(Animation.INDEFINITE);
        Schiff.blinker.play();
    }




    /**
     * Konstruktor Schiff
     * @param spielraster       Spielraster, in welches das Schiff geschrieben werden soll
     * @param id                ID des Schiffes
     * @param x                 X-Koordinate Ecke Oben Links
     * @param y                 Y-Koordinate Ecke Oben Links
     * @param typ               Typ des Schiffs in der Nummernkodierung der Spiellogik
     * @param rotation          Rotation des Schiffs, nur 0, 90, 180 und 270 erlaubt
     * @param schiffszustand    Optischer Zustand des Schiffs
     * @param spielraster       Spielraster, in welchem sich das Schiff befindet
     */
    public Schiff(final Spielraster spielraster, final int id, final int x, final int y, final Enums.ShipTyp typ, final int rotation, final Schiffszustand schiffszustand) {
        this.id = id;
        this.typ = typ;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.spielraster = spielraster;

        // Dimensionen des Schiffes je nach Typ festlegen
        // Das ist Codeduplikation! Der gleiche Kram steht nochmal in der Verwaltung...
        // Sowas sollte man in einem Enum "Schiffstyp" packen, worauf alle zugreifen können.
        breite = typ.getLaenge();
        laenge = typ.getBreite();


        // Bild via Zustand setzen
        bild = new ImageView();
        getChildren().add(bild);
        this.setzeSchiffszustand(schiffszustand);

        // Bild rotieren
        bild.getTransforms().add(new Rotate(rotation, 0, 0));

        switch (rotation) {
            case 0:
                // Größe binden
                bild.fitWidthProperty().bind(widthProperty());
                bild.fitHeightProperty().bind(heightProperty());
                break;

            case 90:
                // Größe invertiert binden
                bild.fitWidthProperty().bind(heightProperty());
                bild.fitHeightProperty().bind(widthProperty());

                // Versetzen
                bild.translateXProperty().bind(widthProperty());
                break;

            case 180:
                // Größe binden
                bild.fitWidthProperty().bind(widthProperty());
                bild.fitHeightProperty().bind(heightProperty());

                // Versetzen
                bild.translateXProperty().bind(widthProperty());
                bild.translateYProperty().bind(heightProperty());
                break;

            case 270:
                // Größe invertiert binden
                bild.fitWidthProperty().bind(heightProperty());
                bild.fitHeightProperty().bind(widthProperty());

                // Versetzen
                bild.translateYProperty().bind(heightProperty());
                break;
        }

        // Klicks bei transparenten Bildfläche durchreichen
        bild.setPickOnBounds(false);

        // Klicks für Schiffspane komplett durchreichen
        setPickOnBounds(false);



        // Drag & Drop
        if (
            (Schiffszustand.VERZIEHBAR == this.schiffszustand) ||
            (Schiffszustand.VERZIEHBAR_FALSCH_POSITIONIERT == this.schiffszustand)
        ) {

            // CSS-Klasse für zb. Cursor-Hover
            getStyleClass().add("verziehbar");


            // Beim Draufklicken
            bild.setOnMousePressed(e -> {

                // Screenkoordinaten der Mausposition speichern
                altesX = e.getScreenX();
                altesY = e.getScreenY();
            });


            // Während des Ziehens
            bild.setOnMouseDragged(e -> {

                // Schiff mit der Mausbewegung verschieben
                    // Neue Mauskoordintaten speichern
                    final double neuesX = e.getScreenX();
                    final double neuesY = e.getScreenY();

                    // Versatz aktualisieren
                    verschiebungX += (neuesX - altesX);
                    verschiebungY += (neuesY - altesY);

                    // Schiff um Versatz verschieben
                    getTransforms().clear();
                    getTransforms().add(Transform.translate(verschiebungX, verschiebungY));


                // Begrenzungen vom Spielfeldrahmen und Schiff in Szenekoordinaten speichern
                final Bounds schiffBegrenzung = localToScene(getLayoutBounds());
                final Bounds spielfeldBegrenzung = this.spielraster.getRahmenBegrenzung();


                // Zellengroesse aus dem Zielspielraster popeln
                final double zellengroesse = this.spielraster.getZellengroesse().doubleValue();

                // Position des Schiffes im Koordinatensystem des Zielspielfeldes berechnen
                final double zielrasterX = schiffBegrenzung.getMinX() - spielfeldBegrenzung.getMinX();
                final double zielrasterY = schiffBegrenzung.getMinY() - spielfeldBegrenzung.getMinY();

                // Snapping
                    // Gesnappte Position des Schiffes im Koordinatensystem des Zielspielfeldes berechnen
                    final int zielkoordinateX = (int)Math.round(zielrasterX / zellengroesse);
                    final int zielkoordinateY = (int)Math.round(zielrasterY / zellengroesse);

                    // Passt das Schiff mit den gesnappten Koordinaten ins Spielraster?
                    if(passtInsSpielraster(this.rotation, zielkoordinateX, zielkoordinateY)) {

                        // Unterschied zwischen gesnappten und nicht gesnappten Koordinaten berechnen
                        final double snapUnterschiedX = (zielkoordinateX * zellengroesse) - zielrasterX;
                        final double snapUnterschiedY = (zielkoordinateY * zellengroesse) - zielrasterY;

                        // Diesen Unterschied als zusaetzliche Transformation hinzufuegen
                        getTransforms().add(Transform.translate(snapUnterschiedX, snapUnterschiedY));

                    }

                // Alte Koordinaten aktualisieren
                altesX = neuesX;
                altesY = neuesY;
            });


            // Beim Loslassen
            bild.setOnMouseReleased(e -> {

                // Wenn das Schiff nicht bewegt worden ist (3px Toleranz)
                if (3 > Math.abs(verschiebungX) && 3 > Math.abs(verschiebungY)) {

                    // Neue Rotation berechnen
                    final int neueRotation = (this.rotation + 90) % 360;

                    // Prüfe, ob das Schiff rotiert noch ins Spielraster passen würde
                    if(passtInsSpielraster(neueRotation,this.x,this.y)) {

                        // Schiff aus aktuellem Grid löschen
                        this.spielraster.entferneSchiff(this.id);

                        // Schiff rotiert in das Grid schreiben
                        this.spielraster.setzeSchiff(this.id, this.x, this.y, this.typ, neueRotation, this.schiffszustand);

                        // Event werfen
                        final ShipzGui gui = ShipzGui.getInstance();
                        gui.setSchiffsId(this.id);
                        gui.setRotation(neueRotation);
                        gui.feuerEvent(Enums.GameAction.SCHIFF_ROTIERT, "");
                    }

                } else { // Wenn das Schiff bewegt worden ist

                    // Zellengroesse aus dem Spielraster popeln
                    final double zellengroesse = this.spielraster.getZellengroesse().doubleValue();

                    // Begrenzungen von Spielfeldrahmen und Schiff als Szenekoordinaten speichern
                    final Bounds zielrasterBegrenzung = this.spielraster.getRahmenBegrenzung();
                    final Bounds schiffBegrenzung = localToScene(getLayoutBounds());

                    // Position des Schiffes im Koordinatensystem des Zielspielfeldes berechnen
                    final double zielrasterX = schiffBegrenzung.getMinX() - zielrasterBegrenzung.getMinX();
                    final double zielrasterY = schiffBegrenzung.getMinY() - zielrasterBegrenzung.getMinY();

                    // Gesnappte Position des Schiffes im Koordinatensystem des Zielspielfeldes berechnen
                    final int zielkoordinateX = (int)Math.round(zielrasterX / zellengroesse);
                    final int zielkoordinateY = (int)Math.round(zielrasterY / zellengroesse);

                    // Passt das Schiff mit den gesnappten Koordinaten ins Spielraster?
                    if(passtInsSpielraster(this.rotation, zielkoordinateX, zielkoordinateY)) {

                        // Schiff im Raster verschieben
                        this.spielraster.verschiebeSchiff(this.id, zielkoordinateX, zielkoordinateY);

                        // Interne Koordinaten aktualisieren
                        this.x = zielkoordinateX;
                        this.y = zielkoordinateY;

                        // Event werfen
                        final ShipzGui gui = ShipzGui.getInstance();
                        gui.setSchiffsId(this.id);
                        gui.setzeKoordinaten(this.x, this.y);
                        gui.feuerEvent(Enums.GameAction.SCHIFF_BEWEGT, "");

                        // Debug Nachricht absetzen
                        gui.debugNachricht("Schiff losgelassen bei Koordinate: " + zielkoordinateX + ", " + zielkoordinateY, false);

                    }

                    // Transform entfernen
                    getTransforms().clear();

                    // Verschiebung zurücksetzen
                    verschiebungX = 0;
                    verschiebungY = 0;
                }

            });

        }
    }



    /**
     * Ändert du Zustand eines Schiffs
     * @param schiffszustand    Neuer Zustand des Schiffes
     */
    void setzeSchiffszustand(final Schiffszustand schiffszustand) {

        // IV setzen
        this.schiffszustand = schiffszustand;

        // Passendes Bild aus Map laden
        final Image bild = Schiff.schiffsbilder.get((this.typ + "_" +  schiffszustand).toLowerCase());
        if (null != bild) {

            // ImageView mit Bild versehen
            this.bild.setImage(bild);

        } else {
            Schiff.gui.debugNachricht("Bild " + (this.typ + "_" +  schiffszustand).toLowerCase() + " konnte nicht gefunden werden.",true);
        }

        // Falsche positionierte Schiffe blinken lassen
        if(Schiffszustand.VERZIEHBAR_FALSCH_POSITIONIERT == this.schiffszustand) {
            Schiff.falschPositionierteSchiffe.add(this);
        } else {
            Schiff.falschPositionierteSchiffe.remove(this);
            pseudoClassStateChanged(Schiff.blink, false);
        }

    }


    /**
     * Entfernt this aus dem statischen Set falschPositionierteSchiffe
     */
    void entferneAusFalschPositionierteSchiffe() {
        Schiff.falschPositionierteSchiffe.remove(this);
    }


//    /**
//     * Gibt die Breite zu einem Schiffstyp zurück.
//     * @param typ   Typ des Schiffs
//     * @return      Breite des Typs
//     */
//    private static int breiteVonTyp(final int typ) {
//        switch (typ) {
//            case 1:
//                return 1;
//            case 2:
//                return 2;
//            case 3:
//                return 3;
//            case 4:
//                return 4;
//            case 5:
//                return 5;
//            case 6:
//                return 3;
//            case 7:
//                return 3;
//            case 8:
//                return 4;
//            default:
//                Schiff.gui.debugNachricht("Es ist kein Schiffstyp mit der ID " + typ + " hinterlegt.", true);
//                return 0;
//        }
//    }
//
//
//    /**
//     * Gibt die Breite zu einem Schiffstyp zurück.
//     * @param typ   Typ des Schiffs
//     * @return      Breite des Typs
//     */
//    private static int hoeheVonTyp(final int typ) {
//        switch (typ) {
//            case 1:
//                return 1;
//            case 2:
//                return 1;
//            case 3:
//                return 1;
//            case 4:
//                return 1;
//            case 5:
//                return 1;
//            case 6:
//                return 2;
//            case 7:
//                return 3;
//            case 8:
//                return 2;
//            default:
//                Schiff.gui.debugNachricht("Es ist kein Schiffstyp mit der ID " + typ + " hinterlegt.", true);
//                return 0;
//        }
//    }

    /**
     * Prüft, ob ein Schiff mit gegebener Rotation und Koordinaten in sein Spielraster passen würde.
     * @param rotation Rotation (0, 90, 180, 270)
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @return  True, wenn es reinpasst, sonst false
     */
    private boolean passtInsSpielraster(final int rotation, final int x, final int y) {

        // Prüfe linke und obere Kante des Spielfelds
        if(0 <= x && 0 <= y) {

            // Prüfe rechte und untere Kante des Spielfeld
            switch (rotation) {
                case 0:
                case 180:
                    if ((x + breite) <= spielraster.getBreite()) {
                        if ((y + laenge) <= spielraster.getHoehe()) {
                            return true;
                        }
                    }
                    break;
                case 90:
                case 270:
                    if ((x + laenge) <= spielraster.getBreite()) {
                        if ((y + breite) <= spielraster.getHoehe()) {
                            return true;
                        }
                    }
                    break;
            }
        }

        return false;
    }

    /**
     * Getter Breite
     * @return Breite des Schiffs
     */
    int getBreite() {
        return breite;
    }

    /**
     * Getter Hoehe
     * @return  Hoehe des Schiffs
     */
    int getLaenge() {
        return laenge;
    }


    /**
     * Gibt eine abgespecktes Schiffsobjekt fuer die Schiffsliste zurueck.
     * @param typ   Typ des Schiffs
     * @param id    ID des Schiffs
     * @return      Schiff fuer Schiffsliste
     */
    static SchiffslisteSchiff getSchiffslisteschiff(final Enums.ShipTyp typ, final int id) {
        return new SchiffslisteSchiff(typ, id);
    }

    /** Abgespeckte Version eines Schiffs fuer die Schiffsliste */
    static class SchiffslisteSchiff extends Pane implements Comparable<SchiffslisteSchiff> {

        /** Breite des Schiffs */
        final int breite;

        /** Hoehe des Schiffs */
        final int hoehe;

        /** Id des Schiffs */
        final int id;

        /** Pfad zu Bild nicht versenkt */
        private final String bildPfadNichtVersenkt;

        /** Pfad zu Bild versenkt */
        private final String bildPfadVersenkt;

        /**
         * Erstellt ein MiniSchiff fuer die Schiffsliste mit spezifiziertem Typ.
         * @param typ Typ für das MiniSchiff.
         * @param id Id für das MiniSchiff.
         */
        private SchiffslisteSchiff(Enums.ShipTyp typ, final int id) {
            this.id = id;
            this.breite = typ.getLaenge();
            this.hoehe = typ.getBreite();
            this.bildPfadNichtVersenkt = typ.toString().toLowerCase() + "_schiffsliste";
            this.bildPfadVersenkt = typ.toString().toLowerCase() + "_schiffsliste_versenkt";
            setzteVersenkt(false);
        }

        /**
         * (De-)Markiert dieses Schiff als versenkt.
         * @param versenkt True für versenkt, sonst False.
         */
        void setzteVersenkt(final boolean versenkt) {
            String backgroundImage = "";
            if (versenkt) {
                try {
                    backgroundImage = ShipzGui.class.getResource(Schiff.gui.getThemepfad() + "images/schiffe/" + this.bildPfadVersenkt + ".png").toExternalForm();
                } catch(final NullPointerException e) {
                    Schiff.gui.debugNachricht("Schiffsggrafik fuer Schiffsliste nicht gefunden.", true);
                }
            } else {
                try {
                    backgroundImage = ShipzGui.class.getResource(Schiff.gui.getThemepfad() + "images/schiffe/" + this.bildPfadNichtVersenkt + ".png").toExternalForm();
                } catch(final NullPointerException e) {
                    Schiff.gui.debugNachricht("Schiffsggrafik fuer Schiffsliste nicht gefunden.", true);
                }
            }


            setStyle(
                    "-fx-background-image: url(\"" + backgroundImage + "\");" +
                    "-fx-background-position: center center;" +
                    "-fx-background-size: 100% 100%;"
            );
        }

        @Override
        public int compareTo(final SchiffslisteSchiff other) {
            final int ergebnis = Integer.compare(other.breite, breite);
            if (0 != ergebnis) {
                return ergebnis;
            } else {
                return Integer.compare(other.hoehe, hoehe);
            }
        }
    }

}
