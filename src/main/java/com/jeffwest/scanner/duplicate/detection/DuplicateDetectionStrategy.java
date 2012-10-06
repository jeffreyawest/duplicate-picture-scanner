package com.jeffwest.scanner.duplicate.detection;

import com.jeffwest.filewrapper.FileWrapper;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 10:31 PM
 */
public interface DuplicateDetectionStrategy
{
  public String getHash(FileWrapper wrapper) throws IOException;
}
