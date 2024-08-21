package SLURPH;

import Administration.Spieler;
import Administration.Spielfeld;
import Administration.Nachrichten.SpielfeldKonfiguration;

public class GameMove {
    // Instance variables
    public Spieler player1Points;
    public Spieler player2Points;
    public Spielfeld player1Board; //typ ändern
    public Spielfeld player2Board;

    // Neu hinzugefügte Konfigurationen
    public SpielfeldKonfiguration player1Konfig;
    public SpielfeldKonfiguration player2Konfig;

    // Constructor
    public GameMove(Spieler player1Points, Spieler player2Points, Spielfeld player1Board, Spielfeld player2Board,SpielfeldKonfiguration player1Konfig, SpielfeldKonfiguration player2Konfig) {
        this.player1Points = player1Points;
        this.player2Points = player2Points;
        this.player1Board = player1Board;
        this.player2Board = player2Board;
        this.player1Konfig = player1Konfig;
        this.player2Konfig = player2Konfig;
    }


}//End Class
