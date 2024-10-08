package gross.gameoflife;

import gross.gameoflife.parser.RleParser;

public class ParserTest {

    public static void main(String[] args) {
        String pathname = "glider.rle";
        RleParser parser = new RleParser(pathname);
        parser.parseFile();
        parser.printGrid();


    }
}
