package Administration;

import Administration.Enums.ESchiffsTyp;
import GUI.ESchiffsZustand;

public class Schiff {
    private final int id;
    private int x, y;
    private int rotation;
    private final ESchiffsTyp schiffsTyp;
    private ESchiffsZustand schiffsZustand;
    private int[][] positionen;

    private int treffer;
    public Schiff(int id, int x, int y, ESchiffsTyp schiffsTyp, int rotation, ESchiffsZustand schiffsZustand) {
        this.id = id;
        this.schiffsTyp = schiffsTyp;
        this.schiffsZustand = schiffsZustand;
        this.updatePositions(x, y, rotation);
    }

    public void updatePositions(int x, int y, int rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        int[][] outArray = new int[this.schiffsTyp.getLaenge() * this.schiffsTyp.getBreite()][2];
        int breite = (rotation == 90 || rotation == 270) ? this.schiffsTyp.getBreite() : this.schiffsTyp.getLaenge();
        int laenge = (rotation == 90 || rotation == 270) ? this.schiffsTyp.getLaenge() : this.schiffsTyp.getBreite();
        int index = 0;
        for (int i = 0; i < breite; i++) {
            for (int j = 0; j < laenge; j++) {
                outArray[index][0] = x + i;
                outArray[index][1] = y + j;
                index++;
            }
        }
        this.positionen = outArray;
    }

    public void schuss() {
        treffer++;
        this.schiffsZustand = ESchiffsZustand.GETROFFEN;
        if(this.treffer >= (this.schiffsTyp.getBreite() * this.schiffsTyp.getLaenge()))
            this.schiffsZustand = ESchiffsZustand.VERSENKT;
    }
    
    public ESchiffsZustand getSchiffsZustand() {
        return this.schiffsZustand;
    }
    
    public ESchiffsTyp getSchiffsTyp() {
        return this.schiffsTyp;
    }

    public int getRotation() {
        return this.rotation;
    }

    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }

    public int[][] getPositionen() {
        return this.positionen;
    }
    
    public int getId() {
        return this.id;
    }
}
