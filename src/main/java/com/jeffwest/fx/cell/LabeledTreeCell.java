package com.jeffwest.fx.cell;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;

public final class LabeledTreeCell extends TreeCell<LabeledObject>
{
  private TextField textField;

  public LabeledTreeCell()
  {
    super();
  }

  public Object getTargetObject()
  {
    if(getItem() == null)
    {
      return null;
    }

    return getItem().getObject();
  }

  @Override
  protected void updateItem(final LabeledObject pItem, final boolean empty)
  {
    // calling super here is very important - don't skip this!
    super.updateItem(pItem, empty);

    if (!empty)
    {
      setText(pItem.toString());
    }
  }
}