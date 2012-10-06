package com.jeffwest.scanner.duplicate;

import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.filewrapper.JPEGWrapper;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jeffrey.a.west
 * Date: Oct 16, 2010
 * Time: 6:42:50 PM
 */
public class FileSet extends Observable
{
  private static final String MODIFIED = "MODIFIED";
  private static final String ORIGINALS = "ORIGINALS";
  private static final String IPHOTO = "IPHOTO";
  private Set<FileWrapper> files;

  private long space;
  private FileWrapper biggestFile;
  private String hash;

  public FileSet()
  {
    files = new HashSet<>(31);
  }

  public boolean hasDuplicates()
  {
    return files.size() > 1;
  }

  public FileSet(FileWrapper file1, FileWrapper file2)
  {
    this.space = file1.getTheFile().length();

    files.add(file1);
    files.add(file2);
  }

  public void addFile(FileWrapper pFileWrapper)
  {
    files.add(pFileWrapper);
    space += pFileWrapper.length();

    if (biggestFile == null)
    {
      biggestFile = pFileWrapper;
    }
    else
    {
      if (biggestFile.length() < pFileWrapper.length())
      {
        biggestFile = pFileWrapper;
      }
    }

    if (pFileWrapper instanceof JPEGWrapper)
    {
      JPEGWrapper jpeg = (JPEGWrapper) pFileWrapper;
      jpeg.getPixelCount();
    }

    setChanged();
    notifyObservers(pFileWrapper);
  }

  public String toString()
  {
    return getHash();
  }

  public Set<FileWrapper> getFiles()
  {
    return files;
  }

  public FileWrapper getFile1()
  {
    return files.iterator().next();
  }

  public long getSpace()
  {
    return space;
  }

  public FileWrapper selectBestFile()
  {
    FileWrapper currentBest = null;

    for (FileWrapper wrapper : files)
    {
      if (!(wrapper instanceof JPEGWrapper))
      {
        continue;
      }

      JPEGWrapper compareToJPEG = (JPEGWrapper) wrapper;

      String pathUPPER = compareToJPEG.getAbsolutePath().toUpperCase();

      if (pathUPPER.contains(MODIFIED)
          || pathUPPER.contains(ORIGINALS)
          || pathUPPER.contains(IPHOTO))
      {
        continue;
      }

      if (currentBest == null
          || wrapper.length() > currentBest.length()
          || compareToJPEG.getPixelCount() > ((JPEGWrapper) currentBest).getPixelCount())
      {
        currentBest = compareToJPEG;
      }

      if (!currentBest.getAbsolutePath().equals(currentBest.getAbsolutePath()))
      {
        if (((JPEGWrapper) currentBest).hasGPS())
        {
          continue;
        }
        else if (compareToJPEG.hasGPS())
        {
          currentBest = compareToJPEG;
        }
      }

    }

    if (currentBest == null)
    {
      for (FileWrapper wrapper : files)
      {
        if (!(wrapper instanceof JPEGWrapper))
        {
          continue;
        }

        JPEGWrapper compareToJPEG = (JPEGWrapper) wrapper;

        String pathUPPER = compareToJPEG.getAbsolutePath().toUpperCase();

        if (pathUPPER.contains(MODIFIED)
            || pathUPPER.contains(ORIGINALS)
            || pathUPPER.contains(IPHOTO))
        {
          continue;
        }

        if (currentBest == null
            || wrapper.length() > currentBest.length()
            || compareToJPEG.getPixelCount() > ((JPEGWrapper) currentBest).getPixelCount())
        {
          currentBest = compareToJPEG;
        }
      }
    }

    if (currentBest == null)
    {
      currentBest = biggestFile;
    }

    return currentBest;
  }

  public FileWrapper getBiggestFile()
  {
    return biggestFile;
  }

  public long size()
  {
    return files.size();
  }

  public void setHash(String hash)
  {
    this.hash = hash;
  }

  public String getHash()
  {
    return hash;
  }
}
