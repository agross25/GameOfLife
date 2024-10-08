package gross.gameoflife.gui;

import gross.gameoflife.grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GridFrame extends JFrame {

    private Timer timer;

    // default constructor
    public GridFrame() {
        Grid gameGrid = new Grid(300, 300);
        setFrame(gameGrid);
    }

    // 2nd constructor
    public GridFrame(int[][] grid) {
        Grid gameGrid = new Grid(grid);
        setFrame(gameGrid);
    }

    public void setFrame(Grid gameGrid) {
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

        Grid paddedGrid = calculateGrid(gameGrid);
        GridComponent gridComponent = paddedGrid == null ? new GridComponent(gameGrid) : new GridComponent(paddedGrid);
        pane.add(gridComponent, BorderLayout.CENTER);

        // Create a Timer that calls a method every second (1000 milliseconds)
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This method will be called every second
                gameGrid.nextGen();
                gridComponent.repaint();
            }
        });

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

        // add action listeners for buttons
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!timer.isRunning()) {
                    timer.start();
                }
            }
        });

        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer.isRunning()) {
                    timer.stop();
                }
            }
        });

    }

    public Grid calculateGrid(Grid gameGrid) {
        int[][] paddedGrid = null;

        int height = gameGrid.getHeight();
        int width = gameGrid.getWidth();
        if (height < 100) {
            if (width < 100) { // both height and width are below minimum
                int hDiff = (100 - height) / 2;
                int wDiff = (100 - width) / 2;
                paddedGrid = new int[100][100];
                // loop through and copy grid info over to this new one
                for (int i = hDiff - 1; i < paddedGrid.length - wDiff - 1; i++) {
                    // ----------------->>>
                }
            } else { // just height is below minimum
                int hDiff = (100 - height) / 2;
                paddedGrid = new int[100][width];
            }
        } else if (width < 100) // just width is below minimum
        {
            int wDiff = (100 - width) / 2;
            paddedGrid = new int[height][100];
        } else // grid dimensions are an adequate size as is
        {
            return null;
        }
        return new Grid(paddedGrid);
    }

}
