package Administration;

import GUI.Schusstyp;

import java.util.ArrayList;
import java.util.List;

public class Board
{
    private List<Ship> ships;
    private final int x;
    private final int y;
    private final Cell[][] grid;

    public Board(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.grid = initializeGrid();
        this.ships = new ArrayList<>();
    }

    public Schusstyp hit(int x, int y)
    {
        return this.grid[x][y].hit();
    }

    public boolean isHit(int x, int y)
    {
        return this.grid[x][y].isHit();
    }

    public Ship getShip(int x, int y)
    {
        return this.grid[x][y].getShip();
    }


    public List<Ship> getShips()
    {
        return this.ships;
    }
    
    public void setShip(Ship ship)
    {
        ships.add(ship);
        for (var pos : ship.getPositions())
            this.grid[pos[0]][pos[1]].setShip(ship);
    }
    
    private Cell[][] initializeGrid()
    {
        Cell[][] returnArray = new Cell[this.x][this.y];
        for (int i = 0; i < returnArray.length; i++)
            for (int j = 0; j < returnArray[i].length; j++)
                returnArray[i][j] = new Cell();
        return returnArray;
    }
    
    public void clear() {
        ships.clear();
        for (Cell[] cells : this.grid)
            for (Cell cell : cells)
                cell.setShip(null);
    }
}
