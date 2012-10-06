package com.jeffwest.scanner.duplicate.resolution;

import com.jeffwest.scanner.duplicate.FileSet;
import com.jeffwest.scanner.duplicate.FileSetManager;

import java.util.Collection;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/9/12
 * Time: 12:17 AM
 */
public class DisplayDuplicateSet implements DuplicateResolutionStrategy
{
  @Override
  public void resolveDuplicates(Collection<FileSetManager> fileSetManagers)
  {
    for (FileSetManager fileSetManager : fileSetManagers)
    {
      Collection<Map.Entry<String, FileSet>> duplicateCollection = fileSetManager.getDuplicateSetHashMap().entrySet();

      System.out.println("Detection Strategy: "+fileSetManager.getDetectionStrategy());
      System.out.println("============================================================================================");

      for (Map.Entry<String, FileSet> duplicateSetEntry : duplicateCollection)
      {
        FileSet set = duplicateSetEntry.getValue();
        if (set.getFiles().size() == 1)
        {
          continue;
        }

        String hash = duplicateSetEntry.getKey();

        System.out.println("Hash=[" + hash + "] Count=[" + set.getFiles().size() + "] Size=[" + set.getSpace() + ']');
      }
      System.out.println("============================================================================================");
      System.out.println("");
      System.out.println("");
    }
  }

  @Override
  public void resolveDuplicates(FileSetManager fileSetManager)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}