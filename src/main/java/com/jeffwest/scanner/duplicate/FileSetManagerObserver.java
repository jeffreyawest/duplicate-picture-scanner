package com.jeffwest.scanner.duplicate;

import com.jeffwest.scanner.Constants;

import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/9/12
 * Time: 11:12 AM
 */
public class FileSetManagerObserver implements Observer
{
  FileSet biggestFileSet;
  FileSet mostDuplicatedFileSet;

  @Override
  public void update(Observable o, Object arg)
  {
    if (arg instanceof FileSet && o instanceof FileSetManager)
    {
      FileSet set = (FileSet) arg;
      FileSetManager manager = (FileSetManager) o;

    }
  }

  public FileSet getBiggestFileSet()
  {
    return biggestFileSet;
  }

  public FileSet getMostDuplicatedFileSet()
  {
    return mostDuplicatedFileSet;
  }
}
