package gross.gameoflife;

import gross.gameoflife.grid.Grid;

public class GridTest {

    public static void main(String[] args) {

        Grid newGrid = new Grid(10, 10);

        System.out.println("GLIDER:");

        // Glider formation
        newGrid.setCellAlive(1, 6);
        newGrid.setCellAlive(2, 6);
        newGrid.setCellAlive(2, 8);
        newGrid.setCellAlive(3, 6);
        newGrid.setCellAlive(3, 7);

        System.out.println(newGrid.toString());

        for (int i = 0; i < 30; i++) {
            newGrid.nextGen();
            System.out.println(newGrid.toString());
        }

        Grid grid2 = new Grid(5, 5);

        System.out.println("LINE:");

        // Glider formation
        grid2.setCellAlive(1, 1);
        grid2.setCellAlive(1, 2);
        grid2.setCellAlive(1, 3);

        System.out.println(grid2.toString());

        for (int i = 0; i < 5; i++) {
            grid2.nextGen();
            System.out.println(grid2.toString());
        }
    }

}
