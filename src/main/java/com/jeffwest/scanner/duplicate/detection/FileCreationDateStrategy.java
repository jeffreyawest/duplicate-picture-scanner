package com.jeffwest.scanner.duplicate.detection;

import com.jeffwest.filewrapper.FileWrapper;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/9/12
 * Time: 11:29 AM
 */
public class FileCreationDateStrategy implements DuplicateDetectionStrategy
{

  private static final String FILE = "FILE_";

  @Override
  public String getHash(FileWrapper wrapper) throws IOException
  {
    return FILE + wrapper.getCreatedDateStr();
  }
}
