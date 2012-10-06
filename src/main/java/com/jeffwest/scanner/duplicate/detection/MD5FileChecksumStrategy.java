package com.jeffwest.scanner.duplicate.detection;

import com.jeffwest.scanner.Constants;
import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.scanner.checksum.ChecksumTool;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 10:32 PM
 */
public class MD5FileChecksumStrategy implements DuplicateDetectionStrategy
{
  private static final String MD5 = "MD5_";
  private int bufferSize;

  public MD5FileChecksumStrategy()
  {
    bufferSize = Constants.ONE_MB * 2;
  }

  public MD5FileChecksumStrategy(int bufferSize)
  {
    this.bufferSize = bufferSize;
  }

  @Override
  public String getHash(FileWrapper wrapper) throws IOException
  {
    return MD5 + ChecksumTool.getMD5Checksum(wrapper.getTheFile(), bufferSize);
  }
}
