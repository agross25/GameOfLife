package org.example;

public class GridTest {

    public static void main(String[] args) {

        Grid newGrid = new Grid(25,25);

        // Glider formation
        newGrid.setCellAlive(1,21);
        newGrid.setCellAlive(2,21);
        newGrid.setCellAlive(2,23);
        newGrid.setCellAlive(3,21);
        newGrid.setCellAlive(3,22);

        System.out.println(newGrid.toString());

        for (int i=0; i<24; i++) {
            newGrid.nextGen();
            System.out.println(newGrid.toString());
        }
    }

}
