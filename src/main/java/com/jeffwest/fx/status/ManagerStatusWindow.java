package com.jeffwest.fx.status;

import com.jeffwest.fx.results.DuplicateResultsWindow;
import com.jeffwest.scanner.RecursiveFileScannerObservable;
import com.jeffwest.scanner.duplicate.DuplicateScanner;
import com.jeffwest.scanner.duplicate.FileSet;
import com.jeffwest.scanner.duplicate.FileSetManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/15/12
 * Time: 4:20 PM
 */
public class ManagerStatusWindow
    implements Observer
{
  private Collection<FileSetManager> managers;
  private Collection<FileSet> duplicateSets;
  private Collection<ManagerStatusRow> rows;

  private DuplicateScanner scanner;
  private Group group;
  private Node rootNode;

  private double dblTotalFileCount;
  private double dblFilesProcessed;

  private ProgressBar progressBar;
  private Button btnShowResults;

  private Text txtDuplicatesCount;
  private Text txtFileSets;
  private Text txtDuplicatesSpace;
  private Text txtFilesTotal;
  private Text txtFilesProcessed;
  private Button resolveButton;
  private Stage stage;

  public ManagerStatusWindow(final double pWindowX,
                             final double pWindowY,
                             final DuplicateScanner pScanner,
                             final double pPDblTotalFileCount
                            )
  {
    scanner = pScanner;
    scanner.getScannerObservable().addObserver(this);

    dblTotalFileCount = pPDblTotalFileCount;

    rows = new HashSet<>(pScanner.getManagers().size());
    group = new Group();

    rootNode = new GridPane();
    group.getChildren().add(rootNode);
    GridPane grid = (GridPane) rootNode;

    grid.add(new Text("Scanning Files..."), 0, 0);

    int currentRow = 1;

    for (FileSetManager manager : pScanner.getManagers())
    {
      ManagerStatusRow statusRow = new ManagerStatusRow(manager, pPDblTotalFileCount);
      rows.add(statusRow);
      grid.add(statusRow.getRootNode(), 0, currentRow++);
    }

    VBox statusVbox = new VBox(10);
    HBox hbProgress = new HBox();

    txtFilesTotal = new Text(String.valueOf(dblTotalFileCount));
    txtFilesProcessed = new Text("0");
    progressBar = new ProgressBar(0);

    hbProgress.getChildren().addAll(txtFilesProcessed, new Text("/"), txtFilesTotal);
    statusVbox.getChildren().addAll(progressBar, hbProgress);

    grid.add(statusVbox, 0, currentRow++);

    btnShowResults = new Button("Show Results");
    btnShowResults.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent pActionEvent)
      {
        stage.hide();
        new DuplicateResultsWindow(scanner);
      }
    });

    btnShowResults.setDisable(true);

    grid.add(btnShowResults, 0, currentRow++);


    stage = new Stage(StageStyle.UTILITY);
    stage.setTitle("Managers running...");
    stage.setX(pWindowX);
    stage.setY(pWindowY);

    Scene newScene = new Scene(group);
    stage.setScene(newScene);

    stage.show();
  }

  @Override
  public void update(Observable o, Object arg)
  {
    if (o instanceof RecursiveFileScannerObservable)
    {
      dblFilesProcessed++;
      txtFilesProcessed.setText(String.valueOf(dblFilesProcessed));

      double dblProgress = dblFilesProcessed / dblTotalFileCount;

      progressBar.setProgress(dblProgress);

      if(dblProgress >= 1.0)
      {
        btnShowResults.setDisable(false);
      }
    }
  }

  public void hide()
  {
    stage.hide();
  }

  public void show()
  {
    stage.show();
  }
}
