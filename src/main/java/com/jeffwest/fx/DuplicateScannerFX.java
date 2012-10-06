package com.jeffwest.fx;

import com.jeffwest.fx.results.DuplicateResultsWindow;
import com.jeffwest.fx.status.ManagerStatusRow;
import com.jeffwest.fx.status.ManagerStatusWindow;
import com.jeffwest.scanner.FileCounter;
import com.jeffwest.scanner.RecursiveFileScannerObservable;
import com.jeffwest.scanner.duplicate.DuplicateScanner;
import com.jeffwest.scanner.duplicate.FileSetManager;
import com.jeffwest.scanner.duplicate.detection.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 11:10 PM
 */
public class DuplicateScannerFX
    extends Application
    implements Observer
{
  private DuplicateScanner scanner;
  private Thread scannerThread;
  private double dblTotalFileCount;
  private double dblFilesProcessed;

  private Stage stage;
  private Button showResults;
  private Text txtFilesProcessed;
  private ProgressBar progressBar;
  private Button btnShowResults;


  public static final String DIRECTORY = "/Users/jeffreyawest/Pictures/PIX-FINAL";

  public static void main(String[] args)
  {
    launch(args);
  }

  public DuplicateScannerFX()
  {
    scanner = new DuplicateScanner();
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    init(primaryStage);
    primaryStage.show();

  }

  private void init(final Stage primaryStage)
  {
    stage = primaryStage;

    final GridPane grid = new GridPane();
    grid.setAlignment(Pos.TOP_LEFT);

//    outerGrid.setGridLinesVisible(true);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setAlignment(Pos.CENTER);
    grid.setPadding(new Insets(25, 25, 10, 25));

    Text scenetitle = new Text("Select Duplicate Detection Strategy");
    scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    scenetitle.setTextAlignment(TextAlignment.LEFT);
    grid.add(scenetitle, 0, 1, 2, 1);

    VBox detectionStrategiesVBox = new VBox(10);
    detectionStrategiesVBox.setAlignment(Pos.CENTER_LEFT);
    grid.add(detectionStrategiesVBox, 0, 2);

    final CheckBox chkFileDatePrefixStrategy = new CheckBox("File Date Prefix");
    final CheckBox chkJPEGDataChecksumStrategy = new CheckBox("JPEG Image Data");
    final CheckBox chkJPEGFileCreatedDateStrategy = new CheckBox("JPEG Created Date");
    chkJPEGFileCreatedDateStrategy.setSelected(true);

    final CheckBox chkMD5FileChecksumStrategy = new CheckBox("MD5 File Checksum");
    final CheckBox chkCRC32FileChecksumStrategy = new CheckBox("CRC32 File Checksum");
    final CheckBox chkFileCreationDateStrategy = new CheckBox("File Creation Date");
    final CheckBox chkFilenameStrategy = new CheckBox("Filename");


    detectionStrategiesVBox.getChildren().addAll(chkFileDatePrefixStrategy,
                                                 chkJPEGDataChecksumStrategy,
                                                 chkJPEGFileCreatedDateStrategy,
                                                 chkMD5FileChecksumStrategy,
                                                 chkCRC32FileChecksumStrategy,
                                                 chkFileCreationDateStrategy,
                                                 chkFilenameStrategy
                                                );
    HBox dirHBox = new HBox(10);
    dirHBox.setAlignment(Pos.TOP_LEFT);
    grid.add(dirHBox, 0, 3, 2, 1);

    Label dirLabel = new Label("Directory: ");
    final TextArea dirName = new TextArea(DIRECTORY);
    dirName.setWrapText(true);
    dirName.setPrefRowCount(3);

    Button btnDirSelect = new Button("...");

    btnDirSelect.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent pActionEvent)
      {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(DIRECTORY));

        dirName.setText(chooser.showDialog(primaryStage).getAbsolutePath());
      }
    });

    dirHBox.getChildren().addAll(dirLabel, dirName, btnDirSelect);


    Button btnBeginScan = new Button("Begin Scan");
    btnBeginScan.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent pActionEvent)
      {
        Button btn = (Button) pActionEvent.getSource();
        btn.setDisable(true);

        if (chkFileDatePrefixStrategy.isSelected())
        {
          scanner.addDetectionStrategy(new FilePrefixStrategy());
        }

        if (chkJPEGDataChecksumStrategy.isSelected())
        {
          scanner.addDetectionStrategy(new JPEGImageChecksumStrategy());
        }

        if (chkJPEGFileCreatedDateStrategy.isSelected())
        {
          scanner.addDetectionStrategy(new JPEGFileCreatedDateStrategy());
        }

        if (chkMD5FileChecksumStrategy.isSelected())
        {
          scanner.addDetectionStrategy(new MD5FileChecksumStrategy());
        }

        if (chkCRC32FileChecksumStrategy.isSelected())
        {
          scanner.addDetectionStrategy(new CRC32FileChecksumStrategy());
        }

        if (chkFileCreationDateStrategy.isSelected())
        {
          scanner.addDetectionStrategy(new FileCreationDateStrategy());
        }

        if (chkFilenameStrategy.isSelected())
        {
          scanner.addDetectionStrategy(new FilenameStrategy());
        }

        dblTotalFileCount = new FileCounter().countFiles(dirName.getText());

        scanner.setStartDir(dirName.getText());

        VBox vbox = new VBox(10);
        HBox hbProgress = new HBox();

        Text txtFilesTotal = new Text(String.valueOf(dblTotalFileCount));
        txtFilesProcessed = new Text("0");

        progressBar = new ProgressBar(0);

        hbProgress.getChildren().addAll(
            new Label("Files: "), txtFilesProcessed, new Text("/"), txtFilesTotal);

        vbox.getChildren().addAll(progressBar, hbProgress);

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

        vbox.getChildren().add(btnShowResults);
        grid.add(vbox, 0, 4);

        scanner.getScannerObservable().addObserver(DuplicateScannerFX.this);
        startScanner();
      }
    });

    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(btnBeginScan);

    grid.add(hbBtn, 0, 5);

    showResults = new Button("Show Results");
    showResults.setDisable(true);
    showResults.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent pActionEvent)
      {
//        statusWindow.hide();
        new DuplicateResultsWindow(scanner);
      }
    });

    Scene scene = new Scene(grid);
    primaryStage.setScene(scene);
  }

  private void startScanner()
  {
    scannerThread = new Thread(scanner, "Main Scanner Thread");
    scannerThread.start();
  }

  @Override
  public void update(Observable o, Object arg)
  {
    if (o instanceof RecursiveFileScannerObservable)
    {
      dblFilesProcessed++;
      txtFilesProcessed.setText(String.valueOf(dblFilesProcessed));

      double dblProgress = dblFilesProcessed / dblTotalFileCount;
//      System.out.println("Progress = " + dblProgress);
      progressBar.setProgress(dblProgress);

      if (dblProgress >= 1.0)
      {
        btnShowResults.setDisable(false);
      }
    }
  }


}
