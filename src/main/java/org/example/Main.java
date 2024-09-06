package org.example;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        Grid newGrid = new Grid(5,5);

        newGrid.setCellAlive(1,2);
        newGrid.setCellAlive(2,0);
        newGrid.setCellAlive(2,2);
        newGrid.setCellAlive(3,1);
        newGrid.setCellAlive(3,2);

        System.out.println(newGrid.toString());

        for (int i=0; i<5; i++) {
            newGrid.nextGen();
            System.out.println(newGrid.toString());
        }
    }
}