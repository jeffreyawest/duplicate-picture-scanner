package com.jeffwest.scanner.duplicate.detection;

import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.filewrapper.JPEGWrapper;
import com.jeffwest.scanner.ImageProperties;
import com.jeffwest.scanner.ImageUtils;
import com.jeffwest.scanner.checksum.ChecksumTool;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 10:32 PM
 */
public class JPEGImageChecksumStrategy implements DuplicateDetectionStrategy
{

  private static final String JPEG_CRC32 = "JPEG_CRC32_";

  @Override
  public String getHash(FileWrapper wrapper)
  {
    String hash = null;

    if (wrapper instanceof JPEGWrapper)
    {
      try
      {
        ImageProperties props = ImageUtils.getJpegProperties(wrapper, FileWrapper.TWO_MB);
        return JPEG_CRC32 + ChecksumTool.getCRC32Checksum(props.imageBytes);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

    return null;
  }
}
