package gross.gameoflife.gui;

import gross.gameoflife.GridController;
import gross.gameoflife.grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GridFrame extends JFrame {

    // private Timer timer;
    private Grid gameGrid;
    private GridComponent gridComponent;
    private final int minDimension = 100;
    private GridController controller;

    // default constructor
    public GridFrame() {
        gameGrid = new Grid(minDimension, minDimension);
        gridComponent = new GridComponent(gameGrid);
        controller = new GridController(gameGrid, gridComponent);
        setFrame();
        initMouseListener();
    }

    // 2nd constructor
    public GridFrame(int[][] grid) {
        gameGrid = new Grid(grid);
        gridComponent = new GridComponent(gameGrid);
        controller = new GridController(gameGrid, gridComponent);
        setFrame();
        initMouseListener();
    }

    public void setFrame() {
        setupFrame();
        Container pane = getContentPane();

        // Initial Set-up
        setupTitle(pane);
        setupGrid(pane);

        // Left side of main panel - RLE button and text area
        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton rleLoader = new JButton("Load RLE from Clipboard");
        JTextArea message = new JTextArea(1, 3);
        bottomLeftPanel.add(createRlePanel(rleLoader, message));

        // Center of main panel - play and pause buttons
        JButton play = new JButton();
        JButton pause = new JButton();
        JPanel bottomCenterPanel = createButtons(play, pause);

        // Main panel to hold entire bottom section
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(bottomLeftPanel, BorderLayout.WEST);
        buttonPanel.add(bottomCenterPanel, BorderLayout.CENTER);

        // Add all components to main pane
        pane.add(buttonPanel, BorderLayout.PAGE_END);
        pane.revalidate();
        pane.repaint();

        //add action listeners for buttons
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.startTimer();
            }
        });

        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.stopTimer();
            }
        });

        rleLoader.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Access the system clipboard
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                String textArea = "";
                try {
                    // Try to retrieve clipboard content
                    Transferable content = cb.getContents(null);
                    if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        String cbText = (String) content.getTransferData(DataFlavor.stringFlavor);
                        textArea = controller.paste(cbText);
                    } else {
                        textArea = "ERROR: No text found in clipboard.";
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (!textArea.contains("ERROR")) {
                    message.setForeground(Color.BLACK);
                    message.setText(textArea);
                } else if (message != null) {
                    message.setForeground(Color.RED);
                    message.setText(textArea);
                }
            }
        });
    }

    private JPanel createButtons(JButton play, JButton pause) {
        // Create Play and Pause Buttons
        play.setText("\u25B6"); // Unicode play symbol
        play.setPreferredSize(new Dimension(80, 50));
        pause.setText("\u23F8"); // Unicode pause symbol
        pause.setPreferredSize(new Dimension(80, 50));
        // Set larger font to enlarge symbols
        play.setFont(new Font("SansSerif", Font.PLAIN, 35));
        pause.setFont(new Font("SansSerif", Font.PLAIN, 55));
        // Panel to hold both buttons
        JPanel bottomCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomCenterPanel.add(play);
        bottomCenterPanel.add(pause);

        return bottomCenterPanel;
    }

    private JPanel createRlePanel(JButton rleLoader, JTextArea message) {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        // Create RLE button for left side
        rleLoader.setMaximumSize(rleLoader.getPreferredSize());
        rleLoader.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Create RLE text area for left side
        Dimension buttonSize = rleLoader.getPreferredSize(); // Get button preferred size
        message.setPreferredSize(buttonSize);
        message.setMaximumSize(buttonSize);
        message.setAlignmentX(Component.LEFT_ALIGNMENT);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setBackground(Color.LIGHT_GRAY);
        // Add all components to left panel and return
        leftPanel.add(rleLoader);
        leftPanel.add(message);
        return leftPanel;
    }

    private void setupTitle(Container pane) {
        JLabel title = new JLabel("Welcome to Game of Life!", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 25));
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setVerticalTextPosition(SwingConstants.CENTER);
        pane.add(title, BorderLayout.PAGE_START);
    }

    private void setupGrid(Container pane) {
        gameGrid = controller.calculateGrid(gameGrid);
        gridComponent.setComponentGrid(gameGrid);
        pane.add(gridComponent, BorderLayout.CENTER);
    }

    private void setupFrame() {
        setSize(900, 900);
        setTitle("Game of Life");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Set up Layout Manager
        setLayout(new BorderLayout());
    }

    private void initMouseListener() {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                controller.toggleCell(mouseX, mouseY);
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
    }
}
