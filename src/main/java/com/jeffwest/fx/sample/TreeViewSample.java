package com.jeffwest.fx.sample;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TreeViewSample extends Application {

  private final Node rootIcon = new ImageView(
      new Image(getClass().getResourceAsStream("folder_16.png"))
  );
  private final ToggleGroup group = new ToggleGroup();
  TreeItem[] securityItems = new TreeItem[10];
  TreeItem[] serverItems = new TreeItem[6];

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Tree View Sample");

    TreeItem<String> rootItem = new TreeItem<String>("Settings", rootIcon);
    rootItem.setExpanded(true);
    TreeItem<String> securityItem = new TreeItem<String> ("Security");
    securityItem.setExpanded(true);
    TreeItem<String> serverItem = new TreeItem<String> ("Server");

    for (int i = 1; i < 9; i++) {
      TreeItem<CheckBox> item = securityItems[i] =
          new TreeItem<CheckBox> (new CheckBox("Option" + i));
    }
    securityItem.getChildren().addAll(securityItems);

    for (int i = 1; i < 5; i++) {
      RadioButton rb = new RadioButton("Option" + i);
      rb.setToggleGroup(group);
      TreeItem<RadioButton> item = serverItems[i] =
          new TreeItem<RadioButton> (rb);
    }
    serverItem.getChildren().addAll(serverItems);
    rootItem.getChildren().addAll(securityItem, serverItem);

    TreeView tree = new TreeView(rootItem);

    StackPane root = new StackPane();
    root.getChildren().add(tree);
    primaryStage.setScene(new Scene(root, 300, 250));
    primaryStage.show();
  }
}