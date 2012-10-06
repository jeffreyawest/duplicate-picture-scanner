package com.jeffwest.filewrapper;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/15/12
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class DummyFileWrapper extends FileWrapper
{
  public DummyFileWrapper(File theFile)
  {
    super(theFile);
  }

  public String toString()
  {
    return "NOOP";
  }
}
