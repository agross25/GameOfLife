package gross.gameoflife;

import gross.gameoflife.gui.GridFrame;

public class GuiTest {
    public static void main(String[] args) {
        GridFrame frame = new GridFrame();
        frame.setVisible(true);

        // use Parser
        // String pathname = "glider.rle";
        // RleParser parser = new RleParser(pathname);
        // int[][] grid = parser.parseFile();
        // GridFrame frame2 = new GridFrame(grid);
        // frame2.setVisible(true);

    }
}
