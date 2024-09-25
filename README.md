# GameOfLife README

# Description

GameOfLife is a Java simulation of the famous Conway's Game of Life.
It consists primarily of a grid made of clickable cells.
Cells can be alive, in which case they will light up with a color, or dead.
Cell neighbors are adjacent or immediately diagonal cells. Depending on the
first configuration of living and dead cells, the cells that live and die in
the next generation will depend on the four basic rules of the game:

1. A live cell with 2 or 3 neighbors continues to live into the next generation.
2. A live cell with 1 or 0 neighbors will die of loneliness in the next generation.
3. A live cell with 4+ neighbors will die of overcrowding in the next generation.
4. A dead cell with exactly 3 neighbors comes alive in the next generation.

# Structure

This program is comprised of 3 primary packages:

1. Grid package - contains the Grid class
2. Gui package - contains the GridFrame and GridComponent classes
3. Test package - contains GridTest and GuiTest classes

The test package offers two small test programs that use main methods to test the above classes.
The three primary classes are:

1. Grid Class:

This class represents the grid that is the basis of the game.
It consists of a 2D array of ints, with each element within the 2D array representing a cell.
Elements store a value of 0 when dead and a value of 1 when alive.

2. GridFrame Class:

This class is an extension of JFrame and forms the basis of the GUI for Game Of Life.
It utilizes a BorderLayout with a title on top, a grid in the center, and play and pause buttons at the bottom.
It also contains the functions to deal with button clicks.

3. GridComponent Class:

This class extends JComponent and is implemented by the GridFrame class as a feature of the grid.
The GridComponent allows the program to isolate specific cells when clicked and to act accordingly,
allowing the user to click cells on and off and interchange them between dead and alive states.

# Features

### Grid Class

1. Variables:
    - 2D int array board represents grid
2. Functions:
    - Grid(int height, int weight) Constructor - requires grid height and weight inputs
    - getCellStatus(int row, int column) - returns 1 or 0 depending on what specified cell contains
    - getHeight() - returns int of grid height
    - getWidth() - return int of grid width
    - setCellAlive(int row, int column) - sets specified cell to be alive
    - setCellDead(int row, int column) - sets specified cell to be dead
    - nextGen() - void function that produces next generation
    - findLiveNeighbors() - allows program to find out how many living neighbors a cell has
    - findDeadNeighbors() - allows program to find out how many dead neighbors a cell has
    - toString() - returns a string representation of the grid for testing purposes

### GridFrame Class:

1. Variables
    - Timer to ensure grid updates by the second
2. Functions
    - GridFrame() - constructor sets up grid with default structure
    - Play and Pause Action Listeners to pause and play the game

### GridComponent Class:

1. Variables
    - GameGrid is a grid to refer to the grid in question
2. Functions
    - GridComponent(Grid grid) - constructor with grid as input
    - MouseListener to light up or clear cell when clicked
    - paintComponent() decides what will happen to the cell when clicked

# Installation Instructions

In order to run the project, import the above classes and create a new GridFrame.
Set it to visible and watch the magic happen!
