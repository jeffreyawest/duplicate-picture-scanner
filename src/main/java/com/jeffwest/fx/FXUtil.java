package com.jeffwest.fx;

import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/15/12
 * Time: 5:32 PM
 */
public class FXUtil
{
  public static Stage getFullScreenStage(String pTitle)
  {
    Screen screen = Screen.getPrimary();
    Rectangle2D bounds = screen.getVisualBounds();

    Stage stage = new Stage();
    stage.setTitle(pTitle);
    stage.setX(bounds.getMaxX() / 2);
    stage.setY(bounds.getMinY());
    stage.setWidth(bounds.getMaxX());
    stage.setHeight(bounds.getMaxY());

    return stage;
  }

  public static HBox getLabelHbox(String s1, String s2)
  {
    HBox hbox = new HBox();
    Label label = new Label(s1);
    label.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
    Text text = new Text(s2);
    hbox.getChildren().addAll(label, text);

    return hbox;
  }

  public static void dumpEvent(Event pEvent)
  {
    System.out.println("Event Class=[" + pEvent.getClass().getSimpleName()
                       + "] Source=[" + pEvent.getSource()
                       + "] Target=[" + pEvent.getTarget()
                       + "]");
  }


}
