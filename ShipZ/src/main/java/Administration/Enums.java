package Administration;

public class Enums
{
    public enum GameAction
    {
        UNDO,
        REDO,
        SPIELRUNDE_SPEICHERN,
        SPIELRUNDE_ABBRECHEN,
        NETZWERKDATEN_CLIENT_ABRUFBBAR,
        NETZWERKSPIEL_ABBRECHEN,
        BESTENLISTE_LEEREN,
        SCHUSS_RETURN,
        SPIEL_LADEN,
        SPIEL_LOESCHEN,
        SCHIFFSKONFIGURATION_ABRUFBAR,
        SCHIFFE_FERTIG_PLATZIERT,
        NETZWERKDATEN_HOST_ABRUFBBAR,
        NETZWERKMODUS_ABRUFBAR,
        LOKALE_SPIELER_ABRUFBAR,
        PROGRAMM_BEENDEN,
        ANSICHT_GEWAEHLT,
        MENUE_ZURUECK,
        SCHIFF_ROTIERT,
        SCHIFF_BEWEGT,
        SCHIFFE_AUTOMATISCH_PLATZIEREN,
        
        
        NETZWERK_TIMEOUT,
        NETZWERK_VERBUNDEN,
        NETZWERK_SPIEL_STARTEN,

        NETZWERK_NACHRICHT_NAME,
        NETZWERK_NACHRICHT_KONFIGURATION,
        NETZWERK_NACHRICHT_SCHIFFE,
        
        LOAD_GAME
    }

    public enum ShipTyp {
        PATROL_BOAT(1, 1),
        SUBMARINE(2, 1),
        DESTROYER(3, 1),
        BATTLESHIP(4, 1),
        CARRIER(5, 1);

        private final int laenge; //width
        private final int breite;  //height

        ShipTyp(int laenge, int breite) {
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

}