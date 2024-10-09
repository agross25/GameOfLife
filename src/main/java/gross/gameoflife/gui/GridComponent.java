package gross.gameoflife.gui;

import gross.gameoflife.grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GridComponent extends JComponent {

    private Grid gameGrid;
    private int cellHeight;
    private int cellWidth;

    public GridComponent(Grid grid) {
        this.gameGrid = grid;

        cellHeight = getHeight() / gameGrid.getHeight();
        cellWidth = getWidth() / gameGrid.getWidth();

        if (cellHeight <= 0) {
            cellHeight = 20;
        }
        if (cellWidth <= 0) {
            cellWidth = 20;
        }

        Timer timer = new Timer(10, e -> repaint());

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getX() / cellHeight;
                int column = e.getY() / cellWidth;

                if (grid.getCellStatus(row, column) == 0) // if cell is dead
                {
                    grid.setCellAlive(row, column);
                } else // if cell is alive
                {
                    grid.setCellDead(row, column);
                }
//                revalidate();
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());


        for (int y = 0; y < gameGrid.getHeight(); y++) {
            for (int x = 0; x < gameGrid.getWidth(); x++) {
                if (gameGrid.getCellStatus(x, y) == 1) // if cell is alive
                {
                    g.setColor(Color.GREEN);
                    g.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                }
                g.setColor(Color.GRAY); // draw gridlines
                g.drawRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
            }
        }
    }
}
