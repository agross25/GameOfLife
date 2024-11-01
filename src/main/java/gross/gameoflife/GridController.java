package gross.gameoflife;

import gross.gameoflife.grid.Grid;
import gross.gameoflife.gui.GridComponent;
import gross.gameoflife.parser.RleParser;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
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

public class GridController {

    private Grid model;
    private GridComponent view;
    private final int minDimension = 100;
    private Timer timer;

    public GridController(Grid grid, GridComponent v) {
        // pass in mock grid and mock view
        model = grid;
        view = v;
        timer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This method will be called every second
                model.nextGen();
                view.repaint();
            }
        });
    }

    public void startTimer() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    public String paste(String cbText) { // just send in string
        int[][] grid = null;
        String message = "";
        // Check that the clipboard text is not empty
        if (cbText != null && !cbText.trim().isEmpty()) {
            // Check if the clipboardText is in URL format and leads to content
            if (isValidAndAccessibleUrl(cbText)) {
                ArrayList<String> text = parseUrl(cbText);
                RleParser parser = new RleParser(text);
                grid = parser.parseFile();
                if (grid == null) {
                    message = "ERROR: Webpage provided is not in RLE format.";
                } else {
                    message = "Successfully parsed RLE webpage.";
                }
            } else if (isValidRleFormat(cbText)) { // Check if it is a valid body of RLE text
                ArrayList<String> text = parseRleText(cbText);
                RleParser parser = new RleParser(text);
                grid = parser.parseFile();
                if (grid == null) {
                    message = "ERROR: Text provided is not in RLE format.";
                } else {
                    message = "Successfully parsed RLE text.";
                }
            } else if (isValidPath(cbText)) { // Check if it is a valid file path
                try {
                    if (Files.exists(Paths.get(cbText))) {
                        RleParser parser = new RleParser(cbText);
                        grid = parser.parseFile();
                        if (grid == null) {
                            message = "ERROR: File Path invalid or inaccessible.";
                        } else {
                            message = "Successfully parsed RLE file.";
                        }
                    } else {
                        message = "ERROR: File Path invalid or inaccessible.";
                    }
                } catch (InvalidPathException ex) {
                    message = "ERROR: Clipboard content is not a valid file path.";
                }
            } else { // If it does not meet any criteria
                message = "ERROR: Input does not match any RLE format criteria.";
            }
        } else {
            message = "ERROR: Empty or null string in clipboard.";
        }
        // Display appropriate message
        if (!message.contains("ERROR")) {
            Grid g = new Grid(grid);
            g = calculateGrid(g);
            model = g;
            view.setComponentGrid(g);
            view.repaint();
        }
        return message;
    }

    public Grid calculateGrid(Grid gameGrid) {
        int[][] paddedGrid = null;
        int height = gameGrid.getHeight();
        int width = gameGrid.getWidth();
        int hDiff = 0;
        int wDiff = 0;
        // Determine the dimensions for the new padded grid
        if (height < minDimension) {
            hDiff = (minDimension - height) / 2;
            height = minDimension;
        }
        if (width < minDimension) {
            wDiff = (minDimension - width) / 2;
            width = minDimension;
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
                if (text.contains("!")) {
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

        // Remove escape sequences
        String cellPattern = tempText.toString().replaceAll("\\r?\\n", "");
        // Put the rest of text in the array
        if (cellPattern.endsWith("!")) {
            rleText.add(cellPattern);
        }

        return rleText;
    }

    public void toggleCell(int mouseX, int mouseY) {
        int startX = view.getStartX();
        int startY = view.getStartY();
        int adjustedX = mouseX - startX;
        int adjustedY = mouseY - startY;
        int cellSize = view.getCellSize();

        if (adjustedX >= 0 && adjustedY >= 0) {
            if (adjustedX < cellSize * model.getWidth() && adjustedY < cellSize * model.getHeight()) {
                int clickedRow = adjustedY / cellSize;
                int clickedCol = adjustedX / cellSize;
                if (model.getCellStatus(clickedRow, clickedCol) == 0) {
                    model.setCellAlive(clickedRow, clickedCol);
                } else {
                    model.setCellDead(clickedRow, clickedCol);
                }
                System.out.println(clickedRow + ", " + clickedCol);
                view.repaint();
                System.out.println("Repainted!");
            }
        }
    }
//        int x = startX / view.getWidth();
//        int y = startY / view.getHeight();
//        if (x < model.getWidth() && y < model.getHeight()) {
//            int currentState = model.getCellStatus(x, y);
//            if (currentState == 1) {
//                model.setCellDead(x, y);
//            } else {
//                model.setCellAlive(x, y);
//            }
//            view.repaint();
//        }
//    }
}
