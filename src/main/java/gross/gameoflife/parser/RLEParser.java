package gross.gameoflife.parser;

import gross.gameoflife.grid.Grid;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RLEParser {

    String filepath;
    BufferedReader reader;

    // Constructor
    public RLEParser(String pathname) {
        filepath = pathname;
        try {
            reader = new BufferedReader(new FileReader(filepath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Main method for parsing - calls other methods
    public void parseFile() {
        // Access text from file
        ArrayList<String> rleText = this.getText();

        // Eliminate comments above header
        rleText = clean(rleText);

        // Identify header and cell text
        String header = new String(), cellText = new String();
        for (int i = 0; i < rleText.size(); i++) {
            String str = rleText.get(i).toLowerCase(); // in case of accidental capital X
            if (str.startsWith("x =")) {
                header = str;
                cellText = rleText.get(i + 1);
                break;
            }
        }

        // Retrieve dimensions based on header - [height, width]
        int[] dimensions = findDimensions(header);

        // Account for errors in RLE file
        if (dimensions == null) {
            System.out.println("Input invalid.");
            System.exit(1);
        }

        // Create new grid of the specified dimensions and print
        Grid newGrid = new Grid(dimensions[0], dimensions[1]);
        System.out.println("\nDimensions: " + dimensions[0] + " X " + dimensions[1]);

        // Parse line cell patterns into ArrayList
//        String o = "o"; // live cell
//        String b = "b"; // dead cell
//        String $ = "$"; // new line

        String[] gridRows = cellText.split("$");

        System.out.println("Cell Pattern:");
        for (int row = 0; row < gridRows.length; row++) {
            String nextLine = gridRows[row];
            System.out.println(nextLine);
            int column = 0;
            // Uses regex to find integers
            Pattern pattern = Pattern.compile("\\d+");

            while (column < newGrid.getWidth()) {
                Matcher matcher = pattern.matcher(nextLine);
                if (matcher.find() && matcher.start() == 0) // if the integer is at the first index of the line
                {
                    int num = Integer.parseInt(matcher.group());
                    nextLine = nextLine.substring(matcher.end() + 1); // remove from line
                    System.out.println(num);
                } else {
                    // Isolate the next character and identify its meaning
                    String symbol = nextLine.substring(0, 1);
                    nextLine = nextLine.substring(1); // remove from line
                    System.out.println(symbol);
                }
                column++;
            }
        }

        for (String str : message)
            System.out.println(str);
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
        // Identify the index before header begins
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

    public int[] findDimensions(String header) {
        // Declare dimensions array and strings to hold height and width expressions
        int[] dimens = new int[2];
        String x = new String(), y = new String();

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
        // Regular expression vars to find a number in the string
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher;
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

    public static void main(String[] args) {
        String pathname = "/Users/adinagross/Library/Mobile Documents/com~apple~CloudDocs/Touro/MCON/MCON357-Practicum_in_Software_Dev/GameOfLife/RLEfiles/1beacon.rle";
        RLEParser parser = new RLEParser(pathname);
        parser.parseFile();
    }

}
