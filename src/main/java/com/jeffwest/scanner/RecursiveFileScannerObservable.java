package com.jeffwest.scanner;

import com.jeffwest.filewrapper.FileWrapper;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 10:53 PM
 */
public class RecursiveFileScannerObservable extends Observable
{
  private ArrayList<FileWrapper> files;
  private FilenameFilter filenameFilter;
  private boolean debug = true;

  public RecursiveFileScannerObservable(int pSize)
  {
    files = new ArrayList<>(pSize);
  }

  public RecursiveFileScannerObservable()
  {
    this(16000);
  }

  public RecursiveFileScannerObservable(FilenameFilter pFilenameFilter)
  {
    this();
    filenameFilter = pFilenameFilter;
  }

  public void scanDirectory(String pDirPath)
  {
    processDirectory(new File(pDirPath));
    if (debug)
    {
      System.out.println(this.getClass() + " processed File Count=[" + files.size() + ']');
    }
  }

  private void processDirectory(final File pDirectory)
  {
    File[] files;

    if (filenameFilter != null)
    {
      files = pDirectory.listFiles(filenameFilter);
    }
    else
    {
      files = pDirectory.listFiles();
    }

    if (files != null)
    {
      if (debug)
      {
        System.out.println("Scanning files/dirs=[" + files.length + "] directory=[" + pDirectory.getAbsolutePath() + ']');
      }

      for (File file : files)
      {
        if (file.isDirectory())
        {
          processDirectory(file);
        }
        else
        {
          processFile(file);
        }
      }
    }
  }

  private void processFile(final File pFile)
  {
    FileWrapper wrapper = FileWrapper.getFileWrapper(pFile);
    files.add(wrapper);
    setChanged();
    notifyObservers(wrapper);
  }

  public boolean isDebug()
  {
    return debug;
  }

  public void setDebug(boolean pDebug)
  {
    debug = pDebug;
  }
}
