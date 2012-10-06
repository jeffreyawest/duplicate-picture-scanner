package com.jeffwest.fx.cell;

import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.filewrapper.JPEGWrapper;
import com.sun.imageio.plugins.jpeg.JPEG;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/16/12
 * Time: 10:50 AM
 */
public final class FileWrapperListCell extends ListCell<FileWrapper>
{
  private TextField textField;

  public FileWrapperListCell()
  {
    super();
  }

  @Override
  protected void updateItem(final FileWrapper pItem, final boolean empty)
  {
    super.updateItem(pItem, empty);

    if (!empty)
    {
      setText(pItem.toString());
    }
  }
}
