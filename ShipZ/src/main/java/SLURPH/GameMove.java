package SLURPH;

public class GameMove {
    // Instance variables
    public int player1Points;
    public int player2Points;
    public int player1Ships;
    public int player2Ships;
    public char[][] player1Board;
    public char[][] player2Board;

    // Constructor
    public GameMove(int player1Points, int player2Points, int player1Ships, int player2Ships, char[][] player1Board, char[][] player2Board) {
        this.player1Points = player1Points;
        this.player2Points = player2Points;
        this.player1Ships = player1Ships;
        this.player2Ships = player2Ships;
        this.player1Board = player1Board;
        this.player2Board = player2Board;
    }


}//End Class
