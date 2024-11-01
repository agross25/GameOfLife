package gross.gameoflife;

import gross.gameoflife.grid.Grid;
import gross.gameoflife.gui.GridComponent;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class GridControllerTest {

    @Test
    void startTimer() {
        // given
        Grid model = mock();
        GridComponent view = mock();
        GridController controller = new GridController(model, view);

        // when

    }

    @Test
    void stopTimer() {
        // given
        Grid model = mock();
        GridComponent view = mock();
        GridController controller = new GridController(model, view);
    }

    @Test
    void paste() {
    }

    @Test
    void toggleCell() {
        // given
        Grid model = mock();
        GridComponent view = mock();
        GridController controller = new GridController(model, view);
        doReturn(100).when(view).getHeight();
        doReturn(100).when(view).getWidth();

        // when
        controller.toggleCell(50, 100);

        // then
        verify(model).setCellAlive(5, 10);
        verify(view).repaint();
    }
}