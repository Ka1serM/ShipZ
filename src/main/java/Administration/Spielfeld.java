package Administration;

import Administration.Enums.ESchiffsTyp;
import GUI.ESchiffsZustand;
import GUI.Schusstyp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Spielfeld
{
    private final HashMap<Integer, Schiff> schiffe;
    private final Zelle[][] grid;
    
    public Spielfeld(int breiteX, int hoeheY)
    {
        this.grid = initializeGrid(breiteX, hoeheY);
        this.schiffe = new HashMap<>();
    }

    public Schusstyp schuss(SpielZug spielZug)
    {
        return this.grid[spielZug.getX()][spielZug.getY()].schuss();
    }
    
    public boolean istGetroffen(int x, int y)
    {
        return this.grid[x][y].schusstyp != Schusstyp.KEIN_SCHUSS;
    }
    
    public Schiff getErstesSchiff(int x, int y)
    {
        return this.grid[x][y].schiffe.getFirst();
    }
    
    public HashMap<Integer, Schiff> getSchiffe()
    {
        return this.schiffe;
    }

    public Schiff getSchiff(int idX)
    {
        return this.schiffe.get(idX);
    }
    
    public void loescheSchiffe() {
        schiffe.clear();
        for (Zelle[] zelles : this.grid)
            for (Zelle zelle : zelles)
                zelle.schiffe.clear();
    }
    
    public void verschiebeSchiff(Schiff schiff, int x, int y, int rotation) {
        for (int[] pos : schiff.getPositionen())
            this.grid[pos[0]][pos[1]].schiffe.remove(schiff);
        
        schiff.updatePositions(x, y, rotation);
        
        for (int[] pos : schiff.getPositionen())
            this.grid[pos[0]][pos[1]].schiffe.add(schiff);
    }
    
    public boolean kannSchiffSetzen(int x, int y, int rotation, ESchiffsTyp schiffsTyp, boolean istSchiffsberuehrungErlaubt) {
        int breite = (rotation == 90 || rotation == 270) ? schiffsTyp.getBreite() : schiffsTyp.getLaenge();
        int laenge = (rotation == 90 || rotation == 270) ? schiffsTyp.getLaenge() : schiffsTyp.getBreite();

        int endX = x + breite - 1;
        int endY = y + laenge - 1;

        //schnelle prüfung für die Grenzen
        if (endX < 0 || endX >= this.grid.length || endY < 0 || endY >= this.grid[0].length)
            return false;

        for (int i = x; i <= endX; i++) {
            for (int j = y; j <= endY; j++) {

                //umliegende Felder (+-1)
                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <= 1; l++) {
                        int posX = i + k;
                        int posY = j + l;

                        //schiffsberührung erlaubt und ist eine Ecke
                        if (istSchiffsberuehrungErlaubt && (Math.abs(k) == 1 && Math.abs(l) == 1))
                            continue;

                        //grenzen und nachbar schiff pruefen
                        if (posX >= 0 && posX < this.grid.length && posY >= 0 && posY < this.grid[0].length) {
                            
                            List<Schiff> schiffeInZelle = this.grid[posX][posY].schiffe;
                            for (Schiff schiff : schiffeInZelle)
                                if (schiff != null)
                                    return false;
                        }
                        
                    }
                }
            }
        }
        return true;
    }

    public boolean istSchiffGueltig(Schiff originalesSchiff, boolean istSchiffsberuehrungErlaubt) {
        int breite = (originalesSchiff.getRotation() == 90 || originalesSchiff.getRotation() == 270) ? originalesSchiff.getSchiffsTyp().getBreite() : originalesSchiff.getSchiffsTyp().getLaenge();
        int laenge = (originalesSchiff.getRotation() == 90 || originalesSchiff.getRotation() == 270) ? originalesSchiff.getSchiffsTyp().getLaenge() : originalesSchiff.getSchiffsTyp().getBreite();

        int endX = originalesSchiff.getX() + breite - 1;
        int endY = originalesSchiff.getY() + laenge - 1;

        //schnelle prüfung für die Grenzen
        if (endX < 0 || endX >= this.grid.length || endY < 0 || endY >= this.grid[0].length)
            return false;

        for (int i = originalesSchiff.getX(); i <= endX; i++) {
            for (int j = originalesSchiff.getY(); j <= endY; j++) {

                //umliegende Felder (+-1)
                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <= 1; l++) {
                        int posX = i + k;
                        int posY = j + l;

                        //schiffsberührung erlaubt und ist eine Ecke
                        if (istSchiffsberuehrungErlaubt && (Math.abs(k) == 1 && Math.abs(l) == 1))
                            continue;

                        //grenzen und nachbar schiff pruefen
                        if (posX >= 0 && posX < this.grid.length && posY >= 0 && posY < this.grid[0].length) {

                            List<Schiff> schiffeInZelle = this.grid[posX][posY].schiffe;
                            for (Schiff schiff : schiffeInZelle)
                                if (schiff != originalesSchiff)
                                    return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void setzeSchiff(int idx, int x, int y, ESchiffsTyp schiffsTyp, int rotation, ESchiffsZustand schiffsZustand)
    {
        this.setzeSchiff(new Schiff(idx, x, y, schiffsTyp, rotation, schiffsZustand));
    }

    public void setzeSchiff(Schiff schiff)
    {
        schiffe.put(schiff.getId(), schiff);
        for (var pos : schiff.getPositionen())
            this.grid[pos[0]][pos[1]].schiffe.add(schiff);
    }


    private Zelle[][] initializeGrid(int breiteX, int hoeheY)
    {
        Zelle[][] returnArray = new Zelle[breiteX][hoeheY];
        for (int i = 0; i < returnArray.length; i++)
            for (int j = 0; j < returnArray[i].length; j++)
                returnArray[i][j] = new Zelle();
        return returnArray;
    }

    private class Zelle {
        private Schusstyp schusstyp;
        private final List<Schiff> schiffe;

        private Zelle() {
            this.schusstyp = Schusstyp.KEIN_SCHUSS;
            this.schiffe = new LinkedList<>();
        }
        
        private Schusstyp schuss() {
            this.schusstyp = Schusstyp.KEIN_TREFFER;
            for (Schiff schiff : schiffe)
            {
                schiff.schuss();
                if(schiff.getSchiffsZustand() == ESchiffsZustand.VERSENKT)
                    this.schusstyp = Schusstyp.TREFFER_VERSENKT;
                else
                    this.schusstyp = Schusstyp.TREFFER;
            }
            return this.schusstyp;
        }
    }
}
