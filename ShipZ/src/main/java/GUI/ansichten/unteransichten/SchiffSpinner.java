package GUI.ansichten.unteransichten;

import Administration.Enums;

/**
 * Dieser Spinner erweitert die Funktionalitaet des Intspinners mit folgender Funktonalitaet:
 * - Es kann der angezeigte Wert mal der Anzahl an Feldern eines Schifftyps zueruckgegeben werden
 * - Es kann der angezeigte Wert mal einer Zehnerpotenz zurueckgegeben werden.
 *
 * Beide Funktionen werden fuer die Spielkonfiguration benötigt.
 */
public class SchiffSpinner extends IntSpinner {

    // IV
    /** Anzahl an Feldern, die das gespinnte Schiff einnimmt */
    private final int anzahlFelder;

    /** Dezimalstelle im RueckgagbeLong von AnsichtSchiffskonfiguration */
    private final Enums.ShipTyp typ;


    /**
     * Erstellt einen IntSpinner, welcher ueber zusaetzliche Funktionalitaet
     * fuer die Schiffszusammenstellung verfuegt.
     * @param min           Minmalwert
     * @param max           Maximalwert
     * @param initial       Initialwert
     * @param typ Schiffstyp
     */
    public SchiffSpinner(int min, int max, int initial, Enums.ShipTyp typ) {

        // Superkontruktor IntSpinner
        super(min, max, initial);

        this.anzahlFelder = typ.getBreite() * typ.getLaenge();
        this.typ = typ;
    }

    /**
     * Gibt die Anzahl an Schiffsfelder zurueck
     * @return Anzahl Schiffsfelder
     */
    public int getAnzahlFelder() {
        return this.anzahlFelder;
    }

    /**
     * Gibt die Dezimalstellestelle fuer Schiffszusammenstellung zurueck
     * @return Dezimalstelle fuer Schiffszusammenstellung
     */
    public Enums.ShipTyp getTyp() {
        return this.typ;
    }


    /**
     * Gibt den aktuell angezeigten Wert mal this.anzahlFelder zurueck.
     * @return Angezeigten Wert mal this.AazahlFelder
     */
    public int getAnzahlFelderInsgesamt() {
        return (this.wert.getValue() * this.anzahlFelder);
    }
}
