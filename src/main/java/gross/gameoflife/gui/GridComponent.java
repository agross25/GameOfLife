package gross.gameoflife.gui;

import gross.gameoflife.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GridComponent extends JComponent {

    private static Grid gameGrid;

    public GridComponent(Grid grid) {
        this.gameGrid = grid;

        Timer timer = new Timer(10, e -> repaint());
        timer.start();

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getX() / 20;
                int column = e.getY() / 20;

                if (grid.getCellStatus(row, column) == 0) // if cell is dead
                {
                    grid.setCellAlive(row, column);
                } else // if cell is alive
                {
                    grid.setCellDead(row, column);
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
                    g.fillRect(x * 20, y * 20, 20, 20);
                }
            }
        }
        repaint();

    }
}
