package com.jeffwest.scanner.duplicate.detection;

import com.jeffwest.filewrapper.FileWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 10:31 PM
 */
public class FilenameStrategy implements DuplicateDetectionStrategy
{

  private static final String FILENAME = "FILENAME_";

  @Override
  public String getHash(FileWrapper wrapper)
  {
    return FILENAME + wrapper.getTheFile().getName();
  }
}
