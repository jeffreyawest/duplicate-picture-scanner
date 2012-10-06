package com.jeffwest.fx.results;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.filewrapper.JPEGWrapper;
import com.jeffwest.fx.FXUtil;
import com.jeffwest.fx.cell.LabeledObject;
import com.jeffwest.fx.cell.LabeledTreeCell;
import com.jeffwest.fx.resolver.DuplicateResolverWindow;
import com.jeffwest.scanner.duplicate.FileSet;
import com.jeffwest.scanner.duplicate.FileSetManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/15/12
 * Time: 5:28 PM
 */
public class DuplicateResultsTab
{
  private static final ColumnConstraints COLUMN_1_CONSTRAINTS = new ColumnConstraints();
  private static final ColumnConstraints COLUMN_2_CONSTRAINTS = new ColumnConstraints();
  private static final Map<String, Label> directoryLabels;
  private static final Map<String, Label> tagLabels;
  private static final Map<FileWrapper, Image> imageCache;
  private static final Font TAHOMA_BOLD_14 = Font.font("Tahoma", FontWeight.BOLD, 14);
  private static final Insets INSETS = new Insets(5, 5, 5, 5);
  private static final Font TAHOMA_BOLD_12 = Font.font("Tahoma", FontWeight.BOLD, 12);
  private static final double THUMB_HEIGHT = 300;
  private static final double THUMB_WIDTH = 300;
  private static final String RESOLVE_DUPLICATES = "Resolve Duplicates";

  private FileSetManager manager;
  private Node rootNode;

  static
  {
    directoryLabels = new HashMap<>(67);
    tagLabels = new HashMap<>(67);
    imageCache = new HashMap<>(67);

    COLUMN_1_CONSTRAINTS.setHgrow(Priority.NEVER);
    COLUMN_1_CONSTRAINTS.setMinWidth(300);

    COLUMN_2_CONSTRAINTS.setHgrow(Priority.NEVER);
    COLUMN_2_CONSTRAINTS.setMaxWidth(300);
  }

  public DuplicateResultsTab(FileSetManager pManager)
  {
    manager = pManager;

    final SplitPane mainPane = new SplitPane();
    mainPane.setOrientation(Orientation.HORIZONTAL);

    final SplitPane leftPane = new SplitPane();
    leftPane.setOrientation(Orientation.VERTICAL);

    rootNode = mainPane;

    final StackPane leftTop = new StackPane();
    final BorderPane right = new BorderPane();
    final ScrollPane leftBottom = new ScrollPane();
    leftBottom.setFitToWidth(true);

    final TreeItem<LabeledObject> treeRoot = new TreeItem<>(new LabeledObject(null, "root"));
    treeRoot.setExpanded(true);

    for (FileSet set : manager.getAlphaOrderedDuplicateSet())
    {
      LabeledObject setLabel = new LabeledObject(set, set.getHash());
      TreeItem<LabeledObject> hashItem = new TreeItem<>(setLabel);
      hashItem.setExpanded(false);
      treeRoot.getChildren().add(hashItem);

      for (FileWrapper file : set.getFiles())
      {
        LabeledObject fileLabel = new LabeledObject(file, null);

        TreeItem<LabeledObject> fileItem = new TreeItem<>(fileLabel);

        hashItem.getChildren().add(fileItem);
      }
    }

    final TreeView<LabeledObject> treeView = new TreeView<>();
    treeView.setEditable(false);

    treeView.setId("TopTreeView");

    treeView.setCellFactory(new Callback<TreeView<LabeledObject>, TreeCell<LabeledObject>>()
    {
      @Override
      public TreeCell<LabeledObject> call(TreeView<LabeledObject> pLabeledFileWrapperTreeView)
      {
        final LabeledTreeCell cell = new LabeledTreeCell();
        cell.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
          @Override
          public void handle(MouseEvent pMouseEvent)
          {
            if (pMouseEvent.getClickCount() == 2)
            {
              if (cell.getTargetObject() instanceof FileSet)
              {
                final FileSet set = (FileSet) cell.getTargetObject();
                new DuplicateResolverWindow(set);
              }
            }

            if (cell.getTargetObject() instanceof JPEGWrapper)
            {
              JPEGWrapper wrapper = (JPEGWrapper) cell.getTargetObject();
              Image image = getImage(wrapper);
              ImageView imageView;
              imageView = getMainImageView(image);
              imageView.setFitHeight(right.getHeight());
              imageView.setFitWidth(right.getWidth());
              right.setCenter(imageView);

              VBox metadataVbox = new VBox(10);
              leftBottom.setContent(metadataVbox);

              Metadata metadata = null;

              try
              {
                metadata = wrapper.getMetadata();

                for (Directory directory : metadata.getDirectories())
                {
                  Label lblDirName = getDirectoryLabel(directory);

                  lblDirName.setFont(TAHOMA_BOLD_14);
                  lblDirName.setUnderline(true);
                  metadataVbox.getChildren().add(lblDirName);

                  GridPane directoryGrid = new GridPane();
                  directoryGrid.setPadding(INSETS);
                  directoryGrid.setGridLinesVisible(true);

                  int rowNumber = 0;

                  for (Tag tag : directory.getTags())
                  {
                    Label lblTagName;
                    lblTagName = getTagLabel(directory, tag);
                    lblTagName.setFont(TAHOMA_BOLD_12);

                    Text txtTagValue = new Text(directory.getDescription(tag.getTagType()));
                    txtTagValue.setWrappingWidth(300);

                    directoryGrid.add(lblTagName, 0, rowNumber);
                    directoryGrid.add(txtTagValue, 1, rowNumber);

                    rowNumber++;
                  }

                  metadataVbox.getChildren().add(directoryGrid);
                }
              }
              catch (Exception e)
              {
                e.printStackTrace();
              }
            }

            if (cell.getTargetObject() instanceof FileSet)
            {
              final FileSet set = (FileSet) cell.getTargetObject();
              FlowPane flow = new FlowPane();
              flow.setHgap(10);
              flow.setVgap(10);
              right.setCenter(flow);

              for (FileWrapper wrapper : set.getFiles())
              {
                if (wrapper instanceof JPEGWrapper)
                {
                  Image image = getImage(wrapper);
                  ImageView imageView = getThumbImageView(image);
                  flow.getChildren().add(imageView);
                }
              }

              VBox fileSetStats = new VBox(10);

              HBox hboxHash = FXUtil.getLabelHbox("Hash: ", set.getHash());
              HBox hboxCount = FXUtil.getLabelHbox("Count: ", String.valueOf(set.getFiles().size()));
              HBox hboxSpace = FXUtil.getLabelHbox("Duplicated Space: ", String.valueOf(set.getSpace()));

              Button btnResolveDuplicates = new Button(RESOLVE_DUPLICATES);
              btnResolveDuplicates.setOnAction(new EventHandler<ActionEvent>()
              {
                @Override
                public void handle(ActionEvent pActionEvent)
                {
                  new DuplicateResolverWindow(set);
                }
              });

              fileSetStats.getChildren().addAll(hboxHash, hboxCount, hboxSpace, btnResolveDuplicates);

              leftBottom.setContent(fileSetStats);
            }
          }
        });

        return cell;
      }

    });


//    treeView.setOnMouseClicked(new EventHandler<MouseEvent>()
//    {
//      @Override
//      public void handle(MouseEvent pMouseEvent)
//      {
//        System.out.println("Source=[" + pMouseEvent.getSource()
//                           + "] Target=[" + pMouseEvent.getTarget() + ']');
//      }
//    });
//
    treeView.setShowRoot(false);
    treeView.setRoot(treeRoot);

    leftTop.getChildren().add(treeView);

    leftPane.getItems().addAll(leftTop, leftBottom);
    mainPane.getItems().addAll(leftPane, right);

  }

  private static Label getDirectoryLabel(Directory pDirectory)
  {
    String name = pDirectory.getName();
//    Label label = directoryLabels.get(name);
    Label label = new Label("Directory: " + name);

    if (label == null)
    {
      label = new Label("Directory: " + name);
      directoryLabels.put(name, label);
    }

    return label;
  }

  private static ImageView getMainImageView(Image pImage)
  {
    ImageView imageView;
    imageView = new ImageView(pImage);
    imageView.setPreserveRatio(true);

    return imageView;
  }

  private static ImageView getImageView(Image pImage)
  {
    ImageView imageView;

    imageView = new ImageView(pImage);

    imageView.setPreserveRatio(true);

    return imageView;
  }

  private static ImageView getThumbImageView(Image pImage)
  {
    ImageView imageView;

    imageView = new ImageView(pImage);

    imageView.setPreserveRatio(true);
    imageView.setFitHeight(THUMB_HEIGHT);
    imageView.setFitWidth(THUMB_WIDTH);

    return imageView;
  }

  private static Image getImage(FileWrapper wrapper)
  {
    Image image;
    image = imageCache.get(wrapper);

    if (image == null)
    {
      try
      {
        image = new Image("file://"+wrapper.getAbsolutePath());
      }
      catch (Exception e)
      {
        e.printStackTrace();
        System.out.println("Error creating image with file: " + wrapper.getAbsolutePath());
      }
    }

    return image;
  }

  private static Label getTagLabel(Directory directory, Tag tag)
  {
    String name = directory.getTagName(tag.getTagType());
    return new Label(name + ": ");
  }

  public Node getRootNode()
  {
    return rootNode;
  }


}
