package com.jeffwest.fx.resolver;

import com.jeffwest.filewrapper.JPEGWrapper;
import com.jeffwest.scanner.duplicate.FileSetManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/15/12
 * Time: 1:47 PM
 */
public class FileSetManagerResolverWindow
{
  private FileSetManager manager;
  private JPEGWrapper preferredImage;

  public FileSetManagerResolverWindow(FileSetManager pFileSetManager)
  {
    manager = pFileSetManager;

    Group group = new Group();
    VBox outerVbox = new VBox(10);
    group.getChildren().add(outerVbox);

    Text title = new Text(manager.getDetectionStrategy().getClass().getSimpleName());
    outerVbox.getChildren().add(title);

    Label countLabel = new Label("Duplicate Count: ");
    Text txtDuplicatesCount = new Text("0");

    HBox hbDuplicateCount = new HBox(10);
    hbDuplicateCount.getChildren().addAll(countLabel, txtDuplicatesCount);

    Label spaceLabel = new Label("Duplicate Space: ");
    Text txtDuplicatesSpace = new Text("0");

    HBox hbDuplicateSpace = new HBox(10);
    hbDuplicateSpace.getChildren().addAll(spaceLabel, txtDuplicatesSpace);

    Label lblFileSets = new Label("Duplicate Sets:");
    Text txtFileSets = new Text("0");

    HBox hboxDuplicateSets = new HBox();
    hboxDuplicateSets.getChildren().addAll(lblFileSets, txtFileSets);

    HBox hbProgress = new HBox();
    Text txtFilesTotal = new Text(String.valueOf(""));
    Text txtFilesProcessed = new Text("0");
    ProgressBar progressBar = new ProgressBar(0);

    hbProgress.getChildren().addAll(txtFilesProcessed, new Text("/"), txtFilesTotal, progressBar);

    outerVbox.getChildren().addAll(hbDuplicateCount, hbDuplicateSpace, hboxDuplicateSets, hbProgress);

    Button resolveButton = new Button("Resolve");
    resolveButton.setVisible(false);
    resolveButton.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent pActionEvent)
      {
        new FileSetManagerResolverWindow(manager);
      }
    });

    outerVbox.getChildren().add(resolveButton);
  }

}
