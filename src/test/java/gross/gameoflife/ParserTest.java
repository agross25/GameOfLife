package gross.gameoflife;

import gross.gameoflife.grid.Grid;
import gross.gameoflife.parser.RleParser;

public class ParserTest {

    public static void main(String[] args) {
        String pathname = "glider.rle";
        RleParser parser = new RleParser(pathname);
        int[][] grid = parser.parseFile();

        System.out.println("Figure 1");
        parser.printGrid();

        System.out.println("Figure 2");
        Grid board = new Grid(grid);
        System.out.println(board.toString());
    }
}
