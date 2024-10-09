package gross.gameoflife.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RleParser {

    private File file;
    private BufferedReader reader;
    private int[][] newGrid;
    private ArrayList<String> rleText;


    // Constructor for filepath
    public RleParser(String str) {
        // If string is a filepath:
        if (isValidPath(str)) {
            try {
                Path p = Paths.get(ClassLoader.getSystemResource(str).toURI());
                file = p.toFile();
                reader = new BufferedReader(new FileReader(file));
                rleText = getText();
            } catch (Exception e) {
                file = null;
                reader = null;
            }
        }
        newGrid = new int[0][0];
    }

    // Constructor for body of text / webpage text
    public RleParser(ArrayList<String> text) {
        rleText = text;
    }

    public boolean isValidPath(String path) {
        try {
            Path filePath = Paths.get(path);
            return true;  // Path is syntactically correct
        } catch (InvalidPathException e) {
            return false;  // Invalid path
        }
    }

    public boolean pathExists(String path) {
        try {
            Path p = Paths.get(ClassLoader.getSystemResource(path).toURI());
            file = p.toFile();
            reader = new BufferedReader(new FileReader(file));
            return true; // Path is accessible
        } catch (Exception e) {
            return false; // Path does not exist or is inaccessible
        }
    }

    // Main method for parsing - calls other methods
    public int[][] parseFile() {

        // Create Pattern and Matcher for Regular Expressions
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = null;

        // Eliminate comments above header
        rleText = clean(rleText);

        // Identify header and cell text
        int headerIndex = getHeaderIndex(rleText);
        String header = rleText.get(headerIndex);
        String cellText = new String();
        // Copies all lines after header since cell pattern sometimes spans multiple lines
        for (int i = headerIndex + 1; i < rleText.size(); i++) {
            cellText += rleText.get(i);
        }

        // Retrieve dimensions based on header info - [height, width]
        int[] dimensions = findDimensions(header, pattern, matcher);

        // Account for errors in RLE file
        if (dimensions == null) {
            // System.out.println("Input invalid.");
            return null;
        }

        // Create new grid of the specified dimensions
        newGrid = new int[dimensions[0]][dimensions[1]];
        // Create array of Strings representing rows of grid
        String[] gridRows = cellText.split("\\$");

        for (int row = 0; row < gridRows.length; row++) {
            String nextLine = gridRows[row];
            int column = -1; // tracks which column in the grid we are up to

            // Parse out all characters from each line
            for (int i = 0; i < nextLine.length(); i++) {
                matcher = pattern.matcher(nextLine);
                int numCells = 1;
                if (matcher.find() && matcher.start() == 0) // if the integer is at the first index of the line
                {
                    numCells = Integer.parseInt(matcher.group());
                    nextLine = nextLine.substring(matcher.end()); // remove from line
                    i = 0; // start from the beginning again in case number is 2+ digits
                }
                // Isolate the next character and identify it
                Character symbol = nextLine.charAt(0);

                // Set cells alive if symbol is o
                int runs = column + numCells; // will always run at least once
                while (column < runs) {
                    column++;
                    if (symbol == 'o') {
                        newGrid[row][column] = 1;
                    } else if (symbol == '!') {
                        i = nextLine.length(); // cuts out any text that may accidentally come after the !
                        break;
                    }
                }
                if (nextLine.length() > 1) {
                    nextLine = nextLine.substring(1); // remove from line
                } else {
                    nextLine = "";
                }
                i--;
            }
        }
        return newGrid;
    }

    public ArrayList<String> getText() {
        ArrayList<String> rleText = new ArrayList<>();
        String line;
        try {
            while (((line = reader.readLine()) != null)) {
                rleText.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rleText;
    }

    public ArrayList<String> clean(ArrayList<String> txt) {
        ArrayList<String> cleanTxt = new ArrayList<>();
        // Identify the index of the header by skipping lines beginning with #
        int index = 0;
        while (txt.get(index).startsWith("#")) {
            index++;
        }

        // Copy the remaining Strings to new array list
        for (int i = index; i < txt.size(); i++) {
            cleanTxt.add(txt.get(i));
        }

        return cleanTxt;
    }

    public int getHeaderIndex(ArrayList<String> rleText) {
        int index = 0;
        for (int i = 0; i < rleText.size(); i++) {
            String str = rleText.get(i).toLowerCase(); // in case of accidental capital X
            if (str.startsWith("x =")) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void printGrid() {
        StringBuilder gridSb = new StringBuilder();

        for (int i = 0; i < newGrid.length; i++) {
            for (int j = 0; j < newGrid[i].length; j++) {
                if (newGrid[i][j] == 0) {
                    gridSb.append("- ");
                } else {
                    gridSb.append("* ");
                }
            }
            gridSb.append("\n");
        }

        System.out.println(gridSb);
    }

    public int[] findDimensions(String header, Pattern pattern, Matcher matcher) {
        // Declare dimensions array and strings to hold height and width expressions
        int[] dimens = new int[2];
        String x;
        String y;

        if (!header.isEmpty()) {
            // Find indices of x, y, and comma(s)
            int xIndex = header.indexOf("x");
            int yIndex = header.indexOf("y");
            int comma1 = header.indexOf(",");
            // If there is a second comma, it is assigned into comma2. Otherwise, it is the end of the string.
            int comma2 = header.lastIndexOf(",") > yIndex ? header.lastIndexOf(",") : header.length() - 1;

            // Isolate height statement
            x = header.substring(xIndex, comma1);
            // Isolate width statement
            y = header.substring(yIndex, comma2);
        } else {
            System.out.println("Error with dimension input.");
            return null;
        }

        // Find integer values of x and y using regex
        String[] dimenStr = {x, y};
        for (int i = 0; i < 2; i++) {
            matcher = pattern.matcher(dimenStr[i]);
            if (matcher.find()) {
                dimens[i] = Integer.parseInt(matcher.group());
            }
        }
        return dimens;
    }

}
