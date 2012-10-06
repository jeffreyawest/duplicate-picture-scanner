package com.jeffwest.fx.sample; /**
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 */

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;

/**
 * A sample that demonstrates how to resize images and use the Viewport property.
 *
 * @related graphics/images/ImageCreation
 * @resource sanfran.jpg
 * @see javafx.scene.image.Image
 * @see javafx.scene.image.ImageView
 */
public class ImagePropertiesSample extends Application
{
  private static final String imagePath1 = "/Users/jeffreyawest/Pictures/PIX-FINAL/AutoImport/20110317_182754_IMG_1138.JPG";
  private static final String url = "file:" + imagePath1;

//  private static final String url = ImagePropertiesSample.class.getResource("sanfran.jpg").toString();

  private void init(Stage primaryStage)
  {
    Group root = new Group();
    primaryStage.setScene(new Scene(root));

    //we can set image properties directly during creation
    ImageView sample1 = new ImageView(new Image(url, 30, 70, false, true));

    ImageView sample2 = new ImageView(new Image(url));
    //image can be resized to preferred width
    sample2.setFitWidth(200);
    sample2.setPreserveRatio(true);

    ImageView sample3 = new ImageView(new Image(url));
    //image can be resized to preferred height
    sample3.setFitHeight(20);
    sample3.setPreserveRatio(true);

    ImageView sample4 = new ImageView(new Image(url));
    //one can resize image without preserving ratio between height and width
    sample4.setFitWidth(40);
    sample4.setFitHeight(80);
    sample4.setPreserveRatio(false);
    sample4.setSmooth(true); //the usage of the better filter

    ImageView sample5 = new ImageView(new Image(url));
    sample5.setFitHeight(60);
    sample5.setPreserveRatio(true);
    //viewport is used for displaying the part of image
    Rectangle2D rectangle2D = new Rectangle2D(50, 200, 120, 60);
    sample5.setViewport(rectangle2D);

    //add the imageviews to layout
    HBox hBox = new HBox();
    hBox.setSpacing(10);
    hBox.getChildren().addAll(sample1, sample3, sample4, sample5);

    //show the layout
    VBox vb = new VBox(10);
    vb.getChildren().addAll(hBox, sample2);
    root.getChildren().add(vb);
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    init(primaryStage);
    primaryStage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}