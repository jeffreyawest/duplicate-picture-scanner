package com.jeffwest.scanner.duplicate.resolution;

import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.filewrapper.JPEGWrapper;
import com.jeffwest.scanner.duplicate.FileSet;
import com.jeffwest.scanner.duplicate.FileSetManager;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 10:45 PM
 */
public class DeleteDuplicateStrategy implements DuplicateResolutionStrategy
{
  @Override
  public void resolveDuplicates(Collection<FileSetManager> fileSetManagers)
  {
    for (FileSetManager fileSetManager : fileSetManagers)
    {
      resolveDuplicates(fileSetManager);
    }
  }

  @Override
  public void resolveDuplicates(FileSetManager fileSetManager)
  {
    for (FileSet fileSet : fileSetManager.getDuplicateSetHashMap().values())
    {
      if (!fileSet.hasDuplicates())
      {
        continue;
      }
      System.out.println("============");

      FileWrapper bestFile = fileSet.selectBestFile();

      for (FileWrapper wrapper : fileSet.getFiles())
      {
        if (bestFile.equals(wrapper))
        {
          System.out.println("Keeping selected best file: " + wrapper.getAbsolutePath());
          continue;
        }

        boolean deleted = false;
        deleted = wrapper.getTheFile().delete();

        System.out.println("Deleted=[" + deleted + "] file=[" + wrapper.getTheFile().getAbsolutePath() + ']');
      }
    }
  }
}
