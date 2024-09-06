package org.example;

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
        board[row][column] = 1;
    }

    // produces next generation of cells according to Conway's rules
    public void nextGen()
    {
        // traverse the entire 2D array to identify all living cells
        for (int i=0; i<board.length; i++)
        {
            for (int j=0; j<board[i].length; j++)
            {
                int cell = board[i][j];
                // if cell is alive, check for 2-3 neighbors
                if (cell == 1)
                {
                    int numNeighbors = findLiveNeighbors(i, j);
                    // identify dead neighbors
                    // test each dead neighbor for 3 neighbors - if yes, add to list to make alive later
                    if (numNeighbors != 2 && numNeighbors != 3)
                    {
                        board[i][j] = 0; // cell dies
                    }
                }
            }
        }
        // 2. test each living cell for having 2-3 neighbors
        //       - if not, cell dies.
        // 3. identify all dead cells that are neighbors with this cell
        //   test each dead cell for 3 living neighbors
        //       - if so, cell comes alive.

        //
    }

    public int findLiveNeighbors(int row, int column)
    {
        int neighbors = 0;

        return neighbors;
    }

    // method to display current generation
    @Override
    public String toString()
    {
        String gridString = "";
        // traverse grid
        // add each row to string
        return gridString;
    }
}
