package gross.gameoflife.gui;

import gross.gameoflife.Grid;

import javax.swing.*;
import java.awt.*;

public class GridFrame extends JFrame {

    public GridFrame() {
        setSize(800, 600);
        setTitle("Game of Life");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set up Layout Manager
        setLayout(new BorderLayout());
        Container pane = getContentPane();

        // Create pre-set fonts
        Font mediumFont = new Font("SansSerif", Font.PLAIN, 25);
        Font largeFont = new Font("SansSerif", Font.PLAIN, 35);
        Font largerFont = new Font("SansSerif", Font.PLAIN, 55);

        // Set up Views
        JLabel title = new JLabel("Welcome to Game of Life!", JLabel.CENTER);
        title.setFont(mediumFont);
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setVerticalTextPosition(SwingConstants.CENTER);
        pane.add(title, BorderLayout.PAGE_START);

        // Component 2 - CENTER

        Grid gameGrid = new Grid(300, 300);
        GridComponent gridComponent = new GridComponent(gameGrid);
        pane.add(gridComponent, BorderLayout.CENTER);
        // - add action Listeners for buttons and clicking of any square
        // - use a timer for playing - every second, board updated
        // Component 3 - LINE_START (left)
        // Component 4 - LINE_END (right)

        JButton play = new JButton("\u25B6"); // Unicode play symbol
        play.setPreferredSize(new Dimension(100, 45));
        JButton pause = new JButton("\u23F8"); // Unicode pause symbol
        pause.setPreferredSize(new Dimension(100, 45));

        // Set larger font to enlarge symbols
        play.setFont(largeFont);
        pause.setFont(largerFont);

        // JPanel with GridLayout to hold the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // 1 row, 2 columns
        buttonPanel.add(play);
        buttonPanel.add(pause);

        pane.add(buttonPanel, BorderLayout.PAGE_END);

    }


}
