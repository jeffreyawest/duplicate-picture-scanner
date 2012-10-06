package com.jeffwest.fx.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/13/12
 * Time: 10:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class FXMLTester extends Application
{
  public static void main(String[] args)
  {
    launch();
  }

  @Override
  public void start(Stage stage) throws Exception
  {
    URL url = new URL("file:/Users/jeffreyawest/Data/mycode/sandbox/checksum-standalone/src/com/jeffwest/fx/controller/mainGrid.fxml");

    Scene root = (Scene) FXMLLoader.load(url);

//    Parent root = FXMLLoader.load(new URL("file:/Users/jeffreyawest/Data/mycode/sandbox/checksum-standalone/src/starter.fxml"));

    for (Node node : root.getRoot().getChildrenUnmodifiable())
    {
      System.out.println(node.getClass());
    }

    stage.setTitle("FXML Welcome");
    stage.setScene(root);
    stage.show();
  }
}
