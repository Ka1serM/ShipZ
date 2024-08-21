package Administration;

import Administration.Enums.ESchiffsTyp;
import Administration.Nachrichten.SpielfeldKonfiguration;
import GUI.ESpielertyp;

public class Spieler
{
    private final String name;
    private int score;
    protected ESpielertyp ESpielertyp;
    
    private boolean bereit;
    
    public Spieler(String name, ESpielertyp ESpielertyp) {
        this.name = name;
        this.ESpielertyp = ESpielertyp;
        this.score = 0;
        this.bereit = false;
    }
    
    public void setBereit(boolean bereit) {
        this.bereit = bereit;
    }
    
    public boolean istBereit() {
        return this.bereit;
    }
        
    
    public String getName() {
        return name;
    }
    
    public int getScore() {
        return score;
    }

    public ESpielertyp getSpielertyp() {
        return ESpielertyp;
    }
    
    public void setScore(int score) {
        this.score = score;
    }

    public SpielZug shoot() {
        return null;
    }

    public void requestShot() {
    }

    public void setLastHit(SpielZug letzterHit) {
    }
    public void removeDestroyedShip(ESchiffsTyp schiffsTyp){
        
    }
    
    public void setKonfiguration(SpielfeldKonfiguration spielfeldKonfiguration) {
        
    }
}
