package ArtificialIntelligence;

import Administration.Nachrichten.SpielfeldKonfiguration;
import Administration.Spieler;
import Administration.Spielfeld;
import Administration.Enums.EAction;
import Administration.Enums.ESchiffsTyp;
import Administration.Event;
import Administration.EventListener;

import java.util.*;

import Administration.SpielZug;
import GUI.ESpielertyp;
import GUI.Schusstyp;
import com.google.gson.Gson;

/**
 * AiSpieler player implementation for the game.
 */
public class AiSpieler extends Spieler {
    private static final int MaxRandomAttempts = 50;
    private final EventListener listener;
    public Spielfeld spielfeld;
    private final Random random;
    private int hoehe;
    private int breite;
    private SpielZug lastHit;
    private LinkedList<SpielZug> lastHitList; // Typ hinzufügen
    private final LinkedList<SpielZug> diagonalPatternPoints;
    private HashMap<ESchiffsTyp, Integer> schiffskonfiguration; //liste aller Schiffe

    /**
     * Initializes the AiSpieler with the given settings and event listener.
     * @param listener the event listener
     */
    public AiSpieler(EventListener listener, String name, ESpielertyp spielerTyp) {
        super(name, spielerTyp);
        this.listener = listener;
        this.random = new Random();
        this.lastHit = null;
        this.diagonalPatternPoints = new LinkedList<>();
        this.schiffskonfiguration = new HashMap<>();
        this.lastHitList = new LinkedList<>(); // Initialisiere die lastHitList
    }

    /**
     * Sets the configuration of the spielfeld by defining its height and width.
     * Initializes the spielfeld with the given dimensions.
     *
     * @param spielfeldKonfiguration the SpielfeldKonfiguration of the game
     */

    public void setKonfiguration(SpielfeldKonfiguration spielfeldKonfiguration) {
        this.breite = spielfeldKonfiguration.getSpielfeldBreite();
        this.hoehe = spielfeldKonfiguration.getSpielfeldHoehe();
        this.spielfeld = new Spielfeld(breite, hoehe);
        this.schiffskonfiguration = new HashMap<>(spielfeldKonfiguration.getSchiffsZusammensetzung());
        this.updatePattern(this.getLongestAvailableShipLength());
    }

    /**
     * Executes a shot based on the current AiSpieler level.
     */
    public SpielZug shoot() {
        SpielZug spielZug;

        switch (this.ESpielertyp) {
            case ESpielertyp.KI1:
                System.out.println("Using performSurroundingSearch");
                spielZug = this.shootSurroundingSearch();
                break;
            case ESpielertyp.KI2:
                System.out.println("Using shootDiagonalPattern");
                spielZug = this.shootDiagonalPattern();
                break;
            case ESpielertyp.KI3:
                System.out.println("Using schussKI3");
                spielZug = this.schussKI3();
                break;
            default:
                System.out.println("Using default: shootRandomly");
                spielZug = this.shootRandomly();
                break;
        }

        if (spielZug == null)
            return this.shootRandomly();

        this.spielfeld.schuss(spielZug);

        return spielZug;
    }

    public void requestShot() {
        SpielZug shot = this.shoot();
        this.notifyListener(shot);
    }

    public void setLastHit(SpielZug letzterHit) {
        this.lastHit = letzterHit;
        this.lastHitList.add(letzterHit); // Treffer zur Liste hinzufügen

    }

    /**
     * Shoots randomly within the spielfeld limits.
     * Ensures that all fields in the spielfeld are covered by generating coordinates between the minimum and maximum values.
     *
     * @return the coordinates of the shot
     */
    private SpielZug shootRandomly() {
        int x, y;
        int attempts = 0;
        int min = 0;  // Minimum value for coordinates
        int maxX = breite - 1;  // Maximum value for coordinates
        int maxY = hoehe - 1;  // Maximum value for coordinates

        // Generate random coordinates between the minimum and maximum values within the spielfeld limits
        do {
            x = random.nextInt((maxX - min) + 1) + min;  // Generate a random x coordinate between min and max
            y = random.nextInt((maxY - min) + 1) + min;  // Generate a random y coordinate between min and max
            attempts++;

        } while (spielfeld.istGetroffen(x, y) && attempts <= MaxRandomAttempts); // Check if the position is already hit and ensure the maximum attempts are not exceeded

        // If all attempts are used, ensure that the loop exits and returns the current x and y even if it's a hit position
        return new SpielZug(x, y);
    }

    private SpielZug shootSurroundingSearch() {
        // Bewegungsrichtungen: links (-1, 0), rechts (+1, 0), oben (0, -1), unten (0, +1)
        int[] dx = {-1, 1, 0, 0}; // Bewegung auf der X-Achse
        int[] dy = {0, 0, -1, 1}; // Bewegung auf der Y-Achse

        // Schleife durch die Trefferliste (von der letzten getroffenen Position ausgehend)
        for (SpielZug letzterTreffer : lastHitList) {
            int x = letzterTreffer.getX();
            int y = letzterTreffer.getY();

            // Überprüfen der vier Richtungen für jedes getroffene Feld
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i]; // neue X-Position
                int ny = y + dy[i]; // neue Y-Position

                // Überprüfen, ob die neue Position gültig ist und noch nicht beschossen wurde
                if (gueltigePosition(nx, ny) && !spielfeld.istGetroffen(nx, ny)) {
                    return new SpielZug(nx, ny); // Rückgabe eines gültigen Spielzugs
                }
            }
        }

        // Wenn kein gültiger Zug gefunden wird, gib null zurück
        return null;
    }

    /**
     * Updates the configuration when a ship is destroyed.
     *
     * @param shipType the type of the ship that is potentially destroyed
     */

    public void removeDestroyedShip(ESchiffsTyp shipType) {
        this.schiffskonfiguration.put(shipType, this.schiffskonfiguration.getOrDefault(shipType, 0) - 1);
        this.updatePattern(this.getLongestAvailableShipLength());
    }


    /** Filtert die schiffskonfiguration map
     * @return Laenge des laengsten schiffs in der schiffskonfiguration map (wenn Anzahl > 0)
     * @autor Marcel K.
     */
    public int getLongestAvailableShipLength() {
        // Iteriere durch die Einträge der schiffskonfiguration-Map
        int longestLength = 2; // Standardwert, falls keine Schiffe mehr übrig sind
        for (Map.Entry<ESchiffsTyp, Integer> entry : schiffskonfiguration.entrySet())
            // Überprüfen, ob Schiffe dieses Typs noch verfügbar sind, und update longestLength nur wenn groeser
            if (entry.getValue() > 0 && entry.getKey().getLaenge() > longestLength)
                longestLength = entry.getKey().getLaenge();
        return longestLength;
    }

    public void updatePattern(int abstand) {
        this.diagonalPatternPoints.clear();
        for (int x = 0; x < breite; x++)
            for (int y = 0; y < hoehe; y++)
                // Check if the coordinates (x, y) meet the spacing condition
                // and if the field has not been hit yet
                if ((x + y) % abstand == 0 && !spielfeld.istGetroffen(x, y))
                    diagonalPatternPoints.add(new SpielZug(x, y));
    }


    public SpielZug shootDiagonalPattern() {
        // Prüfen, ob die Trefferliste nicht leer ist und der letzte Treffer kein versenktes Schiff war
        if (!this.diagonalPatternPoints.isEmpty()) {
            SpielZug spielZug = this.shootSurroundingSearch();
            if (spielZug != null)
                return spielZug;
        }

        // Wenn die Liste der Diagonalpunkte leer ist, aktualisiere das Muster
        if (this.diagonalPatternPoints.isEmpty()) {
            this.updatePattern(this.getLongestAvailableShipLength() - 1); // Reduziere die Länge, falls keine großen Schiffe mehr vorhanden sind
        }

        // Füge eine Überprüfung hinzu, ob die Liste immer noch leer ist
        if (!this.diagonalPatternPoints.isEmpty()) {
            return this.diagonalPatternPoints.pop();
        } else {
            // Wenn die Liste leer ist, schieße zufällig
            return this.shootRandomly();
        }
    }

    private SpielZug schussKI3() {
//         Überprüfen, ob es einen letzten Treffer gibt und ob eine Umkreissuche sinnvoll ist
        if (!lastHitList.isEmpty() && lastHitList.getLast().getSchussTyp() == Schusstyp.TREFFER) {
            SpielZug umkreisSchuss = shootSurroundingSearch();
            if (umkreisSchuss != null) {
                return umkreisSchuss; // Führe die Umkreissuche durch und gib den Schuss zurück, wenn ein passendes Feld gefunden wird
            }
        }

        // KI2-Strategie: Diagonales Schussmuster verwenden
        if (!this.diagonalPatternPoints.isEmpty()) {
            // Wenn noch Punkte im diagonalen Muster vorhanden sind, nimm den nächsten Punkt
            return this.diagonalPatternPoints.pop();
        } else {
            // Aktualisiere das diagonale Muster, wenn es leer ist, basierend auf der Länge des größten verbleibenden Schiffs
            this.updatePattern(this.getLongestAvailableShipLength() - 1);
            // Schieße erneut im diagonalen Muster, wenn aktualisiert
            if (!this.diagonalPatternPoints.isEmpty()) {
                return this.diagonalPatternPoints.pop();
            }
        }
        if (!lastHitList.isEmpty() && lastHitList.getLast().getSchussTyp() == Schusstyp.TREFFER) {
            SpielZug umkreisSchuss = shootSurroundingSearch();
            if (umkreisSchuss != null) {
                return umkreisSchuss; // Führe die Umkreissuche durch und gib den Schuss zurück, wenn ein passendes Feld gefunden wird
            }
        }

        // KI2-Strategie: Diagonales Schussmuster verwenden
        if (!this.diagonalPatternPoints.isEmpty()) {
            // Wenn noch Punkte im diagonalen Muster vorhanden sind, nimm den nächsten Punkt
            return this.diagonalPatternPoints.pop();
        } else {
            // Aktualisiere das diagonale Muster, wenn es leer ist, basierend auf der Länge des größten verbleibenden Schiffs
            this.updatePattern(this.getLongestAvailableShipLength() - 1);
            // Schieße erneut im diagonalen Muster, wenn aktualisiert
            if (!this.diagonalPatternPoints.isEmpty()) {
                return this.diagonalPatternPoints.pop();
            }
        }

        // Wenn keine Umkreissuche und kein diagonales Muster ausgeführt wurde, nutze die Heatmap-Strategie
        int[][] heatmap = erstelleHeatmap();

        int maxWahrscheinlichkeit = 0;
        List<SpielZug> kandidaten = new ArrayList<>();

        for (int row = 0; row < hoehe; row++) {
            for (int col = 0; col < breite; col++) {
                if (!spielfeld.istGetroffen(row, col) && heatmap[row][col] > maxWahrscheinlichkeit) {
                    maxWahrscheinlichkeit = heatmap[row][col];
                    kandidaten.clear();
                    kandidaten.add(new SpielZug(row, col));
                } else if (heatmap[row][col] == maxWahrscheinlichkeit) {
                    kandidaten.add(new SpielZug(row, col));
                }
            }
        }


        if (!kandidaten.isEmpty()) {
            return kandidaten.get(random.nextInt(kandidaten.size()));
        }

        // Falls keine spezifischen Strategien greifen, zufälligen unbeschossenen Schuss wählen

        return shootRandomly();
    }



    /** Erstellt eine Heatmap basierend auf der Treffer historie und den verbleibenden Schiffsgrößen.
     * @return eine 2D-Heatmap des Spielfelds
     */
    private int[][] erstelleHeatmap() {
        int[][] heatmap = new int[hoehe][breite];
        int groessteSchiffsGroesse = getGroessteSchiffsGroesse();

        for (int row = 0; row < hoehe; row++) {
            for (int col = 0; col < breite; col++) {
                if (!spielfeld.istGetroffen(row, col)) {
                    heatmap[row][col] = berechneWahrscheinlichkeit(row, col, groessteSchiffsGroesse);
                }
            }
        }

        return heatmap;
    }

    /**
     * Berechnet die Wahrscheinlichkeit für ein Feld, basierend auf möglichen horizontalen und vertikalen Positionierungen.
     * @param row Die Zeile des Feldes.
     * @param col Die Spalte des Feldes.
     * @param groessteSchiffsGroesse Die Größe des größten verbleibenden Schiffs.
     * @return Die Wahrscheinlichkeitsbewertung für dieses Feld.
     */
    private int berechneWahrscheinlichkeit(int row, int col, int groessteSchiffsGroesse) {
        int wahrscheinlichkeit = 0;
        // Horizontale Wahrscheinlichkeiten berechnen
        wahrscheinlichkeit += berechneHorizontal(row, col, groessteSchiffsGroesse);
        // Vertikale Wahrscheinlichkeiten berechnen
        wahrscheinlichkeit += berechneVertikal(row, col, groessteSchiffsGroesse);

        return wahrscheinlichkeit;
    }

    /**
     * Berechnet die Wahrscheinlichkeiten für die horizontale Platzierung von Schiffen.
     * @param row Die Zeile des Feldes.
     * @param col Die Spalte des Feldes.
     * @param laenge Die Länge des größten verbleibenden Schiffs.
     * @return Die Wahrscheinlichkeitsbewertung für die horizontale Platzierung.
     */
    private int berechneHorizontal(int row, int col, int laenge) {
        int wahrscheinlichkeit = 0;

        if (gueltigePosition(row, col + laenge - 1)) {
            boolean moeglich = true;
            for (int i = 0; i < laenge; i++) {
                if (spielfeld.istGetroffen(row, col + i)) {
                    moeglich = false;
                    break;
                }
            }
            if (moeglich) {
                wahrscheinlichkeit++;
            }
        }

        return wahrscheinlichkeit;
    }

    /**
     * Berechnet die Wahrscheinlichkeiten für die vertikale Platzierung von Schiffen.
     * @param row Die Zeile des Feldes.
     * @param col Die Spalte des Feldes.
     * @param laenge Die Länge des größten verbleibenden Schiffs.
     * @return Die Wahrscheinlichkeitsbewertung für die vertikale Platzierung.
     */
    private int berechneVertikal(int row, int col, int laenge) {
        int wahrscheinlichkeit = 0;

        if (gueltigePosition(row + laenge - 1, col)) {
            boolean moeglich = true;
            for (int i = 0; i < laenge; i++) {
                if (spielfeld.istGetroffen(row + i, col)) {
                    moeglich = false;
                    break;
                }
            }
            if (moeglich) {
                wahrscheinlichkeit++;
            }
        }

        return wahrscheinlichkeit;
    }

    /**
     * Returns the size of the largest remaining ship.
     *
     * @return size of the largest remaining ship
     */
    public int getGroessteSchiffsGroesse() {
        int longestLength = 1; // Standardwert, falls keine Schiffe mehr übrig sind

        for (Map.Entry<ESchiffsTyp, Integer> entry : schiffskonfiguration.entrySet()) {
            if (entry.getValue() > 0) { // Nur Schiffe berücksichtigen, die noch vorhanden sind
                int length = entry.getKey().getLaenge();
                if (length > longestLength) {
                    longestLength = length;
                }
            }
        }
        return longestLength;
    }

    private boolean gueltigePosition(int x, int y) {
        return x >= 0 && x < breite && y >= 0 && y < hoehe;
    }

    private void notifyListener(SpielZug spielZug) {
        String spielZugJson = new Gson().toJson(spielZug);
        Event event = new Event(AiSpieler.class.getSimpleName(), EAction.SPIELZUG_GEMACHT.ordinal(), spielZugJson);
        listener.actionPerformed(event);
    }
}
