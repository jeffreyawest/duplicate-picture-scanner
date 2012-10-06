package com.jeffwest.fx.status;

import com.jeffwest.scanner.duplicate.FileSet;
import com.jeffwest.scanner.duplicate.FileSetManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/14/12
 * Time: 6:06 PM
 */
public class ManagerStatusRow implements Observer
{
  private FileSetManager manager;
  private VBox outerVbox;
  private double totalFileCount;
  private double filesProcessed;

  private Set<FileSet> duplicateSets;
  private Text txtDuplicatesCount;
  private Text txtDuplicatesSpace;

  private boolean complete;

  public ManagerStatusRow(final FileSetManager pManager,
                          final double pTotalFileCount)
  {
    duplicateSets = new HashSet<>((int) pTotalFileCount);

    totalFileCount = pTotalFileCount;
    manager = pManager;
    manager.addObserver(this);

    outerVbox = new VBox(10);

    Text title = new Text(manager.getDetectionStrategy().getClass().getSimpleName());
    outerVbox.getChildren().add(title);

    Label countLabel = new Label("Duplicate Count: ");
    txtDuplicatesCount = new Text("0");

    HBox hbDuplicateCount = new HBox(10);
    hbDuplicateCount.getChildren().addAll(countLabel, txtDuplicatesCount);

    Label spaceLabel = new Label("Duplicate Space: ");
    txtDuplicatesSpace = new Text("0");

    HBox hbDuplicateSpace = new HBox(10);
    hbDuplicateSpace.getChildren().addAll(spaceLabel, txtDuplicatesSpace);

    outerVbox.getChildren().addAll(hbDuplicateCount, hbDuplicateSpace);
  }

  @Override
  public void update(Observable o, Object arg)
  {
    if (o instanceof FileSetManager)
    {
      final FileSetManager manager = (FileSetManager) o;

      filesProcessed = manager.getFilesProcessed();
      txtDuplicatesCount.setText(String.valueOf(manager.getDuplicateCount()));
      txtDuplicatesSpace.setText(String.valueOf(manager.getDuplicateSpace()));

      double dblProgress = filesProcessed / totalFileCount;

      if (dblProgress >= 1.0)
      {
        complete = true;
      }
    }
    else
    {
      System.out.println("unexpected udpate: " + o.getClass().getSimpleName());
    }
  }

  public Node getRootNode()
  {
    return outerVbox;
  }

  public boolean isComplete()
  {
    return complete;
  }
}