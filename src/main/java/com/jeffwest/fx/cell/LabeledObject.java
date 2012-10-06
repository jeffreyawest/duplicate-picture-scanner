package com.jeffwest.fx.cell;

import javafx.beans.property.SimpleStringProperty;

public final class LabeledObject
{
  private SimpleStringProperty name;
  private Object obj;

  public LabeledObject(final Object pObject, final String pLabel)
  {
    obj = pObject;

    if (pLabel != null)
    {
      name = new SimpleStringProperty(pLabel);
    }
    else if (obj != null)
    {
      name = new SimpleStringProperty(obj.toString());
    }
    else
    {
      name = new SimpleStringProperty("BLANK");
    }
  }

  public Object getObject()
  {
    return obj;
  }

  public String toString()
  {
    return name.getValue();
  }
}

  