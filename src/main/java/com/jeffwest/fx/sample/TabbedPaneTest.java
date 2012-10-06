package com.jeffwest.fx.sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/15/12
 * Time: 3:02 PM
 */
public class TabbedPaneTest extends Application
{
  @Override
  public void start(Stage primaryStage) throws Exception
  {
    primaryStage.setTitle("http://java-buddy.blogspot.com/");
    Group root = new Group();
    Scene scene = new Scene(root, 400, 300, Color.WHITE);

    TabPane tabPane = new TabPane();
    BorderPane mainPane = new BorderPane();

    //Create Tabs
    Tab tabA = new Tab();
    tabA.setText("Tab A");

    SplitPane splitPane = new SplitPane();
    VBox left = new VBox(10);
    left.getChildren().add(new Text("Jeff West1"));
    left.getChildren().add(new Text("Jeff West2"));
    left.getChildren().add(new Text("Jeff West3"));

    VBox right = new VBox(10);
    right.getChildren().addAll(new Text("Julie West1"));
    right.getChildren().addAll(new Text("Julie West2"));
    right.getChildren().addAll(new Text("Julie West3"));

    splitPane.getItems().addAll(left, right);

    tabA.setContent(splitPane);

    tabPane.getTabs().add(tabA);

    Tab tabB = new Tab();
    tabB.setText("Tab B");
    tabPane.getTabs().add(tabB);

    Tab tabC = new Tab();
    tabC.setText("Tab C");
    tabPane.getTabs().add(tabC);

    mainPane.setCenter(tabPane);

    mainPane.prefHeightProperty().bind(scene.heightProperty());
    mainPane.prefWidthProperty().bind(scene.widthProperty());

    root.getChildren().add(mainPane);
    primaryStage.setScene(scene);
    primaryStage.show();

  }

  public static void main(String[] args)
  {
    launch(args);
  }
}
