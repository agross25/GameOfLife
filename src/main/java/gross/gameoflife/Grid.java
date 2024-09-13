package gross.gameoflife;

import java.util.ArrayList;

public class Grid {

    private int[][] board;

    // grid constructor
    public Grid(int height, int width) {
        board = new int[height][width];
    }

    // method to set a living cell
    public void setCellAlive(int row, int column) {
        if (row >= board.length || column >= board[row].length) {
            System.out.println("Action aborted. Provided dimensions are out of bounds.");
        } else if (row < 0 || column < 0) {
            System.out.println("Action aborted. Provided dimensions are negative.");
        } else {
            board[row][column] = 1;
        }
    }

    public void setCellDead(int row, int column) {
        if (row >= board.length || column >= board[row].length) {
            System.out.println("Action aborted. Provided dimensions are out of bounds.");
        } else if (row < 0 || column < 0) {
            System.out.println("Action aborted. Provided dimensions are negative.");
        } else {
            board[row][column] = 0;
        }
    }

    // produces next generation of cells according to Conway's rules
    public void nextGen() {
        ArrayList<int[]> cellsToDie = new ArrayList<>();
        ArrayList<int[]> cellsToComeAlive = new ArrayList<>();
        ArrayList<int[]> checkedDeadCells = new ArrayList<>();

        // traverse the entire 2D array to identify all living cells
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int cell = board[i][j];
                // if cell is alive, check status of neighbors
                if (cell == 1) {
                    // identify all live neighbors
                    int liveNeighbors = findLiveNeighbors(i, j);
                    // will die if it does not have 2 or 3 neighbors (1 or less, 4 or more)
                    if (liveNeighbors != 2 && liveNeighbors != 3) {
                        int[] cellLocation = {i, j};
                        cellsToDie.add(cellLocation);
                    }
                    // identify all dead neighbors
                    ArrayList<int[]> deadNeighbors = findDeadNeighbors(i, j);
                    // loop through all dead cells, check if they have 3+ neighbors
                    for (int[] deadCell : deadNeighbors) {
                        if (!checkedDeadCells.contains(deadCell)) {
                            // add to checkedDeadCells to avoid getting checked again
                            checkedDeadCells.add(deadCell);
                            // add to cellsToComeAlive if it has 3 live neighbors
                            if (findLiveNeighbors(deadCell[0], deadCell[1]) == 3) {
                                cellsToComeAlive.add(deadCell);
                            }
                        }
                    }
                }
            }
        }
        // kill all cells in cellsToDie
        for (int[] cellToKill : cellsToDie) {
            setCellDead(cellToKill[0], cellToKill[1]);
        }

        // revive all cells in cellsToComeAlive
        for (int[] cellToRevive : cellsToComeAlive) {
            setCellAlive(cellToRevive[0], cellToRevive[1]);
        }
    }

    public int findLiveNeighbors(int row, int column) {
        int liveNeighbors = 0;

        boolean hasTop = row > 0; // a higher row exists
        boolean hasBottom = row < board.length - 1; // a lower row exists
        boolean hasLeft = column > 0; // a left column exists
        boolean hasRight = column < board[0].length - 1; // a right column exists

        if (hasTop) {
            // check if cell directly above current cell is alive
            if (board[row - 1][column] == 1) {
                liveNeighbors++;
            }
            // check if diagonal upper left cell is alive
            if (hasLeft && board[row - 1][column - 1] == 1) {
                liveNeighbors++;
            }
            // check if diagonal upper right cell is alive
            if (hasRight && board[row - 1][column + 1] == 1) {
                liveNeighbors++;
            }
        }
        if (hasBottom) {
            // check if cell directly below current cell is alive
            if (board[row + 1][column] == 1) {
                liveNeighbors++;
            }
            // check if diagonal lower left cell is alive
            if (hasLeft && board[row + 1][column - 1] == 1) {
                liveNeighbors++;
            }
            // check if diagonal lower right cell is alive
            if (hasRight && board[row + 1][column + 1] == 1) {
                liveNeighbors++;
            }
        }
        // check if adjacent left cell is alive
        if (hasLeft && board[row][column - 1] == 1) {
            liveNeighbors++;
        }
        // check if adjacent right cell is alive
        if (hasRight && board[row][column + 1] == 1) {
            liveNeighbors++;
        }
        return liveNeighbors;
    }

    public ArrayList<int[]> findDeadNeighbors(int row, int column) {
        ArrayList<int[]> deadNeighbors = new ArrayList<>();

        boolean hasTop = row > 0; // a higher row exists
        boolean hasBottom = row < board.length - 1; // a lower row exists
        boolean hasLeft = column > 0; // a left column exists
        boolean hasRight = column < board[0].length - 1; // a right column exists

        if (hasTop) {
            // check if cell directly above current cell is dead
            if (board[row - 1][column] == 0) {
                deadNeighbors.add(new int[]{row - 1, column});
            }
            // check if diagonal upper left cell is dead
            if (hasLeft && board[row - 1][column - 1] == 0) {
                deadNeighbors.add(new int[]{row - 1, column - 1});
            }
            // check if diagonal upper right cell is dead
            if (hasRight && board[row - 1][column + 1] == 0) {
                deadNeighbors.add(new int[]{row - 1, column + 1});
            }
        }
        if (hasBottom) {
            // check if cell directly below current cell is dead
            if (board[row + 1][column] == 0) {
                deadNeighbors.add(new int[]{row + 1, column});
            }
            // check if diagonal lower left cell is dead
            if (hasLeft && board[row + 1][column - 1] == 0) {
                deadNeighbors.add(new int[]{row + 1, column - 1});
            }
            // check if diagonal lower right cell is dead
            if (hasRight && board[row + 1][column + 1] == 0) {
                deadNeighbors.add(new int[]{row + 1, column + 1});
            }
        }
        // check if adjacent left cell is dead
        if (hasLeft && board[row][column - 1] == 0) {
            deadNeighbors.add(new int[]{row, column - 1});
        }
        // check if adjacent right cell is dead
        if (hasRight && board[row][column + 1] == 0) {
            deadNeighbors.add(new int[]{row, column + 1});
        }

        return deadNeighbors;
    }

    // method to display current generation
    @Override
    public String toString() {
        StringBuilder gridSb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    gridSb.append("- ");
                } else {
                    gridSb.append("* ");
                }
            }
            gridSb.append("\n");
        }

        return gridSb.toString();
    }
}
