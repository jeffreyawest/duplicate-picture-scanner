package com.jeffwest.scanner;

import com.jeffwest.filewrapper.FileWrapper;

import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/15/12
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileCounter implements Observer
{
  private String dirPath;
  private long fileCount;

  public long countFiles(String pPath)
  {
    RecursiveFileScannerObservable scanner = new RecursiveFileScannerObservable();
    scanner.addObserver(this);
    scanner.scanDirectory(pPath);
    return fileCount;
  }

  @Override
  public void update(Observable o, Object arg)
  {
    if (arg instanceof FileWrapper)
    {
      fileCount++;
    }
  }
}
