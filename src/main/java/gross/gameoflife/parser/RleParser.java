package gross.gameoflife.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RleParser {

    private BufferedReader reader;
    private int[][] newGrid;
    private ArrayList<String> rleText;


    // Constructor for filepath
    public RleParser(String str) {
        // If string is a filepath:
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream(str);
            reader = new BufferedReader(new InputStreamReader(in));
            rleText = getText();
        } catch (Exception e) {
            e.printStackTrace();
            reader = null;
        }
        newGrid = new int[0][0];
    }

    // Constructor for body of text / webpage text
    public RleParser(ArrayList<String> text) {
        rleText = text;
    }

    // Main method for parsing - calls other methods
    public int[][] parseFile() {

        // Create Pattern and Matcher for Regular Expressions
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = null;

        // Eliminate comments above header
        rleText = removeComments(rleText);

        // Identify header and cell text
        int headerIndex = getHeaderIndex(rleText);
        if (headerIndex >= 0) { // if it has a header
            String header = rleText.get(headerIndex);
            StringBuilder cellText = new StringBuilder();
            // Copies all lines after header since cell pattern sometimes spans multiple lines
            for (int i = headerIndex + 1; i < rleText.size(); i++) {
                cellText.append(rleText.get(i));
            }

            // Retrieve dimensions based on header info - [height, width]
            int[] dimensions = findDimensions(header, pattern, matcher);

            // Account for errors in RLE file
            if (dimensions == null) {
                // System.out.println("Input invalid.");
                return null;
            }

            // Create new grid of the specified dimensions
            newGrid = new int[dimensions[1]][dimensions[0]];
            // Create array of Strings representing rows of grid
            String[] gridRows = cellText.toString().split("\\$");
            int rowNum = 0; // will be used to access gridRow index

            for (int row = 0; row < dimensions[1]; row++) {
                String nextLine = gridRows[rowNum];
                int column = -1; // tracks which column in the grid we are up to

                // Parse out all characters from each line
                for (int i = 0; i < nextLine.length(); i++) {
                    matcher = pattern.matcher(nextLine);
                    int numCells = 1;
                    Character symbol = 'x';
                    if (matcher.find() && matcher.start() == 0) // if the integer is at the first index of the line
                    {
                        numCells = Integer.parseInt(matcher.group());
                        int endIndex = matcher.end();
                        if (nextLine.length() > endIndex) {
                            nextLine = nextLine.substring(endIndex); // remove from line
                            symbol = nextLine.charAt(0); // isolate the next character and identify it
                        } else {
                            nextLine = "";
                            symbol = '$';
                        }
                        i = 0; // start from the beginning again in case number is 2+ digits
                    } else if (nextLine.length() > 0) {
                        symbol = nextLine.charAt(0);
                    }

                    // Set cells alive if symbol is o
                    int runs = column + numCells; // will always run at least once
                    while (column < runs) {
                        column++;
                        if (symbol == 'o') {
                            newGrid[row][column] = 1;
                        } else if (symbol == '$') {
                            row += (numCells - 1);
                            break;
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
                rowNum++;
            }
        } else {
            return null;
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

    public ArrayList<String> removeComments(ArrayList<String> txt) {
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
        int index = -1;
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
            int comma2 = header.lastIndexOf(",") > yIndex ? header.lastIndexOf(",") : header.length();

            // Isolate height statement
            x = header.substring(xIndex, comma1);
            // Isolate width statement
            y = header.substring(yIndex, comma2);
        } else {
            // System.out.println("Error with dimension input.");
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
