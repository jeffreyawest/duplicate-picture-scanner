package com.jeffwest.fx.controller;

import com.jeffwest.scanner.duplicate.DuplicateScanner;
import com.jeffwest.scanner.duplicate.detection.FilePrefixStrategy;
import com.jeffwest.scanner.duplicate.detection.JPEGImageChecksumStrategy;
import com.jeffwest.scanner.duplicate.detection.JPEGFileCreatedDateStrategy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/13/12
 * Time: 10:43 PM
 */
public class MainSceneController implements Initializable
{
  private Set<String> detectionStrategies = new HashSet<>(7);

  private Stage primaryStage;

  @FXML
  private Text directoryName;

  @FXML
  private Button btnPerformScan;

  @FXML
  private CheckBox chkFileDatePrefix;

  @FXML
  private CheckBox chkJPEGImageData;

  @FXML
  private CheckBox chkJPEGCreatedDate;

  @Override
  public void initialize(URL pURL, ResourceBundle pResourceBundle)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @FXML
  protected void processSelect(ActionEvent event)
  {
    System.out.println(chkJPEGCreatedDate == null);
    if (event.getSource() instanceof CheckBox)
    {
      CheckBox box = (CheckBox) event.getSource();

      if (box.isSelected())
      {
        detectionStrategies.add(box.getId());
      }
      else
      {
        detectionStrategies.remove(box.getId());
      }
    }
  }

  public CheckBox getChkFileDatePrefix()
  {
    return chkFileDatePrefix;
  }

  public CheckBox getChkJPEGImageData()
  {
    return chkJPEGImageData;
  }

  public CheckBox getChkJPEGCreatedDate()
  {
    return chkJPEGCreatedDate;
  }

  public void clickChooseDir()
  {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setInitialDirectory(new File("/Users/jeffreyawest/Pictures"));
    directoryName.setText(chooser.showDialog(primaryStage).getAbsolutePath());

    System.out.println("Chose dir: " + directoryName);
  }

  public void clickPerformScan()
  {
    DuplicateScanner scanner = new DuplicateScanner();

    if (chkJPEGCreatedDate.isSelected())
    {
      scanner.addDetectionStrategy(new JPEGFileCreatedDateStrategy());
    }

    if (chkJPEGImageData.isSelected())
    {
      scanner.addDetectionStrategy(new JPEGImageChecksumStrategy());
    }

    if (chkFileDatePrefix.isSelected())
    {
      scanner.addDetectionStrategy(new FilePrefixStrategy());
    }

    scanner.setStartDir(directoryName.getText());

    HashMap<FileSetManagerController, Stage> managerStages = new HashMap<>();

    Screen screen = Screen.getPrimary();
    Rectangle2D bounds = screen.getVisualBounds();

    primaryStage.setX(bounds.getMinX());
    primaryStage.setY(bounds.getMinY());


    Stage stage = new Stage();
    stage.setTitle("Many st");
    stage.setScene(new Scene(new Group(), 260, 230, Color.LIGHTCYAN));
    stage.show();

  }
}
