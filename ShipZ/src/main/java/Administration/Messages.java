package Administration;

import GUI.Schusstyp;
import java.util.HashMap;

class SpielKonfiguration {
    public int spielfeldBreite;
    public int spielfeldHoehe;
    public boolean schiffsberuehrungErlaubt;
    public HashMap<Enums.ShipTyp, Integer> schiffsZusammensetzung;

    public SpielKonfiguration(int spielfeldBreite, int spielfeldHoehe, boolean schiffsberuehrungErlaubt, HashMap<Enums.ShipTyp, Integer> schiffsZusammensetzung) {
        super();
        this.spielfeldBreite = spielfeldBreite;
        this.spielfeldHoehe = spielfeldHoehe;
        this.schiffsberuehrungErlaubt = schiffsberuehrungErlaubt;
        this.schiffsZusammensetzung = schiffsZusammensetzung;
    }
}

class Treffer {
    public int x, y;
    public Schusstyp schusstyp;

    public Treffer(int x, int y, Schusstyp schusstyp) {
        super();
        this.x = x;
        this.y = y;
        this.schusstyp = schusstyp;
    }
}
