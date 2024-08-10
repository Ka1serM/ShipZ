package SLURPH;

import Administration.Enums;

public class Settings
{

    private final int size;
    private final int maxAttempts;
    private final int[] shipCounts;

    public Settings()
    {
        size = 10;
        shipCounts = new int[]{2, 2, 2, 2, 2};
        maxAttempts = 20;
    }

    public int getSize()
    {
        return size;
    }

    public int getMaxAttempts()
    {
        return maxAttempts;
    }

    public int[] getShipCounts()
    {
        return shipCounts;
    }
}
