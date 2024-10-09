package gross.gameoflife.gui;

import gross.gameoflife.grid.Grid;
import gross.gameoflife.parser.RleParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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

    // default constructor
    public GridFrame() {
        gameGrid = new Grid(100, 100);
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
        // setFrame();
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

        // Create a Timer that calls a method every second (1000 milliseconds)
        timer = new Timer(1000, new ActionListener() {
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
                boolean inputValid = false;
                String clipboardText = "";
                String errorMessage = "";
                String successMessage = "";

                // Try to retrieve text from the clipboard
                try {
                    Transferable content = clipboard.getContents(null);

                    // Retrieve clipboard text
                    if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        clipboardText = (String) content.getTransferData(DataFlavor.stringFlavor);
                        // Check if the clipboard text is not empty
                        if (clipboardText != null && !clipboardText.trim().isEmpty()) {
                            // Check if the clipboardText is in URL format and leads to content
                            if (isValidAndAccessibleUrl(clipboardText)) {
                                ArrayList<String> text = parseUrl(clipboardText);
                                if (text != null) {
                                    // create an RLE Parser with this text
                                    RleParser parser = new RleParser(text);
                                    grid = parser.parseFile();
                                    if (grid == null) {
                                        errorMessage = "Webpage provided is not in RLE format.";
                                    } else {
                                        inputValid = true;
                                        successMessage = "Successfully parsed RLE webpage.";
                                    }
                                }
                            }
                            // Test if it is a body of text with valid RLE format
                            else if (isValidRleFormat(clipboardText)) {
                                // parse text into ArrayList of strings
                                ArrayList<String> text = parseRleText(clipboardText);
                                // create an RLE Parser with this text
                                RleParser parser = new RleParser(text);
                                grid = parser.parseFile();
                                if (grid == null) {
                                    errorMessage = "Text provided is not in RLE format.";
                                } else {
                                    inputValid = true;
                                    successMessage = "Successfully parsed RLE text.";
                                }
                            }
                            // Test if it is a file path
                            else if (isValidPath(clipboardText)) {
                                if (Files.exists(Paths.get(clipboardText))) {
                                    RleParser parser = new RleParser(clipboardText);
                                    grid = parser.parseFile();
                                    if (grid == null) {
                                        errorMessage = "File Path invalid or inaccessible.";
                                    } else {
                                        inputValid = true;
                                        successMessage = "Successfully parsed RLE file.";
                                    }
                                } else {
                                    errorMessage = "File Path invalid or inaccessible.";
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

                    // Respond based on input validity
                    if (inputValid) {
                        message.setForeground(Color.BLACK);
                        message.setText(successMessage);
                        if (grid != null) {
                            resetGrid(grid);
                            // Update the displayed grid component with the new grid
                            pane.remove(gridComponent); // Remove the old grid
                            gridComponent.setComponentGrid(gameGrid); // Create a new grid component with the new grid
                            pane.add(gridComponent, BorderLayout.CENTER); // Add the updated grid component
                            pane.revalidate(); // Revalidate the panel to apply changes
                            pane.repaint();    // Repaint the panel to reflect the new grid
                            gridComponent.repaint();
                        }
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
        int min = 100;

        // test whether height / width are below 100
        // Determine the dimensions for the new padded grid
        if (height < min) {
            hDiff = (min - height) / 2;
            height = min;
        }
        if (width < min) {
            wDiff = (min - width) / 2;
            width = min;
        }

        paddedGrid = new int[height][width];

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
        ArrayList<String> rleText = new ArrayList<>();
        try {
            // Connect to the URL and fetch the HTML content
            Document doc = Jsoup.connect(url).get();
            Elements textElements = doc.select("p, h1, h2, h3, h4, h5, h6, div, span");
            for (Element element : textElements) {
                String str = element.text();
                if (!str.trim().isEmpty()) { // Skip empty or whitespace-only text
                    rleText.add(str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rleText;
    }

    public ArrayList<String> parseRleText(String text) {
        ArrayList<String> rleText = new ArrayList<>();
        String tempText = text;

        // Parse commented lines above header (if applicable)
        if (text.startsWith("#")) {
            int startIndex = 0;
            int endIndex;
            while (tempText.contains("#")) // if there is at least one '#' in the string
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
                tempText = tempText.substring(endIndex);
                rleText.add(line);
            }
        }

        // Parse header
        String line = "";
        Pattern header = Pattern.compile("x\\s=\\s(\\d+),\\sy\\s=\\s(\\d+)");
        Pattern rule = Pattern.compile("rule\\\\s=\\\\s([a-zA-Z0-9]+)");
        Matcher matcher = rule.matcher(tempText);
        if (!matcher.find()) {
            matcher = header.matcher(tempText);
        }
        if (matcher.find()) {
            line = tempText.substring(0, matcher.end());
            tempText = tempText.substring(matcher.end()); // cut off header
        }
        rleText.add(line);

        // Put the rest of text in the array
        String conwayChars = "^[ 0-9$!bo]+$"; // checks for only allowed characters
        if (tempText.matches(conwayChars) && (tempText.endsWith("!"))) // contains only one ! at the end
        {
            rleText.add(tempText);
        }

        return rleText;
    }
}
