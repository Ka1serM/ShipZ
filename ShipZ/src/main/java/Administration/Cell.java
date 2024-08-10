package Administration;

import GUI.Schusstyp;

public class Cell {
    private boolean isHit;
    private Ship ship;

    public Cell() {
        this.isHit = false;
        this.ship = null;
    }

    public boolean isHit() {
        return isHit;
    }

    public Schusstyp hit() {
        this.isHit = true;
        if(this.ship != null) {
            this.ship.hit();
            if(this.ship.isSunk())
                return Schusstyp.TREFFER_VERSENKT;
            return Schusstyp.TREFFER;
        }
        return Schusstyp.KEIN_TREFFER;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public boolean hasShip() {
        return ship != null;
    }
}
