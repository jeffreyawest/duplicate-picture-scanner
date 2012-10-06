package com.jeffwest.fx.resolver;

import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.filewrapper.JPEGWrapper;
import com.jeffwest.fx.cell.FileWrapperListCell;
import com.jeffwest.scanner.duplicate.FileSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/16/12
 * Time: 10:24 AM
 */
public class DuplicateResolverWindow
{
  private static final String SET_TO_PREFERED_IMAGE = "Set to Prefered Image";
  private static final String DELETE = "Delete";
  private static final String IMAGE_INFORMATION = "Image Information";
  private static final String PATH = "Path: ";
  private static final String SIZE = "Size: ";
  private static final String RESOLUTION = "Resolution: ";
  private static final String HAS_GPS = "Has GPS?";
  private static final String DUPLICATE_RESOLVER = "Duplicate Resolver";
  private FileSet fileSet;

  private BorderPane outerBorderPane;
  private SplitPane imageSplitPane;

  private JPEGWrapper preferredFile;
  private JPEGWrapper compareFile;

  private StackPane compareStackPane;
  private StackPane preferredStackPane;
  private Stage stage;

  private ObservableList<FileWrapper> jpegList;

  public DuplicateResolverWindow(final FileSet pFileSet)
  {
    fileSet = pFileSet;
    outerBorderPane = new BorderPane();
    outerBorderPane.setLeft(buildLeftPane());
    imageSplitPane = new SplitPane();
    outerBorderPane.setCenter(imageSplitPane);
    preferredStackPane = new StackPane();
    compareStackPane = new StackPane();
    imageSplitPane.getItems().addAll(preferredStackPane, compareStackPane);

    updatePreferredImage((JPEGWrapper) pFileSet.selectBestFile(), 0);

    getNewCompareImage();


    Screen screen = Screen.getPrimary();
    Rectangle2D bounds = screen.getVisualBounds();

    stage = new Stage();
    stage.setTitle(DUPLICATE_RESOLVER);

    stage.setX(bounds.getMinX());
    stage.setY(bounds.getMinY());

    stage.setWidth(bounds.getMaxX());
    stage.setHeight(bounds.getMaxY());

    stage.setScene(new Scene(outerBorderPane));
    stage.show();
  }

  private void getNewCompareImage()
  {
    for (FileWrapper fileWrapper : jpegList)
    {
      if (!fileWrapper.equals(preferredFile))
      {
        updateCompareImage((JPEGWrapper) fileWrapper, 0);
        break;
      }
    }
  }

  private Node buildLeftPane()
  {
    StackPane pane = new StackPane();
    ListView<FileWrapper> list = new ListView<>();

    list.setCellFactory(new Callback<ListView<FileWrapper>, ListCell<FileWrapper>>()
    {
      @Override
      public ListCell<FileWrapper> call(ListView<FileWrapper> pJPEGWrapperListView)
      {
        final ListCell<FileWrapper> cell = new FileWrapperListCell();

        cell.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
          @Override
          public void handle(MouseEvent pMouseEvent)
          {
            updateCompareImage((JPEGWrapper) cell.getItem(), 0);
          }
        });

        return cell;
      }
    });

    jpegList = FXCollections.observableArrayList(fileSet.getFiles());

    list.setItems(jpegList);

    pane.getChildren().add(list);

    return pane;
  }

  private void updatePreferredImage(final JPEGWrapper pWrapper, final double pRotate)
  {
    preferredStackPane.getChildren().clear();

    if (pWrapper != null)
    {
      preferredFile = pWrapper;
      Node node = buildImagePane(pWrapper, pRotate, true);
      preferredStackPane.getChildren().add(node);
    }
  }

  private void updateCompareImage(final JPEGWrapper pWrapper, final double pDouble)
  {
    compareStackPane.getChildren().clear();

    if (pWrapper != null && !pWrapper.equals(preferredFile))
    {
      compareFile = pWrapper;
      Node node = buildImagePane(pWrapper, pDouble, false);
      compareStackPane.getChildren().add(node);
    }
  }

  private Node buildImagePane(final JPEGWrapper pJPEGWrapper, double pRotate, final boolean pPreferred)
  {
    final ScrollPane scrollPane = new ScrollPane();
    final Image image = new Image("file://" + pJPEGWrapper.getAbsolutePath());
    final ImageView imageView = new ImageView(image);
    final BorderPane outerPane = new BorderPane();

    outerPane.setPrefSize(600, 600);
    outerPane.setCenter(imageView);

    imageView.setPreserveRatio(true);
    imageView.setRotate(pRotate);
    imageView.setFitWidth(500);
    imageView.setFitWidth(500);

    scrollPane.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent pKeyEvent)
      {
        if (pKeyEvent.getCode() == KeyCode.SPACE)
        {
          getNewCompareImage();
        }
        else if (pKeyEvent.getCode() == KeyCode.R)
        {
          if (!pPreferred)
          {
            updateCompareImage(pJPEGWrapper, imageView.getRotate() + 90);
          }
          else
          {
            updatePreferredImage(pJPEGWrapper, imageView.getRotate() + 90);
          }
        }
      }
    });

    GridPane imageInfo = new GridPane();
    ColumnConstraints col1Constraints = new ColumnConstraints();
    col1Constraints.setPercentWidth(25);
    ColumnConstraints col2Constraints = new ColumnConstraints();
    col2Constraints.setPercentWidth(75);

    int currentRow = 0;

    Button btnKeep = new Button("KEEP");
    btnKeep.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent pActionEvent)
      {
        jpegList.remove(pJPEGWrapper);

        if (jpegList.size() <= 1)
        {
          stage.close();
        }
        else if (pPreferred)
        {
          getNewPreferredImage();
        }
        else
        {
          getNewCompareImage();
        }
      }
    });

    Button btnDelete = new Button("DELETE");
    btnDelete.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent pActionEvent)
      {
        pJPEGWrapper.getTheFile().delete();

        jpegList.remove(pJPEGWrapper);

        if (jpegList.size() <= 1)
        {
          stage.close();
        }
        else if (pPreferred)
        {
          getNewPreferredImage();
        }
        else
        {
          getNewCompareImage();
        }

      }
    });

    imageInfo.add(btnKeep, 0, currentRow);
    imageInfo.add(btnDelete, 1, currentRow);
    currentRow++;

    imageInfo.add(new Label(IMAGE_INFORMATION), 0, currentRow, 2, 1);
    currentRow++;

    Label lblFilename = new Label("Filename: ");
    Text txtFilename = new Text(pJPEGWrapper.getTheFile().getName());

    imageInfo.add(lblFilename, 0, currentRow);
    imageInfo.add(txtFilename, 1, currentRow);
    currentRow++;

    Label lblName = new Label(PATH);
    TextArea txtName = new TextArea(pJPEGWrapper.getTheFile().getParent());
    txtName.setPrefRowCount(2);
    txtName.setWrapText(true);
    txtName.setEditable(false);

    imageInfo.add(lblName, 0, currentRow);
    imageInfo.add(txtName, 1, currentRow);

    Label lblSize = new Label(SIZE);
    Text txtSize = new Text(String.valueOf(pJPEGWrapper.length()));
    currentRow++;
    imageInfo.add(lblSize, 0, currentRow);
    imageInfo.add(txtSize, 1, currentRow);

    Label lblResolution = new Label(RESOLUTION);
    Text txtResolution = new Text(pJPEGWrapper.getResolution());
    currentRow++;
    imageInfo.add(lblResolution, 0, currentRow);
    imageInfo.add(txtResolution, 1, currentRow);

    Label lblHasGPS = new Label(HAS_GPS);
    Text txtHasGPS = new Text(String.valueOf(pJPEGWrapper.hasGPS()));
    currentRow++;
    imageInfo.add(lblHasGPS, 0, currentRow);
    imageInfo.add(txtHasGPS, 1, currentRow);

    outerPane.setBottom(imageInfo);
    scrollPane.setContent(outerPane);
    scrollPane.setVisible(true);

    ContextMenu compareWindowContext = new ContextMenu();
    scrollPane.setContextMenu(compareWindowContext);

    MenuItem itemDelete = new MenuItem(DELETE);
    itemDelete.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent pActionEvent)
      {
        boolean deleted;
        deleted = pJPEGWrapper.getTheFile().delete();
        System.out.println("Deleted: " + deleted);

        jpegList.remove(pJPEGWrapper);

        if (jpegList.size() == 1)
        {
          stage.close();
        }
        else
        {
          if (pPreferred)
          {
            getNewPreferredImage();
          }

          getNewCompareImage();
        }
      }
    });

    compareWindowContext.getItems().add(itemDelete);

    if (!pPreferred)
    {
      MenuItem itemSetPreferred = new MenuItem(SET_TO_PREFERED_IMAGE);
      itemSetPreferred.setOnAction(new EventHandler<ActionEvent>()
      {
        @Override
        public void handle(ActionEvent pActionEvent)
        {
          updatePreferredImage(pJPEGWrapper, 0);
          getNewCompareImage();
        }
      });
      compareWindowContext.getItems().add(itemSetPreferred);
    }

    return scrollPane;
  }

  private void getNewPreferredImage()
  {
    preferredStackPane.getChildren().clear();

    for (FileWrapper fileWrapper : jpegList)
    {
      if (!fileWrapper.equals(preferredFile) && !fileWrapper.equals(compareFile))
      {
        updatePreferredImage((JPEGWrapper) fileWrapper, 0);
        break;
      }
    }
  }
}


