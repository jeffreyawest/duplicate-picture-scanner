package com.jeffwest.fx.results;

import com.jeffwest.scanner.duplicate.DuplicateScanner;
import com.jeffwest.scanner.duplicate.FileSetManager;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/15/12
 * Time: 5:25 PM
 */
public class DuplicateResultsWindow
{
  private DuplicateScanner scanner;

  public DuplicateResultsWindow(DuplicateScanner pScanner)
  {
    scanner = pScanner;
    TabPane topPane = new TabPane();

    for (FileSetManager manager : pScanner.getManagers())
    {
      Tab newTab = new Tab(manager.getDetectionStrategy().getClass().getSimpleName());
      newTab.setContent(new DuplicateResultsTab(manager).getRootNode());
      topPane.getTabs().add(newTab);
    }

    Screen screen = Screen.getPrimary();
    Rectangle2D bounds = screen.getVisualBounds();

    Stage stage = new Stage();
    stage.setTitle("Duplicate Results");

    stage.setX(bounds.getMinX());
    stage.setY(bounds.getMinY());

    stage.setWidth(bounds.getMaxX());
    stage.setHeight(bounds.getMaxY());

    stage.setScene(new Scene(topPane));
    stage.show();
  }
}
