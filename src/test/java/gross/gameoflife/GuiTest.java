package gross.gameoflife;

import gross.gameoflife.gui.GridFrame;
import gross.gameoflife.parser.RleParser;

public class GuiTest {
    public static void main(String[] args) {
//        GridFrame frame = new GridFrame();
//        frame.setVisible(true);

//        use Parser
        String pathname = "glider.rle";
        RleParser parser = new RleParser(pathname);
        int[][] grid = parser.parseFile();
        GridFrame frame = new GridFrame(grid);
        frame.setVisible(true);

    }
}
