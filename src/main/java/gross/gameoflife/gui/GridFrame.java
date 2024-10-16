package gross.gameoflife.gui;

import gross.gameoflife.grid.Grid;
import gross.gameoflife.parser.RleParser;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridFrame extends JFrame {

    private Timer timer;
    private Grid gameGrid;
    private GridComponent gridComponent;
    private final int MIN = 100;

    // default constructor
    public GridFrame() {
        gameGrid = new Grid(MIN, MIN);
        gridComponent = new GridComponent(gameGrid);
        setFrame();
    }

    // 2nd constructor
    public GridFrame(int[][] grid) {
        gameGrid = new Grid(grid);
        gridComponent = new GridComponent(gameGrid);
        setFrame();
    }

    public void resetGrid(int[][] grid) {
        gameGrid = new Grid(grid);
        gridComponent.setComponentGrid(gameGrid); // Pass the updated grid to the existing GridComponent
        gridComponent.repaint();
    }

    public void setFrame() {
        setSize(900, 900);
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

        gameGrid = calculateGrid(gameGrid);
        gridComponent.setComponentGrid(gameGrid);
        pane.add(gridComponent, BorderLayout.CENTER);

        // Create a Timer that calls nextGen()
        timer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This method will be called every second
                gameGrid.nextGen();
                gridComponent.repaint();
            }
        });

        // Box Panel for RLE button and text area
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Create RLE button and text area for left side
        JButton rleLoader = new JButton("Load RLE from Clipboard");
        rleLoader.setMaximumSize(rleLoader.getPreferredSize());
        rleLoader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea message = new JTextArea(1, 3);
        Dimension buttonSize = rleLoader.getPreferredSize(); // Get button preferred size
        message.setPreferredSize(buttonSize);
        message.setMaximumSize(buttonSize);
        message.setAlignmentX(Component.LEFT_ALIGNMENT);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);

        // Main Panel to contain whole bottom section
        JPanel buttonPanel = new JPanel(new BorderLayout());
        message.setBackground(buttonPanel.getBackground());

        leftPanel.add(rleLoader);
        leftPanel.add(message);

        // Panel to elements on left
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(leftPanel);

        // Play and Pause Buttons
        JButton play = new JButton("\u25B6"); // Unicode play symbol
        play.setPreferredSize(new Dimension(80, 50));
        JButton pause = new JButton("\u23F8"); // Unicode pause symbol
        pause.setPreferredSize(new Dimension(80, 50));

        // Set larger font to enlarge symbols
        play.setFont(largeFont);
        pause.setFont(largerFont);

        // Panel to hold the buttons
        JPanel bottomCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomCenterPanel.add(play);
        bottomCenterPanel.add(pause);

        buttonPanel.add(bottomPanel, BorderLayout.WEST);
        buttonPanel.add(bottomCenterPanel, BorderLayout.CENTER);

        pane.add(buttonPanel, BorderLayout.PAGE_END);

        pane.revalidate();
        pane.repaint();

        //add action listeners for buttons
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

        rleLoader.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Access the system clipboard
                int[][] grid = null;

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String clipboardText = "";
                String errorMessage = "";
                String successMessage = "";

                try {
                    // Try to retrieve clipboard content
                    Transferable content = clipboard.getContents(null);
                    if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        clipboardText = (String) content.getTransferData(DataFlavor.stringFlavor);

                        // Check that the clipboard text is not empty
                        if (clipboardText != null && !clipboardText.trim().isEmpty()) {
                            // Check if the clipboardText is in URL format and leads to content
                            if (isValidAndAccessibleUrl(clipboardText)) {
                                ArrayList<String> text = parseUrl(clipboardText);
                                RleParser parser = new RleParser(text);
                                grid = parser.parseFile();
                                if (grid == null) {
                                    errorMessage = "Webpage provided is not in RLE format.";
                                } else {
                                    successMessage = "Successfully parsed RLE webpage.";
                                }
                            }
                            // Check if it is a valid body of RLE text
                            else if (isValidRleFormat(clipboardText)) {
                                ArrayList<String> text = parseRleText(clipboardText);
                                RleParser parser = new RleParser(text);
                                grid = parser.parseFile();
                                if (grid == null) {
                                    errorMessage = "Text provided is not in RLE format.";
                                } else {
                                    successMessage = "Successfully parsed RLE text.";
                                }
                            }
                            // Check if it is a valid file path
                            else if (isValidPath(clipboardText)) {
                                try {
                                    if (Files.exists(Paths.get(clipboardText))) {
                                        RleParser parser = new RleParser(clipboardText);
                                        grid = parser.parseFile();
                                        if (grid == null) {
                                            errorMessage = "File Path invalid or inaccessible.";
                                        } else {
                                            successMessage = "Successfully parsed RLE file.";
                                        }
                                    } else {
                                        errorMessage = "File Path invalid or inaccessible.";
                                    }
                                } catch (InvalidPathException ex) {
                                    errorMessage = "Clipboard content is not a valid file path.";
                                }
                            } else { // If it does not meet any criteria
                                errorMessage = "Input does not match any RLE format criteria.";
                            }
                        } else {
                            errorMessage = "Empty or null string in clipboard.";
                        }
                    } else {
                        errorMessage = "No text found in clipboard.";
                    }

                    // Display appropriate message
                    if (!successMessage.isEmpty()) {
                        message.setForeground(Color.BLACK);
                        message.setText(successMessage);
                        Grid g = new Grid(grid);
                        g = calculateGrid(g);
                        resetGrid(g.getGrid());
                    } else {
                        message.setForeground(Color.RED);
                        message.setText(errorMessage);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public Grid calculateGrid(Grid gameGrid) {
        int[][] paddedGrid = null;

        int height = gameGrid.getHeight();
        int width = gameGrid.getWidth();
        int hDiff = 0;
        int wDiff = 0;

        // test whether height / width are below 100
        // Determine the dimensions for the new padded grid
        if (height < MIN) {
            hDiff = (MIN - height) / 2;
            height = MIN;
        }
        if (width < MIN) {
            wDiff = (MIN - width) / 2;
            width = MIN;
        }
        int minDimen = Math.min(height, width);

        paddedGrid = new int[minDimen][minDimen];

        // Copy the original grid into the padded grid
        for (int i = 0; i < gameGrid.getHeight(); i++) {
            for (int j = 0; j < gameGrid.getWidth(); j++) {
                paddedGrid[i + hDiff][j + wDiff] = gameGrid.getCellStatus(i, j);
            }
        }

        return new Grid(paddedGrid);
    }

    public boolean isValidAndAccessibleUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // mimics a browser request
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidRleFormat(String text) {
        if (text.startsWith("#") || text.startsWith("x = ")) {
            // Look for header format
            Pattern regex = Pattern.compile("x\\s=\\s(\\d+),\\sy\\s=\\s(\\d+)");
            Matcher matcher = regex.matcher(text);
            if (matcher.find()) {
                if (text.endsWith("!")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isValidPath(String path) {
        try {
            Path filePath = Paths.get(path);
            return true;  // Path exists and is accessible
        } catch (InvalidPathException e) {
            return false;  // Invalid path
        }
    }

    public ArrayList<String> parseUrl(String url) {
        String rleContents = "";
        try {
            InputStream in = new URL(url).openStream();
            rleContents = IOUtils.toString(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseRleText(rleContents);
    }

    public ArrayList<String> parseRleText(String text) {
        ArrayList<String> rleText = new ArrayList<>();
        StringBuilder tempText = new StringBuilder(text);

        // Parse commented lines above header (if applicable)
        if (text.startsWith("#")) {
            int startIndex = 0;
            int endIndex;
            while (tempText.indexOf("#") >= 0) // if there is at least one '#' in the string
            {
                if (tempText.lastIndexOf("#") == startIndex) // if there is only 1 # symbol
                {
                    endIndex = tempText.indexOf("x = ");
                } else // if there are 2+ # symbols in the string
                {
                    String tempStr = tempText.substring(startIndex + 1); // cut off first #
                    endIndex = tempStr.indexOf("#") + 1; // get second #
                }
                String line = tempText.substring(startIndex, endIndex);
                tempText.replace(startIndex, endIndex, "");
                rleText.add(line);
            }
        }

        // Parse header
        String line = "";
        Pattern header = Pattern.compile("x\\s=\\s(\\d+),\\sy\\s=\\s(\\d+)");
        Pattern rule = Pattern.compile("rule = [\\S\\s]+?\\n");
        Matcher matcher = rule.matcher(tempText);

        boolean found = matcher.find();
        int end = matcher.end();
        // What is wrong with this block of code?
        // Matcher.find() is returning true but program does not enter second loop
        if (!found) {
            matcher = header.matcher(tempText);
            end = matcher.end();
        }
        if (found || matcher.find()) {
            line = tempText.toString().substring(0, end);
            tempText.replace(0, matcher.end(), ""); // cut off header
        }
        rleText.add(line);

        // Put the rest of text in the array
        if (tempText.toString().endsWith("!")) // contains one ! at the end
        {
            int enter = tempText.indexOf("\n");
            while (enter >= 0) // if string contains a "\n"
            {
                String str = tempText.substring(0, enter);
                rleText.add(str);
                tempText = new StringBuilder(tempText.substring(enter + 1)); // isolate next part of string
                enter = tempText.indexOf("\n"); // re-assign enter
            }
            if (!tempText.isEmpty()) {
                rleText.add(tempText.toString());
            }
        }

        return rleText;
    }
}
