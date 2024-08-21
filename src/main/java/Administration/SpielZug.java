package Administration;

import GUI.Schusstyp;

public class SpielZug {
    private int x, y;
    private Schusstyp schusstyp = Schusstyp.KEIN_SCHUSS;

    public SpielZug(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public SpielZug(int x, int y, Schusstyp schusstyp) {
        this.x = x;
        this.y = y;
        this.schusstyp = schusstyp;
    }
    
    public void setSchussTyp(Schusstyp schusstyp) {
        this.schusstyp = schusstyp;
    }

    public Schusstyp getSchussTyp() {
        return this.schusstyp;
    }
    
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
