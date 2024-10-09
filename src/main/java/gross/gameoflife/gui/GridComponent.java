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
                // Get the number of rows and columns from the grid
                int totalRows = gameGrid.getHeight();
                int totalCols = gameGrid.getWidth();

                // Get the available space in the component
                int availableHeight = getHeight();
                int availableWidth = getWidth();

                // Determine the largest square cell size that fits within the available width and height
                int cellSize = Math.min(availableHeight / totalRows, availableWidth / totalCols);

                // Calculate the total grid size based on the cell size
                int gridHeight = cellSize * totalRows;
                int gridWidth = cellSize * totalCols;

                // Calculate the starting X and Y positions to center the grid
                int startX = (availableWidth - gridWidth) / 2;
                int startY = (availableHeight - gridHeight) / 2;

                // Get the mouse click coordinates
                int mouseX = e.getX();
                int mouseY = e.getY();

                // Adjust the mouse coordinates to match the grid's position
                int adjustedX = mouseX - startX;
                int adjustedY = mouseY - startY;

                // Ensure the click is within the bounds of the grid
                if (adjustedX >= 0 && adjustedY >= 0 && adjustedX < gridWidth && adjustedY < gridHeight) {
                    // Calculate the row and column that was clicked
                    int clickedRow = adjustedY / cellSize;
                    int clickedCol = adjustedX / cellSize;

                    // Toggle the cell's status
                    if (gameGrid.getCellStatus(clickedRow, clickedCol) == 0) {
                        gameGrid.setCellAlive(clickedRow, clickedCol);
                    } else {
                        gameGrid.setCellDead(clickedRow, clickedCol);
                    }

                    // Repaint the grid to reflect changes
                    repaint();
                }
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

        // Determine the largest square cell size that fits within the available width and height
        cellSize = Math.min(availableHeight / totalRows, availableWidth / totalCols);

        // Calculate the total grid size based on the cell size
        int gridHeight = cellSize * totalRows;
        int gridWidth = cellSize * totalCols;

        // Center the grid inside the component
        int startX = (availableWidth - gridWidth) / 2;
        int startY = (availableHeight - gridHeight) / 2;

        // Draw the cells and gridlines using the calculated square cell size
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                int x = startX + col * cellSize;
                int y = startY + row * cellSize;

                // Draw live cells as green
                if (gameGrid.getCellStatus(row, col) == 1) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x, y, cellSize, cellSize); // Draw square cell
                }

                // Draw grid lines
                g.setColor(Color.GRAY);
                g.drawRect(x, y, cellSize, cellSize); // Draw square grid lines
            }
        }
    }
}
