package com.jeffwest.pruner;

import java.io.File;

/**
 * User: jeffrey.a.west
 * Date: Sep 16, 2011
 * Time: 7:04:36 PM
 */
public class DirectoryPruner
{
  private static final boolean DEBUG = true;

  public static void main(String[] args)
  {
    try
    {
      String PATH = "/Users/jeffreyawest/Pictures";
      File dir = new File(PATH);

      cleanDir(dir);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private static int cleanDir(File dir)
  {
    File[] files = dir.listFiles();
    int fileCount = 0;


    if (files != null)
    {
      for (File file : files)
      {
        if (file.isFile())
        {
          //System.out.println("File found: " + io.getAbsolutePath());
          fileCount += 1;
          continue;
        }

        fileCount += cleanDir(file);
      }
    }

    if (fileCount == 0)
    {
      if (DEBUG)
      {
        System.out.println("Directory has no files: [" + dir.getAbsolutePath() + ']');
      }
      boolean deleted = false;
      deleted = dir.delete();
      if (DEBUG)
      {
        System.out.println("***Deleted [" + deleted + "] directory with no files: [" + dir.getAbsolutePath() + ']');
      }
//      System.exit(1);
    }
    else
    {
      if (DEBUG)
      {
        System.out.println("Directory has [" + fileCount + "] files: [" + dir.getAbsolutePath() + ']');
      }

    }

    return fileCount;
  }
}
