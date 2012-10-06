package com.jeffwest.fx.resolver;


import com.jeffwest.filewrapper.JPEGWrapper;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;

public class ImageChooser extends Application
{
  public static double MAX_HEIGHT = 768;
  public static double MAX_WIDTH = 1024;
  public static double SIZE_RATIO = 1.5;

  private VBox mainVbox;
  private VBox image1Vbox;
  private VBox image2Vbox;

  private HBox imagesHBox;

  private ImageView image1View;
  private ImageView image2View;

  private ImageView image1Viewport;
  private ImageView image2Viewport;

  private JPEGWrapper wrapper1;
  private JPEGWrapper wrapper2;

  private String imagePath1;
  private String imagePath2;

  public ImageChooser()
  {
  }


  public ImageChooser(JPEGWrapper pWrapper1, JPEGWrapper pWrapper2)
  {
    wrapper1 = pWrapper1;
    wrapper2 = pWrapper2;
  }

  private void choose()
  {
    launch();
  }

  private void init(Stage primaryStage)
  {
    Group root = new Group();
    primaryStage.setScene(new Scene(root));
    //we can set image properties directly during creation

    String image1URL = "file:/" + imagePath1;
    //image 1
    image1View = getImageView("file:/" + imagePath1, primaryStage);
    image1Viewport = getImageViewport(image1URL);

    image1View.setOnMouseClicked(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent pMouseEvent)
      {
        keepImage(imagePath1);
        System.out.println("Image 1 clicked!");
      }
    });

    image1Vbox = new VBox();
    image1Vbox.getChildren().addAll(image1Viewport, image1View);

    //image 2
    String image2URL = "file:/" + imagePath2;
    image2View = getImageView(image2URL, primaryStage);
    image2Viewport = getImageViewport(image2URL);

    image2View.setOnMouseClicked(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent pMouseEvent)
      {
        keepImage(imagePath1);
        System.out.println("Image 2 clicked!");
      }
    });

    image2Vbox = new VBox();
    image2Vbox.setSpacing(10);
    image2Vbox.getChildren().addAll(image2Viewport, image2View);

    imagesHBox = new HBox();
    imagesHBox.setSpacing(10);
    imagesHBox.getChildren().addAll(image1Vbox, image2Vbox);

    mainVbox = new VBox(10);
    mainVbox.getChildren().add(imagesHBox);

    //show the layout
    root.getChildren().add(mainVbox);
  }

  private void keepImage(String pImagePath1)
  {
    //To change body of created methods use File | Settings | File Templates.
  }

  private ImageView getImageViewport(String pImageURL)
  {
    Image image = new Image(pImageURL);
    ImageView image1Viewport = new ImageView(image);
    image1Viewport.setFitHeight(60);
    image1Viewport.setPreserveRatio(true);

    //viewport is used for displaying the part of image
    Rectangle2D rectangle2D = new Rectangle2D(0, 0, 120, 60);
    image1Viewport.setViewport(rectangle2D);
    return image1Viewport;
  }

  private ImageView getImageView(String pImageURL, Stage primaryStage)
  {
    Image image = new Image(pImageURL);
    ImageView imageView = new ImageView(image);

    Screen.getPrimary().getVisualBounds().getMaxX();

    MAX_WIDTH = Screen.getPrimary().getVisualBounds().getMaxX();
    MAX_HEIGHT = Screen.getPrimary().getVisualBounds().getMaxY();

    if (image.getHeight() > MAX_HEIGHT / SIZE_RATIO)
    {
      imageView.setFitHeight(MAX_HEIGHT / SIZE_RATIO);
      imageView.setPreserveRatio(true);
    }
    else if (image.getWidth() > MAX_WIDTH / SIZE_RATIO)
    {
      imageView.setFitWidth(MAX_WIDTH / SIZE_RATIO);
      imageView.setPreserveRatio(true);
    }

    imageView.setSmooth(true);
    return imageView;
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    init(primaryStage);
    primaryStage.show();
  }

  public static void main(String[] args)
  {
    String filename1 = "/Users/jeffreyawest/Pictures/PIX-FINAL/Honeymoon/Honeymoon/20100506_050837_IMG_1210.JPG";
    String filename2 = "/Users/jeffreyawest/Pictures/PIX-FINAL/Honeymoon/20100506_050837_IMG_1210.JPG";

    JPEGWrapper jpeg1 = new JPEGWrapper(new File(filename1));
    JPEGWrapper jpeg2 = new JPEGWrapper(new File(filename2));

    ImageChooser chooser = new ImageChooser(jpeg1, jpeg2);
    chooser.choose();

  }
}