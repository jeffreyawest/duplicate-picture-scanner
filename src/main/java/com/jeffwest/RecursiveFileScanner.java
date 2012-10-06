package com.jeffwest;

import com.jeffwest.filewrapper.FileWrapper;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 10:53 PM
 */
public class RecursiveFileScanner
{

  public HashMap<String, FileWrapper> processDirectory(File pTopDir)
  {
    HashMap<String, FileWrapper> directoryChecksums;

    File[] files = pTopDir.listFiles(new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.isFile();
      }
    });

    directoryChecksums = new HashMap<String, FileWrapper>(files.length);

    System.out.println("Scanning files=[" + files.length + "] directory=[" + pTopDir.getAbsolutePath() + ']');

    for (File file : files)
    {
      if (file.length() <= 0 || !file.exists())
      {
        continue;
      }

      FileWrapper wrapper = FileWrapper.getFileWrapper(file);
      processFileWrapper(wrapper, false);
      directoryChecksums.put(wrapper.getHash(), wrapper);
    }

    File[] directories = pTopDir.listFiles(new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.isDirectory();
      }
    });

    for (File directory : directories)
    {
      directoryChecksums.putAll(processDirectory(directory));
    }

    return directoryChecksums;
  }

  public static final void processFileWrapper(final FileWrapper file, boolean quiet)
  {
    try
    {
      if (file.length() == 0)
      {
        return;
      }

      final String checksum = file.getHash();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

}
