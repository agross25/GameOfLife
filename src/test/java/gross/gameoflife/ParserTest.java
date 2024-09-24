package gross.gameoflife;

import gross.gameoflife.parser.RleParser;

public class ParserTest {

    public static void main(String[] args) {
        String pathname = "/Users/adinagross/Library/Mobile Documents/com~apple~CloudDocs/" +
                "Touro/MCON/MCON357-Practicum_in_Software_Dev/GameOfLife/RLEfiles/1beacon.rle";
        RleParser parser = new RleParser(pathname);
        parser.parseFile();
        parser.printGrid();
    }
}
