package Administration.Nachrichten;

import Administration.Enums.ESchiffsTyp;
import java.util.HashMap;

public class SpielfeldKonfiguration {
    private final int spielfeldBreiteX;
    private final int spielfeldHoeheY;
    private final boolean isSchiffsberuehrungErlaubt;
    private final HashMap<ESchiffsTyp, Integer> schiffsZusammensetzung;

    public SpielfeldKonfiguration(int spielfeldBreite, int spielfeldHoehe, boolean isSchiffsberuehrungErlaubt, HashMap<ESchiffsTyp, Integer> schiffsZusammensetzung) {
        this.spielfeldBreiteX = spielfeldBreite;
        this.spielfeldHoeheY = spielfeldHoehe;
        this.isSchiffsberuehrungErlaubt = isSchiffsberuehrungErlaubt;
        this.schiffsZusammensetzung = schiffsZusammensetzung;
    }

    public HashMap<ESchiffsTyp, Integer> getSchiffsZusammensetzung() {
        return schiffsZusammensetzung;
    }

    public boolean isSchiffsberuehrungErlaubt() {
        return isSchiffsberuehrungErlaubt;
    }

    public int getSpielfeldHoehe() {
        return spielfeldHoeheY;
    }

    public int getSpielfeldBreite() {
        return spielfeldBreiteX;
    }
}

