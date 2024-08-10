package Administration;

public class Ship {
    private final int id;
    
    private final int x, y;
    private final int rotation;
    private final Enums.ShipTyp type;
    private int hits;
    private final int[][] positions;

    public Ship(int id, int x, int y, Enums.ShipTyp type, int rotation) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
        this.rotation = rotation;
        this.positions = this.initializePositions(x, y, rotation);
    }

    public void hit() {
        hits++;
    }

    public boolean isSunk() {
        return this.hits >= (this.type.getBreite() * this.type.getLaenge());
    }

    public Enums.ShipTyp getType() {
        return this.type;
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

    public int[][] getPositions() {
        return this.positions;
    }
    
    public int[][] initializePositions(int x, int y, int rotation) {
        int[][] outArray = new int[this.type.getLaenge() * this.type.getBreite()][2];
        int breite = (rotation == 90 || rotation == 270) ? this.type.getBreite() : this.type.getLaenge();
        int laenge = (rotation == 90 || rotation == 270) ? this.type.getLaenge() : this.type.getBreite();
        int index = 0;
        for (int i = 0; i < breite; i++) {
            for (int j = 0; j < laenge; j++) {
                outArray[index][0] = x + i;
                outArray[index][1] = y + j;
                index++;
            }
        }
        return outArray;
    }

    public int getId() {
        return this.id;
    }
}
