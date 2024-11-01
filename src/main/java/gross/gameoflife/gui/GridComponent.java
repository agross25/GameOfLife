package gross.gameoflife.gui;

import gross.gameoflife.grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GridComponent extends JComponent {

    private Grid gameGrid;
    private int cellSize;
    private int startX;
    private int startY;

    public GridComponent(Grid grid) {
        this.gameGrid = grid;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                recalculateGridDimensions();
            }
        });
    }

    private void recalculateGridDimensions() {
        // Calculate number of rows and columns in grid
        int totalRows = gameGrid.getHeight();
        int totalCols = gameGrid.getWidth();

        // Calculate available height and width in component
        int componentHeight = getHeight(); // including top and bottom?
        int componentWidth = getWidth();

        // Calculate biggest possible cell size for square cell
        cellSize = Math.min(componentHeight / totalRows, componentWidth / totalCols);

        // Get size of the grid in pixels
        int gridHeight = cellSize * totalRows;
        int gridWidth = cellSize * totalCols;

        // How much padding will be on either side of grid - grid starts here
        startX = (componentWidth - gridWidth) / 2;
        startY = (componentHeight - gridHeight) / 2;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setComponentGrid(Grid grid) {
        this.gameGrid = grid;
    }

    // Remove existing mouse listeners to prevent duplication


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
        startX = (availableWidth - gridWidth) / 2;
        startY = (availableHeight - gridHeight) / 2;

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
