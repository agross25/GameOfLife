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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridFrame extends JFrame {

    private Timer timer;
    private Grid gameGrid;

    // default constructor
    public GridFrame() {
        gameGrid = new Grid(300, 300);
        setFrame(gameGrid);
    }

    // 2nd constructor
    public GridFrame(int[][] grid) {
        gameGrid = new Grid(grid);
        setFrame(gameGrid);
    }

    public void resetGrid(int[][] grid) {
        gameGrid = new Grid(grid);
        setFrame(gameGrid);
    }

    public void setFrame(Grid gameGrid) {
        setSize(800, 700);
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
        // System.out.println(paddedGrid.toString());
        GridComponent gridComponent = new GridComponent(paddedGrid);
        // GridComponent gridComponent = new GridComponent(gameGrid);
        pane.add(gridComponent, BorderLayout.CENTER);

        // Create a Timer that calls a method every second (1000 milliseconds)
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This method will be called every second
                System.out.println("Action!");
                gameGrid.nextGen();
                gridComponent.repaint();
            }
        });

        // Main Panel to contain whole bottom section
        JPanel buttonPanel = new JPanel(new BorderLayout());

        // Panels to contain other panels
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel bottomCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

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
        message.setBackground(buttonPanel.getBackground());

        leftPanel.add(rleLoader);
        leftPanel.add(message);

        bottomPanel.add(leftPanel);

        // Play and Pause Buttons
        JButton play = new JButton("\u25B6"); // Unicode play symbol
        play.setPreferredSize(new Dimension(80, 50));
        JButton pause = new JButton("\u23F8"); // Unicode pause symbol
        pause.setPreferredSize(new Dimension(80, 50));

        // Set larger font to enlarge symbols
        play.setFont(largeFont);
        pause.setFont(largerFont);

        // JPanels to hold the buttons
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
                System.out.println("Play!");
                if (!timer.isRunning()) {
                    timer.start();
                }
            }
        });

        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pause!");
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

                // Try to retrieve text from the clipboard
                try {
                    Transferable content = clipboard.getContents(null);

                    // Retrieve clipboard text
                    if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        clipboardText = (String) content.getTransferData(DataFlavor.stringFlavor);
                        // Check if the clipboard text is not empty
                        if (clipboardText != null && !clipboardText.trim().isEmpty()) {
                            // Check if the clipboardText is in URL format and leads to content
                            if (isValidAndAccessibleURL(clipboardText)) {
                                ArrayList<String> text = parseURL(clipboardText);
                                if (text != null) {
                                    // create an RLE Parser with this text
                                    RleParser parser = new RleParser(text);
                                    grid = parser.parseFile();
                                    inputValid = true;
                                }
                            }
                            // Test if it is a body of text with valid RLE format
                            else if (isValidRleFormat(clipboardText)) {
                                // parse text into ArrayList of strings
                                ArrayList<String> text = parseRleText(clipboardText);
                                // create an RLE Parser with this text
                                RleParser parser = new RleParser(text);
                                grid = parser.parseFile();
                                inputValid = true;
                            }
                            // Assume it is a file path
                            else {
                                // Create a Parser with this string - Parser will test if it is a valid file path
                                RleParser parser = new RleParser(clipboardText);
                                grid = parser.parseFile();
                                inputValid = true;
                            }
                            // If it does not meet any criteria
                            // use Regex to text if it is a valid URL, filepath, or RLE content.
                        } else {
                            errorMessage = "Empty or null string in clipboard.";
                        }
                    } else {
                        errorMessage = "No text found in clipboard.";
                    }
                    // Respond based on input validity
                    if (inputValid) {
                        message.setForeground(Color.BLACK);
                        message.setText(clipboardText);
                        if (grid != null) {
                            resetGrid(grid);
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
        int min = 30;

        // test whether height / width are below 100
        if (height < min) {
            if (width < min) { // both height and width are below minimum
                hDiff = (min - height) / 2;
                wDiff = (min - width) / 2;
                paddedGrid = new int[min][min];
            } else { // just height is below minimum -> set grid to width x width
                hDiff = (width - height) / 2;
                paddedGrid = new int[width][width];
            }
        } else if (width < min) // just width is below minimum -> set grid to height x height
        {
            wDiff = (height - width) / 2;
            paddedGrid = new int[height][height];
        } else // grid dimensions are an adequate size as is
        {
            // find which dimension is larger and make the grid that size
            if (height > width) {
                paddedGrid = new int[height][height];
            } else // if width > height or width == height
            {
                paddedGrid = new int[width][width];
            }
        }

        // loop through and copy grid content into paddedGrid
        for (int i = hDiff; i - hDiff < gameGrid.getHeight(); i++) {
            for (int j = wDiff; j - wDiff < gameGrid.getWidth(); j++) {
                if (gameGrid.getCellStatus(i - hDiff, j - wDiff) == 1) {
                    paddedGrid[i][j] = 1;
                }
            }
        }

        return new Grid(paddedGrid);
    }

    public boolean isValidAndAccessibleURL(String urlString) {
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

    public ArrayList<String> parseURL(String url) {
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
                    endIndex = tempText.substring(startIndex + 1).indexOf("#"); // cut off starting #
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
