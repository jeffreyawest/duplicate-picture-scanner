package com.jeffwest.scanner.duplicate.resolution;

import com.jeffwest.scanner.duplicate.FileSet;
import com.jeffwest.filewrapper.FileWrapper;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 11:55 PM
 */
public class MoveDuplicateStrategy
{
  private String duplicateDestination;
  private boolean SOUT = false;

  private void moveDuplicate(FileSet fileSet)
  {
    System.out.println("Moving fileSet to [" + duplicateDestination + ']');

    FileWrapper dup = fileSet.getFile1();

    String startingPath = dup.getTheFile().getAbsolutePath();
    String finalPath = duplicateDestination;

    finalPath += ('/' + startingPath.substring(startingPath.indexOf(':') + 1));

    if (SOUT)
    {
      System.out.println("Final Path=[" + finalPath + ']');
    }

    File newFile = new File(finalPath);

    String dirName = newFile.getAbsolutePath().substring(0, newFile.getAbsolutePath().lastIndexOf('/'));
    File newDir = new File(dirName);

    if (SOUT)
    {
      System.out.println("Making directory [" + dirName + ']');
    }

    boolean makeDirs = newDir.mkdirs();
    boolean success = dup.getTheFile().renameTo(newFile);

    if (SOUT)
    {
      System.out.println("MakeDirs=[" + makeDirs + "] Success=[" + success + ']');
    }
  }

}
