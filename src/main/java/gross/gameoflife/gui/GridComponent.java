package gross.gameoflife.gui;

import gross.gameoflife.grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GridComponent extends JComponent {

    private Grid gameGrid;
    private int cellSize;

    public GridComponent(Grid grid) {
        this.gameGrid = grid;
        initMouseListener();
    }
    // Timer timer = new Timer(10, e -> repaint());

    private void initMouseListener() {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getX() / cellSize;
                int column = e.getY() / cellSize;

                if (gameGrid.getCellStatus(row, column) == 0) // if cell is dead
                {
                    gameGrid.setCellAlive(row, column);
                } else // if cell is alive
                {
                    gameGrid.setCellDead(row, column);
                }
                revalidate();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
    }

    public void setComponentGrid(Grid grid) {
        this.gameGrid = grid;
        removeMouseListeners();
        initMouseListener();
    }

    // Remove existing mouse listeners to prevent duplication
    private void removeMouseListeners() {
        for (MouseListener listener : getMouseListeners()) {
            removeMouseListener(listener);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Get the number of rows and columns from the grid
        int totalRows = gameGrid.getHeight();
        int totalCols = gameGrid.getWidth();

        // Get the available space in the component
        int availableHeight = getHeight();
        int availableWidth = getWidth();

        // Calculate the largest square cell size that fits within both dimensions
        int cellSize = Math.min(availableHeight / totalRows, availableWidth / totalCols);

        // Calculate how much space the grid will actually take up
        int gridHeight = cellSize * totalRows;
        int gridWidth = cellSize * totalCols;

        // Center the grid inside the component
        int startX = (availableWidth - gridWidth) / 2;
        int startY = (availableHeight - gridHeight) / 2;

        // Clear the background with a color
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the cells and gridlines using the calculated square cell size
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                int x = startX + col * cellSize;
                int y = startY + row * cellSize;

                // Draw live cells as green
                if (gameGrid.getCellStatus(row, col) == 1) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x, y, cellSize, cellSize); // Square cell
                }

                // Draw grid lines
                g.setColor(Color.GRAY);
                g.drawRect(x, y, cellSize, cellSize); // Square grid lines
            }
        }
    }
}
