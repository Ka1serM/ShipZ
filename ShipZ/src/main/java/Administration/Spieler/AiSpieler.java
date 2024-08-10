package Administration.Spieler;

import Administration.Board;
import Administration.Enums;
import Administration.Event;
import Administration.EventListener;

import java.util.*;
import Administration.Enums.ShipTyp;
import GUI.Spielertyp;

/**
 * AiSpieler player implementation for the game.
 */
public class AiSpieler extends Spieler {
    private static final int MaxRandomAttempts = 50;
    private final EventListener listener;
    public Board board;
    private final Random random;
    private int hoehe;
    private int breite;
    private Spielertyp spielerTyp;
    private int[] lastHit;
    private final LinkedList<int[]> diagonalPatternPoints;
    private HashMap<Enums.ShipTyp, Integer> schiffskonfiguration; //liste aller Schiffe
    private int abstand;
    
    /**
     * Initializes the AiSpieler with the given settings and event listener.
     * @param listener the event listener
     */
    public AiSpieler(EventListener listener, Spielertyp spielerTyp, String name) {
        super(name);
        this.listener = listener;
        this.spielerTyp = spielerTyp;
        this.random = new Random();
        this.lastHit = null;
        this.diagonalPatternPoints = new LinkedList<>();
        this.schiffskonfiguration = null;
        this.abstand = 5;
    }

    /**
     * Sets the configuration of the board by defining its height and width.
     * Initializes the board with the given dimensions.
     *
     * @param hoehe the height of the board
     * @param breite the width of the board
     */

    public void setKonfiguration(int breite, int hoehe, HashMap<Enums.ShipTyp, Integer> schiffskonfiguration) {
        this.breite = breite;
        this.hoehe = hoehe;
        this.board = new Board(breite, hoehe);
        this.schiffskonfiguration = new HashMap<>(schiffskonfiguration);
    }
    
    /**
     * Executes a shot based on the current AiSpieler level.
     */
    public void shoot() {
        int[] shot;

        switch (this.spielerTyp) {
            case Spielertyp.KI1:
                System.out.println("Using performSurroundingSearch");
                shot = shootSurroundingSearch();
                break;
            case Spielertyp.KI2:
                System.out.println("Using shootDiagonalPattern");
                shot = shootDiagonalPattern();
                break;
            case Spielertyp.KI3:
                System.out.println("Using schussKI3");
                shot = schussKI3();
                break;
            default:
                System.out.println("Using default: shootRandomly");
                shot = shootRandomly();
                break;
        }

        this.board.hit(shot[0], shot[1]);

        this.notifyListener(shot);
    }
    
    public void setLastHit(int[] lastHit) {
        this.lastHit = lastHit;
    }



    /**
     * Shoots randomly within the board limits.
     * Ensures that all fields in the board are covered by generating coordinates between the minimum and maximum values.
     *
     * @return the coordinates of the shot
     */
    private int[] shootRandomly() {
        int x, y;
        int attempts = 0;
        int min = 0;  // Minimum value for coordinates
        int maxX = breite - 1;  // Maximum value for coordinates
        int maxY = hoehe - 1;  // Maximum value for coordinates

        // Generate random coordinates between the minimum and maximum values within the board limits
        do {
            x = random.nextInt((maxX - min) + 1) + min;  // Generate a random x coordinate between min and max
            y = random.nextInt((maxY - min) + 1) + min;  // Generate a random y coordinate between min and max
            attempts++;
        } while (board.isHit(x, y) && attempts <= MaxRandomAttempts); // Check if the position is already hit and ensure the maximum attempts are not exceeded

        // If all attempts are used, ensure that the loop exits and returns the current x and y even if it's a hit position
        return new int[]{x, y};
    }

    /**
     * Performs a search around the last hit position.
     *
     * @return the coordinates of the next shot
     */
    public int[] shootSurroundingSearch() {
        if (lastHit == null) {
            return shootRandomly();
        }

        final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direction : DIRECTIONS) {
            int nextRow = lastHit[0] + direction[0];
            int nextCol = lastHit[1] + direction[1];

            if (this.gueltigePosition(nextRow, nextCol) && !board.isHit(nextRow, nextCol)) {
                return new int[]{nextRow, nextCol};
            }
        }
        
        
        //If no valid position found, return random position
        return shootRandomly();
    }

    /**
     * Updates the configuration when a ship is destroyed.
     *
     * @param shipType the type of the ship that is potentially destroyed
     */
    public void removeDestroyedShip(ShipTyp shipType) {
        this.schiffskonfiguration.put(shipType, this.schiffskonfiguration.getOrDefault(shipType, 0) -1);
        // Update the fill pattern spacing based on the number of destroyed ships
        //this.abstand = this.getLongestAvailableShipLength();
        //this.fillPattern();
    }

    /** Filtert die schiffskonfiguration map
     * @return Laenge des laengsten schiffs in der schiffskonfiguration map (wenn Anzahl > 0)
     * @autor Marcel K.
     */
    public int getLongestAvailableShipLength() {
        return this.schiffskonfiguration.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> entry.getKey().getLaenge())
                .max(Integer::compare)
                .orElse(0);
    }

    public void fillPattern() {
        this.diagonalPatternPoints.clear();
        for (int x = 0; x < breite; x++) {
            for (int y = 0; y < hoehe; y++) {
                // Check if the coordinates (x, y) meet the spacing condition
                // and if the field has not been hit yet
                if ((x + y) % this.abstand == 0 && !board.isHit(x, y)) {
                    diagonalPatternPoints.add(new int[]{x, y});
                }
            }
        }
    }

    /**
     * Shoots in a diagonal pattern within the board limits.
     *
     * @return the coordinates of the shot
     */

    public int[] shootDiagonalPattern() {
        if(this.lastHit != null)
            return shootSurroundingSearch();
        
        //check if list is empty, attempt to refill
        if (this.diagonalPatternPoints.isEmpty()) {
            this.abstand = this.getLongestAvailableShipLength();
            if (this.abstand > 1) {
                this.fillPattern();
            }
        }
        int[] shot;
        shot = this.diagonalPatternPoints.pop();
        return shot;
    }

    // spielzug generieren und Antwort eintragen
    //
    // schiffstyp überprüfen und spielfeld anpassen

    /**
     * Advanced shooting strategy for AiSpieler level 3.
     *
     * @return coordinates of the shot
     */
    private int[] schussKI3() {
        // Heatmap erstellen basierend auf der Treffer historie und den verbleibenden Schiffsgrößen
        int[][] heatmap = erstelleHeatmap();

        // Felder mit der höchsten Wahrscheinlichkeit finden
        int maxWahrscheinlichkeit = 0;
        List<int[]> kandidaten = new ArrayList<>();

        for (int row = 0; row < hoehe; row++) {
            for (int col = 0; col < breite; col++) {
                if (!board.isHit(row, col) && heatmap[row][col] > maxWahrscheinlichkeit) {
                    maxWahrscheinlichkeit = heatmap[row][col];
                    kandidaten.clear();
                    kandidaten.add(new int[]{row, col});
                } else if (heatmap[row][col] == maxWahrscheinlichkeit) {
                    kandidaten.add(new int[]{row, col});
                }
            }
        }

        if (!kandidaten.isEmpty()) {
            int[] schuss = kandidaten.get(random.nextInt(kandidaten.size()));
            return new int[]{schuss[0], schuss[1]};
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
                if (!board.isHit(row, col)) {
                    heatmap[row][col] = berechneWahrscheinlichkeit(row, col, groessteSchiffsGroesse);
                }
            }
        }

        return heatmap;
    }

    /** Berechnet die Wahrscheinlichkeit eines Treffers für ein bestimmtes Feld.
     * Basierend auf den verbleibenden Schiffsgrößen.
     * @param row die Zeilenkoordinate
     * @param col die Spaltenkoordinate
     * @param groessteSchiffsGroesse die Größe des größten verbleibenden Schiffs
     * @return Wahrscheinlichkeit eines Treffers
     */
    private int berechneWahrscheinlichkeit(int row, int col, int groessteSchiffsGroesse) {
        int wahrscheinlichkeit = 0;

        for (int laenge = 1; laenge <= groessteSchiffsGroesse; laenge++) {
            // Horizontal
            if (gueltigePosition(row, col + laenge - 1)) {
                boolean moeglich = true;
                for (int i = 0; i < laenge; i++) {
                    if (board.isHit(row, col + i)) {
                        moeglich = false;
                        break;
                    }
                }
                if (moeglich) {
                    wahrscheinlichkeit++;
                }
            }
            // Vertikal
            if (gueltigePosition(row + laenge - 1, col)) {
                boolean moeglich = true;
                for (int i = 0; i < laenge; i++) {
                    if (board.isHit(row + i, col)) {
                        moeglich = false;
                        break;
                    }
                }
                if (moeglich) {
                    wahrscheinlichkeit++;
                }
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
    
        for (Map.Entry<Enums.ShipTyp, Integer> entry : schiffskonfiguration.entrySet()) {
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

    private void notifyListener(int[] shot) {
        Event event = new Event(this, Enums.GameAction.SCHUSS_RETURN.ordinal(), shot[0] + "/" + shot[1]);
        listener.actionPerformed(event);
    }
}
