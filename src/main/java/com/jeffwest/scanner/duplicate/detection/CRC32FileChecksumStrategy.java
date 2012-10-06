package com.jeffwest.scanner.duplicate.detection;

import com.jeffwest.scanner.Constants;
import com.jeffwest.scanner.checksum.ChecksumTool;
import com.jeffwest.filewrapper.FileWrapper;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 10:32 PM
 */
public class CRC32FileChecksumStrategy implements DuplicateDetectionStrategy
{
  private static final String CRC32 = "CRC32_";
  private int bufferSize;

  public CRC32FileChecksumStrategy()
  {
    bufferSize = Constants.ONE_MB * 4;
  }

  public CRC32FileChecksumStrategy(int bufferSize)
  {
    this.bufferSize = bufferSize;
  }

  @Override
  public String getHash(FileWrapper wrapper) throws IOException
  {
    //733556
    return CRC32 + ChecksumTool.getCRC32Checksum(wrapper.getTheFile(), bufferSize);


//    return CRC32 + ChecksumTool.checksumMappedFile(wrapper.getTheFile());
  }
}
