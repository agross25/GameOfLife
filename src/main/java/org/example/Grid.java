package org.example;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Grid {

    int[][] board;

    // grid constructor
    public Grid(int height, int width)
    {
        board = new int[height][width];
    }

    // method to set a living cell
    public void setCellAlive(int row, int column)
    {
        try { board[row][column] = 1; }
        catch (Exception e) { System.out.println(e.getMessage()); }
    }

    public void setCellDead(int row, int column)
    {
        try { board[row][column] = 0; }
        catch (Exception e) { System.out.println(e.getMessage()); }
    }

    // produces next generation of cells according to Conway's rules
    public void nextGen()
    {
        ArrayList<int[]> cellsToDie = new ArrayList<>();
        ArrayList<int[]> cellsToComeAlive = new ArrayList<>();

        // traverse the entire 2D array to identify all living cells
        for (int i=0; i<board.length; i++)
        {
            for (int j=0; j<board[i].length; j++)
            {
                int cell = board[i][j];
                // if cell is alive, check status of neighbors
                if (cell == 1)
                {
                    // identify all live neighbors
                    int liveNeighbors = findLiveNeighbors(i, j);
                    // will die if it does not have 2 or 3 neighbors (1 or less, 4 or more)
                    if (liveNeighbors != 2 && liveNeighbors != 3)
                    {
                        int[] cellLocation = {i, j};
                        cellsToDie.add(cellLocation);
                    }
                    // identify all dead neighbors
                    ArrayList<int[]> deadNeighbors = findDeadNeighbors(i, j);
                    // loop through all dead cells, check if they have 3+ neighbors
                    for (int[] deadCell : deadNeighbors)
                    {
                        boolean alreadyUsed = false;
                        for (int[] c : cellsToComeAlive)
                        {
                            if (c[0]==deadCell[0] && c[1]==deadCell[1])
                                alreadyUsed = true;
                        }
                        if (!alreadyUsed)
                        {
                            // add to cellsToComeAlive if not already there
                            if (findLiveNeighbors(deadCell[0], deadCell[1]) == 3)
                                cellsToComeAlive.add(deadCell);
                            // can be optimized by tracking which cells were already checked
                        }
                    }
                }
            }
        }
        // kill all cells in cellsToDie
        for (int[] cellToKill : cellsToDie)
            setCellDead(cellToKill[0], cellToKill[1]);

        // revive all cells in cellsToComeAlive
        for (int[] cellToRevive : cellsToComeAlive)
            setCellAlive(cellToRevive[0], cellToRevive[1]);
    }

    public int findLiveNeighbors(int row, int column)
    {
        int liveNeighbors = 0;

        boolean hasTop = row > 0; // a higher row exists
        boolean hasBottom = row < board.length-1; // a lower row exists
        boolean hasLeft = column > 0; // a left column exists
        boolean hasRight = column < board[0].length-1; // a right column exists

        if (hasTop) {
            // check if cell directly above current cell is alive
            if (board[row-1][column] == 1)
                liveNeighbors++;
            if (hasLeft) {
                // check if diagonal upper left cell is alive
                if (board[row-1][column-1] == 1)
                    liveNeighbors++;
            }
            if (hasRight) {
                // check if diagonal upper right cell is alive
                if (board[row-1][column+1] == 1)
                    liveNeighbors++;
            }
        }
        if (hasBottom) {
            // check if cell directly below current cell is alive
            if (board[row+1][column] == 1)
                liveNeighbors++;
            if (hasLeft) {
                // check if diagonal lower left cell is alive
                if (board[row+1][column-1] == 1)
                    liveNeighbors++;
            }
            if (hasRight) {
                // check if diagonal lower right cell is alive
                if (board[row+1][column+1] == 1)
                    liveNeighbors++;
            }
        }
        if (hasLeft) {
            // check if adjacent left cell is alive
            if (board[row][column-1] == 1)
                liveNeighbors++;
        }
        if (hasRight) {
            // check if adjacent right cell is alive
            if (board[row][column+1] == 1)
                liveNeighbors++;
        }

        return liveNeighbors;
    }

    public ArrayList<int[]> findDeadNeighbors(int row, int column)
    {
        ArrayList<int[]> deadNeighbors = new ArrayList<>();

        boolean hasTop = row > 0; // a higher row exists
        boolean hasBottom = row < board.length-1; // a lower row exists
        boolean hasLeft = column > 0; // a left column exists
        boolean hasRight = column < board[0].length-1; // a right column exists

        if (hasTop) {
            // check if cell directly above current cell is dead
            if (board[row-1][column] == 0)
                deadNeighbors.add(new int[]{row-1, column});
            if (hasLeft) {
                // check if diagonal upper left cell is dead
                if (board[row-1][column-1] == 0)
                    deadNeighbors.add(new int[] {row-1, column-1});
            }
            if (hasRight) {
                // check if diagonal upper right cell is dead
                if (board[row-1][column+1] == 0)
                    deadNeighbors.add(new int[] {row-1, column+1});
            }
        }
        if (hasBottom) {
            // check if cell directly below current cell is dead
            if (board[row+1][column] == 0)
                deadNeighbors.add(new int[]{row+1, column});
            if (hasLeft) {
                // check if diagonal lower left cell is dead
                if (board[row+1][column-1] == 0)
                    deadNeighbors.add(new int[] {row+1, column-1});
            }
            if (hasRight) {
                // check if diagonal lower right cell is dead
                if (board[row+1][column+1] == 0)
                    deadNeighbors.add(new int[] {row+1, column+1});
            }
        }
        if (hasLeft) {
            // check if adjacent left cell is dead
            if (board[row][column-1] == 0)
                deadNeighbors.add(new int[] {row, column-1});
        }
        if (hasRight) {
            // check if adjacent right cell is dead
            if (board[row][column+1] == 0)
                deadNeighbors.add(new int[] {row, column+1});
        }

        return deadNeighbors;
    }

    // method to display current generation
    @Override
    public String toString()
    {
        String gridString = "";

        for (int i=0; i<board.length; i++)
        {
            for (int j=0; j<board[i].length; j++)
            {
                if (board[i][j] == 0)
                    gridString += "- ";
                else
                    gridString += "* ";
            }
            gridString += "\n";
        }

        return gridString;
    }
}
