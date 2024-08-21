package Administration.Enums;

public enum ESchiffsTyp {
    PATROL_BOAT(1, 1),
    SUBMARINE(2, 1),
    DESTROYER(3, 1),
    BATTLESHIP(4, 1),
    CARRIER(5, 1);

    private final int laenge; //width
    private final int breite;  //height

ESchiffsTyp(int laenge, int breite) {
        this.laenge = laenge;
        this.breite = breite;
    }

    public int getLaenge() {
        return this.laenge;
    }

    public int getBreite() {
        return this.breite;
    }
}
