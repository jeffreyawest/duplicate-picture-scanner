package com.jeffwest.scanner.duplicate.resolution;

import com.jeffwest.scanner.duplicate.FileSetManager;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 10:45 PM
 */
public interface DuplicateResolutionStrategy
{
  public void resolveDuplicates(Collection<FileSetManager> fileSetManagers);

  public void resolveDuplicates(FileSetManager fileSetManager);

}
