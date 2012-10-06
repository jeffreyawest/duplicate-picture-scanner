package com.jeffwest.fx.status;

import com.jeffwest.fx.resolver.FileSetManagerResolverWindow;
import com.jeffwest.scanner.duplicate.FileSet;
import com.jeffwest.scanner.duplicate.FileSetManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
public class ManagerScannerWindow implements Observer
{
  private FileSetManager manager;
  private Group group;
  private VBox outerVbox;
  private double totalFileCount;
  private double filesProcessed;

  private Set<FileSet> duplicateSets;
  private ProgressBar progressBar;
  private Text txtDuplicatesCount;
  private Text txtFileSets;
  private Text txtDuplicatesSpace;
  private Text txtFilesTotal;
  private Text txtFilesProcessed;
  private Button resolveButton;

  public ManagerScannerWindow(final double pWindowX,
                              final double pWindowY,
                              final FileSetManager pManager,
                              final long pTotalFileCount)
  {
    duplicateSets = new HashSet<>((int) pTotalFileCount);

    totalFileCount = pTotalFileCount;
    manager = pManager;
    manager.addObserver(this);

    group = new Group();
    outerVbox = new VBox(10);
    group.getChildren().add(outerVbox);

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

    Label lblFileSets = new Label("Duplicate Sets:");
    txtFileSets = new Text("0");

    HBox hboxDuplicateSets = new HBox();
    hboxDuplicateSets.getChildren().addAll(lblFileSets, txtFileSets);

    HBox hbProgress = new HBox();
    txtFilesTotal = new Text(String.valueOf(totalFileCount));
    txtFilesProcessed = new Text("0");
    progressBar = new ProgressBar(0);

    hbProgress.getChildren().addAll(txtFilesProcessed, new Text("/"), txtFilesTotal, progressBar);

    outerVbox.getChildren().addAll(hbDuplicateCount, hbDuplicateSpace, hboxDuplicateSets, hbProgress);

    resolveButton = new Button("Resolve");
    resolveButton.setVisible(false);
    resolveButton.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent pActionEvent)
      {
        new FileSetManagerResolverWindow(pManager);
      }
    });

    outerVbox.getChildren().add(resolveButton);

    Stage stage = new Stage(StageStyle.UTILITY);
    stage.setTitle("Manager: " + manager.getDetectionStrategy().getClass().getSimpleName());
    stage.setX(pWindowX);
    stage.setY(pWindowY);

    Scene newScene = new Scene(group, 300, 300);
    stage.setScene(newScene);

    stage.show();
  }

  @Override
  public void update(Observable o, Object arg)
  {
    if (o instanceof FileSetManager)
    {
      final FileSetManager manager = (FileSetManager) o;

      filesProcessed = manager.getFilesProcessed();
      txtFilesProcessed.setText(String.valueOf(filesProcessed));
      txtFileSets.setText(String.valueOf(manager.getFileSetSize()));
      txtDuplicatesCount.setText(String.valueOf(manager.getDuplicateCount()));
      txtDuplicatesSpace.setText(String.valueOf(manager.getDuplicateSpace()));

      double dblProgress = filesProcessed / totalFileCount;
      progressBar.setProgress(dblProgress);

      if (dblProgress >= 1.0)
      {
        resolveButton.setVisible(true);
      }
    }
    else
    {
      System.out.println("unexpected udpate: " + o.getClass().getSimpleName());
    }
  }

  public Group getGroup()
  {
    return group;
  }
}