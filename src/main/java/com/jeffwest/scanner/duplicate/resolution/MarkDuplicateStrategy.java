package com.jeffwest.scanner.duplicate.resolution;

import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.filewrapper.JPEGWrapper;
import com.jeffwest.scanner.duplicate.FileSet;
import com.jeffwest.scanner.duplicate.FileSetManager;

import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/10/12
 * Time: 8:27 PM
 */
public class MarkDuplicateStrategy implements DuplicateResolutionStrategy
{
  @Override
  public void resolveDuplicates(Collection<FileSetManager> fileSetManagers)
  {
    System.out.println("File Managers: " + fileSetManagers.size());

    for (FileSetManager fileSetManager : fileSetManagers)
    {
      Set<FileSet> fileSets = fileSetManager.getSizeOrderedDuplicateSet();

      System.out.println("File sets: " + fileSets.size());

      for (FileSet fileSet : fileSets)
      {
        if (!fileSet.hasDuplicates())
        {
          continue;
        }

        String caption = "DUPLICATE-" + fileSet.getHash();

        if (fileSet.getFile1() instanceof JPEGWrapper)
        {
          Set<FileWrapper> files = fileSet.getFiles();

          for (FileWrapper file : files)
          {
            if (file instanceof JPEGWrapper)
            {
              JPEGWrapper jpeg = (JPEGWrapper) file;

//              jpeg.setCaptionField(caption);
              jpeg.addCaptionField(caption);
            }
          }
        }
      }
    }
  }
//
//  @Override
//  public void resolveDuplicates(Collection<FileSetManager> fileSetManagers)
//  {
//    System.out.println("File Managers: " + fileSetManagers.size());
//
//    for (FileSetManager fileSetManager : fileSetManagers)
//    {
//      Set<FileSet> fileSets = fileSetManager.getSizeOrderedDuplicateSet();
//
//      System.out.println("File sets: " + fileSets.size());
//
//      for (FileSet fileSet : fileSets)
//      {
//        if (fileSet.hasDuplicates() && fileSet.getFile1() instanceof JPEGWrapper)
//        {
//          String caption = "DUPLICATE-" + fileSet.getHash();
//          JPEGWrapper.setCaptionFieldsBatch(caption, fileSet);
//        }
//      }
//    }
//  }

  @Override
  public void resolveDuplicates(FileSetManager fileSetManager)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
